const express = require('express');
const cors = require('cors');
const nodemailer = require('nodemailer');

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

app.get('/', (req, res) => {
    res.json({ 
        status: 'ok', 
        message: 'XyraPanel Backend API',
        endpoints: {
            report: 'POST /api/report'
        }
    });
});

app.post('/api/report', async (req, res) => {
    try {
        const { deviceInfo, message, subject, appVersion } = req.body;

        if (!message) {
            return res.status(400).json({ success: false, message: 'Message is required' });
        }

        const transporter = nodemailer.createTransport({
            service: 'gmail',
            auth: {
                user: process.env.SMTP_USER || 'xyraofficialsup@gmail.com',
                pass: process.env.SMTP_PASS
            }
        });

        const emailBody = `
LAPORAN MASALAH XYRAPANEL
========================

Pesan:
${message}

------------------------
INFO PERANGKAT:
${deviceInfo || 'Tidak tersedia'}

App Version: ${appVersion || '1.0'}
Waktu: ${new Date().toLocaleString('id-ID', { timeZone: 'Asia/Jakarta' })}
        `;

        const mailOptions = {
            from: process.env.SMTP_USER || 'xyraofficialsup@gmail.com',
            to: 'xyraofficialsup@gmail.com',
            subject: subject || 'Laporan Masalah XyraPanel',
            text: emailBody
        };

        await transporter.sendMail(mailOptions);

        return res.status(200).json({ 
            success: true, 
            message: 'Laporan berhasil dikirim!' 
        });

    } catch (error) {
        console.error('Email error:', error);
        return res.status(500).json({ 
            success: false, 
            message: 'Gagal mengirim laporan. Silakan coba lagi.' 
        });
    }
});

app.listen(PORT, '0.0.0.0', () => {
    console.log(`XyraPanel Backend running on port ${PORT}`);
});
