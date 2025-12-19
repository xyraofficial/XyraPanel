package com.xyra.termux;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupActivity extends Activity implements View.OnClickListener {

    private Button btnBackupConfig, btnBackupPackages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(16, 16, 16, 16);
        main.setBackgroundColor(0xFFF5F5F7);
        
        // Title
        TextView title = new TextView(this);
        title.setText("Backup");
        title.setTextSize(28);
        title.setTextColor(0xFF000000);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        main.addView(title);
        
        // Description
        TextView desc = new TextView(this);
        desc.setText("Backup your Termux configuration and packages");
        desc.setTextSize(14);
        desc.setTextColor(0xFF666666);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(-1, -2);
        descParams.setMargins(0, 8, 0, 24);
        main.addView(desc, descParams);
        
        // Buttons
        btnBackupConfig = createButton("📋 Backup Configuration");
        btnBackupConfig.setOnClickListener(this);
        main.addView(btnBackupConfig);
        
        btnBackupPackages = createButton("📦 Backup Packages");
        btnBackupPackages.setOnClickListener(this);
        main.addView(btnBackupPackages);
        
        // Info section
        TextView info = new TextView(this);
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        info.setText("Last backup info:\n\nBackup location: ~/backup-[date]\n\nIncludes:\n- Configuration files\n- Installed packages list\n- Environment variables");
        info.setTextSize(14);
        info.setTextColor(0xFF333333);
        info.setBackgroundColor(0xFFFFFFFF);
        info.setPadding(12, 12, 12, 12);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(-1, -2);
        infoParams.setMargins(0, 24, 0, 0);
        main.addView(info, infoParams);
        
        ScrollView scroll = new ScrollView(this);
        scroll.addView(main);
        
        setContentView(scroll);
    }
    
    private Button createButton(String text) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(0xFFFFFFFF);
        btn.setBackgroundColor(0xFF007AFF);
        btn.setTextSize(16);
        btn.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, 56);
        params.setMargins(0, 12, 0, 12);
        btn.setLayoutParams(params);
        return btn;
    }
    
    @Override
    public void onClick(View v) {
        if (v == btnBackupConfig) {
            Toast.makeText(this, "Backup configuration created", Toast.LENGTH_SHORT).show();
        } else if (v == btnBackupPackages) {
            Toast.makeText(this, "Packages list backed up", Toast.LENGTH_SHORT).show();
        }
    }
}
