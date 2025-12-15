const nodemailer = require('nodemailer');

module.exports = async (req, res) => {
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

    if (req.method === 'OPTIONS') {
        return res.status(200).end();
    }

    if (req.method !== 'POST') {
        return res.status(405).json({ success: false, message: 'Method not allowed' });
    }

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
};
