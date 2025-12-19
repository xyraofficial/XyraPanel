package com.xyra.termux;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class CommandActivity extends Activity implements View.OnClickListener {

    private EditText input;
    private TextView output;
    private Button btnExecute, btnOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(16, 16, 16, 16);
        main.setBackgroundColor(0xFFF5F5F7);
        
        // Header
        TextView title = new TextView(this);
        title.setText("Command Executor");
        title.setTextSize(28);
        title.setTextColor(0xFF000000);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        main.addView(title);
        
        // Input
        input = new EditText(this);
        input.setHint("Enter command...");
        input.setMinLines(3);
        input.setBackgroundColor(0xFFFFFFFF);
        input.setPadding(12, 12, 12, 12);
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(-1, -2);
        inputParams.setMargins(0, 16, 0, 16);
        main.addView(input, inputParams);
        
        // Buttons
        LinearLayout btnLayout = new LinearLayout(this);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);
        btnLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        
        btnExecute = createButton("Execute");
        btnExecute.setOnClickListener(this);
        btnLayout.addView(btnExecute, new LinearLayout.LayoutParams(0, 48, 1));
        
        btnOpen = createButton("Open Termux");
        btnOpen.setOnClickListener(this);
        LinearLayout.LayoutParams openParams = new LinearLayout.LayoutParams(0, 48, 1);
        openParams.setMargins(8, 0, 0, 0);
        btnLayout.addView(btnOpen, openParams);
        
        main.addView(btnLayout);
        
        // Output
        output = new TextView(this);
        output.setText("Output will appear here...");
        output.setTextColor(0xFF333333);
        output.setBackgroundColor(0xFFFFFFFF);
        output.setPadding(12, 12, 12, 12);
        LinearLayout.LayoutParams outputParams = new LinearLayout.LayoutParams(-1, -1, 1);
        outputParams.setMargins(0, 16, 0, 0);
        
        ScrollView scrollOutput = new ScrollView(this);
        scrollOutput.addView(output);
        main.addView(scrollOutput, outputParams);
        
        ScrollView scroll = new ScrollView(this);
        scroll.addView(main);
        
        setContentView(scroll);
    }
    
    private Button createButton(String text) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(0xFFFFFFFF);
        btn.setBackgroundColor(0xFF007AFF);
        btn.setTextSize(14);
        btn.setTypeface(null, android.graphics.Typeface.BOLD);
        return btn;
    }
    
    @Override
    public void onClick(View v) {
        if (v == btnExecute) {
            executeCommand();
        } else if (v == btnOpen) {
            openTermux();
        }
    }
    
    private void executeCommand() {
        String cmd = input.getText().toString().trim();
        if (cmd.isEmpty()) {
            Toast.makeText(this, "Enter a command", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            Intent intent = new Intent("com.termux.RUN_COMMAND");
            intent.setPackage("com.termux");
            intent.putExtra("com.termux.RUN_COMMAND", cmd);
            sendBroadcast(intent);
            output.setText("✓ Command sent:\n" + cmd + "\n\nCheck Termux terminal");
            Toast.makeText(this, "Command sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            output.setText("✗ Error: " + e.getMessage());
        }
    }
    
    private void openTermux() {
        try {
            PackageManager pm = getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage("com.termux");
            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Termux not installed", Toast.LENGTH_SHORT).show();
                Intent play = new Intent(Intent.ACTION_VIEW);
                play.setData(Uri.parse("https://f-droid.org/en/packages/com.termux/"));
                startActivity(play);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
