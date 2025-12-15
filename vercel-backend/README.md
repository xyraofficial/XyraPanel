# XyraPanel Backend API

Backend server untuk fitur Laporkan Masalah di XyraPanel.

## Setup di Vercel

1. Push folder ini ke GitHub
2. Import repository di Vercel
3. Set Environment Variables:
   - `SMTP_USER`: xyraofficialsup@gmail.com
   - `SMTP_PASS`: (app password Gmail Anda)
4. Deploy!

## API Endpoint

### POST /api/report

Mengirim laporan masalah via email.

**Request Body:**
```json
{
  "message": "Deskripsi masalah",
  "deviceInfo": "Info perangkat",
  "subject": "Subject email (opsional)",
  "appVersion": "1.0"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Laporan berhasil dikirim!"
}
```

## Environment Variables

| Variable | Description |
|----------|-------------|
| SMTP_USER | Email Gmail untuk mengirim |
| SMTP_PASS | App Password Gmail |
