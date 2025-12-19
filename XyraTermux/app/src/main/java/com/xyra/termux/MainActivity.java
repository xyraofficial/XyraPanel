package com.xyra.termux;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ScrollView;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btnCommandExecutor, btnSystemInfo, btnInstallTools, btnBackup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(16, 16, 16, 16);
        main.setBackgroundColor(0xFFF5F5F7);
        
        // Header
        TextView header = new TextView(this);
        header.setText("XyraTermux");
        header.setTextSize(32);
        header.setTextColor(0xFF000000);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        main.addView(header);
        
        TextView subheader = new TextView(this);
        subheader.setText("Termux ToolBox");
        subheader.setTextSize(14);
        subheader.setTextColor(0xFF666666);
        main.addView(subheader);
        
        // Spacing
        View spacing = new View(this);
        spacing.setLayoutParams(new LinearLayout.LayoutParams(-1, 32));
        main.addView(spacing);
        
        // Buttons
        btnCommandExecutor = createButton("🔧 Command Executor");
        btnCommandExecutor.setOnClickListener(this);
        main.addView(btnCommandExecutor);
        
        btnSystemInfo = createButton("ℹ️ System Info");
        btnSystemInfo.setOnClickListener(this);
        main.addView(btnSystemInfo);
        
        btnInstallTools = createButton("📦 Install Tools");
        btnInstallTools.setOnClickListener(this);
        main.addView(btnInstallTools);
        
        btnBackup = createButton("💾 Backup");
        btnBackup.setOnClickListener(this);
        main.addView(btnBackup);
        
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
        if (v == btnCommandExecutor) {
            startActivity(new Intent(this, CommandActivity.class));
        } else if (v == btnSystemInfo) {
            startActivity(new Intent(this, SystemInfoActivity.class));
        } else if (v == btnInstallTools) {
            startActivity(new Intent(this, ToolsActivity.class));
        } else if (v == btnBackup) {
            startActivity(new Intent(this, BackupActivity.class));
        }
    }
}
