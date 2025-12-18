package com.xyra.panel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class LoginActivity extends Activity {

    private static final String PREFS_NAME = "XyraPanelPrefs";
    private static final String KEY_FACE_REGISTERED = "face_registered";
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";
    private static final String KEY_NAME = "XyraFaceKey";
    
    private ImageView ivFaceIcon;
    private TextView tvStatus;
    private TextView tvSubtitle;
    private Button btnAuthenticate;
    private Button btnRegisterFace;
    private View progressIndicator;
    
    private FingerprintManager fingerprintManager;
    private CancellationSignal cancellationSignal;
    private KeyStore keyStore;
    private Cipher cipher;
    private boolean isAuthenticating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        ivFaceIcon = (ImageView) findViewById(R.id.iv_face_icon);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvSubtitle = (TextView) findViewById(R.id.tv_subtitle);
        btnAuthenticate = (Button) findViewById(R.id.btn_authenticate);
        btnRegisterFace = (Button) findViewById(R.id.btn_register_face);
        progressIndicator = findViewById(R.id.progress_indicator);
        
        checkBiometricSupport();
        setupButtons();
        updateUI();
    }
    
    private void checkBiometricSupport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        }
    }
    
    private void setupButtons() {
        btnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBiometricAvailable()) {
                    startBiometricAuthentication();
                } else {
                    showNoBiometricDialog();
                }
            }
        });
        
        btnRegisterFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterFaceDialog();
            }
        });
    }
    
    private void updateUI() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isRegistered = prefs.getBoolean(KEY_FACE_REGISTERED, false);
        
        if (isRegistered) {
            tvStatus.setText("Face ID Terdaftar");
            tvSubtitle.setText("Gunakan wajah Anda untuk membuka XyraPanel");
            btnAuthenticate.setVisibility(View.VISIBLE);
            btnRegisterFace.setText("DAFTAR ULANG FACE ID");
        } else {
            tvStatus.setText("Face ID Belum Terdaftar");
            tvSubtitle.setText("Daftarkan wajah Anda untuk keamanan");
            btnAuthenticate.setVisibility(View.GONE);
            btnRegisterFace.setText("DAFTAR FACE ID");
        }
    }
    
    private boolean isBiometricAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fingerprintManager != null) {
                return fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints();
            }
        }
        return false;
    }
    
    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                    
                keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
                    
                keyGenerator.generateKey();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean initCipher() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
                    
                keyStore.load(null);
                SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private void startBiometricAuthentication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isBiometricAvailable()) {
                showNoBiometricDialog();
                return;
            }
            
            generateKey();
            
            if (initCipher()) {
                showAuthenticationUI(true);
                
                cancellationSignal = new CancellationSignal();
                isAuthenticating = true;
                
                FingerprintManager.CryptoObject cryptoObject = 
                    new FingerprintManager.CryptoObject(cipher);
                    
                fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0,
                    new FingerprintManager.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            isAuthenticating = false;
                            onAuthSuccess();
                        }
                        
                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            onAuthFailed();
                        }
                        
                        @Override
                        public void onAuthenticationError(int errorCode, CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            isAuthenticating = false;
                            onAuthError(errString.toString());
                        }
                    }, null);
            } else {
                Toast.makeText(this, "Gagal inisialisasi biometrik", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void showAuthenticationUI(boolean authenticating) {
        if (authenticating) {
            progressIndicator.setVisibility(View.VISIBLE);
            ivFaceIcon.setAlpha(0.5f);
            tvStatus.setText("Memindai wajah...");
            tvSubtitle.setText("Posisikan wajah Anda di depan kamera");
            btnAuthenticate.setEnabled(false);
        } else {
            progressIndicator.setVisibility(View.GONE);
            ivFaceIcon.setAlpha(1.0f);
            btnAuthenticate.setEnabled(true);
            updateUI();
        }
    }
    
    private void onAuthSuccess() {
        tvStatus.setText("Verifikasi Berhasil!");
        tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        tvSubtitle.setText("Membuka XyraPanel...");
        progressIndicator.setVisibility(View.GONE);
        
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 1000);
    }
    
    private void onAuthFailed() {
        tvStatus.setText("Wajah Tidak Dikenali");
        tvStatus.setTextColor(Color.parseColor("#F44336"));
        tvSubtitle.setText("Coba lagi atau gunakan metode lain");
        
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                showAuthenticationUI(false);
            }
        }, 2000);
    }
    
    private void onAuthError(String error) {
        showAuthenticationUI(false);
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
    }
    
    private void showRegisterFaceDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_register_face);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            window.setAttributes(params);
        }
        
        final ImageView ivDialogFace = (ImageView) dialog.findViewById(R.id.iv_dialog_face);
        final TextView tvDialogStatus = (TextView) dialog.findViewById(R.id.tv_dialog_status);
        final TextView tvDialogInfo = (TextView) dialog.findViewById(R.id.tv_dialog_info);
        final Button btnStartRegister = (Button) dialog.findViewById(R.id.btn_start_register);
        final Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        final View progressRegister = dialog.findViewById(R.id.progress_register);
        
        btnStartRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBiometricAvailable()) {
                    tvDialogStatus.setText("Biometrik Tidak Tersedia");
                    tvDialogStatus.setTextColor(Color.parseColor("#F44336"));
                    tvDialogInfo.setText("Pastikan Face ID/Fingerprint sudah diaktifkan di pengaturan perangkat Anda");
                    return;
                }
                
                progressRegister.setVisibility(View.VISIBLE);
                btnStartRegister.setEnabled(false);
                tvDialogStatus.setText("Memindai wajah...");
                tvDialogInfo.setText("Posisikan wajah Anda di depan kamera");
                
                generateKey();
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && initCipher()) {
                    cancellationSignal = new CancellationSignal();
                    
                    FingerprintManager.CryptoObject cryptoObject = 
                        new FingerprintManager.CryptoObject(cipher);
                        
                    fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0,
                        new FingerprintManager.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);
                                progressRegister.setVisibility(View.GONE);
                                
                                SharedPreferences.Editor editor = 
                                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putBoolean(KEY_FACE_REGISTERED, true);
                                editor.putBoolean(KEY_BIOMETRIC_ENABLED, true);
                                editor.apply();
                                
                                tvDialogStatus.setText("Pendaftaran Berhasil!");
                                tvDialogStatus.setTextColor(Color.parseColor("#4CAF50"));
                                tvDialogInfo.setText("Wajah Anda telah terdaftar dengan sukses");
                                
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        updateUI();
                                        Toast.makeText(LoginActivity.this, 
                                            "Face ID berhasil didaftarkan!", Toast.LENGTH_SHORT).show();
                                    }
                                }, 1500);
                            }
                            
                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                                tvDialogStatus.setText("Gagal Memindai");
                                tvDialogStatus.setTextColor(Color.parseColor("#FF9800"));
                                tvDialogInfo.setText("Coba posisikan wajah Anda dengan lebih baik");
                            }
                            
                            @Override
                            public void onAuthenticationError(int errorCode, CharSequence errString) {
                                super.onAuthenticationError(errorCode, errString);
                                progressRegister.setVisibility(View.GONE);
                                btnStartRegister.setEnabled(true);
                                tvDialogStatus.setText("Error");
                                tvDialogStatus.setTextColor(Color.parseColor("#F44336"));
                                tvDialogInfo.setText(errString.toString());
                            }
                        }, null);
                } else {
                    progressRegister.setVisibility(View.GONE);
                    btnStartRegister.setEnabled(true);
                    tvDialogStatus.setText("Gagal Inisialisasi");
                    tvDialogStatus.setTextColor(Color.parseColor("#F44336"));
                    tvDialogInfo.setText("Tidak dapat menginisialisasi biometrik");
                }
            }
        });
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancellationSignal != null && !cancellationSignal.isCanceled()) {
                    cancellationSignal.cancel();
                }
                dialog.dismiss();
            }
        });
        
        dialog.show();
    }
    
    private void showNoBiometricDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_no_biometric);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            window.setAttributes(params);
        }
        
        Button btnOpenSettings = (Button) dialog.findViewById(R.id.btn_open_settings);
        Button btnSkip = (Button) dialog.findViewById(R.id.btn_skip);
        
        btnOpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
        dialog.show();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (cancellationSignal != null && !cancellationSignal.isCanceled()) {
            cancellationSignal.cancel();
        }
        isAuthenticating = false;
    }
    
    @Override
    public void onBackPressed() {
        if (isAuthenticating) {
            if (cancellationSignal != null && !cancellationSignal.isCanceled()) {
                cancellationSignal.cancel();
            }
            isAuthenticating = false;
            showAuthenticationUI(false);
        } else {
            super.onBackPressed();
        }
    }
}
