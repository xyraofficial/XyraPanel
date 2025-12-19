package com.xyra.termux;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

    private EditText commandInput;
    private TextView resultText;
    private Button executeButton;
    private Button openTermuxButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create main layout
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(16, 16, 16, 16);
        
        // Title
        TextView titleText = new TextView(this);
        titleText.setText("XyraTermux");
        titleText.setTextSize(24);
        titleText.setTextColor(0xFF000000);
        mainLayout.addView(titleText);
        
        // Subtitle
        TextView subtitle = new TextView(this);
        subtitle.setText("Termux Command Executor");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xFF666666);
        mainLayout.addView(subtitle);
        
        // Command input
        commandInput = new EditText(this);
        commandInput.setHint("Enter Termux command...");
        commandInput.setMinLines(3);
        mainLayout.addView(commandInput);
        
        // Execute button
        executeButton = new Button(this);
        executeButton.setText("Execute");
        executeButton.setOnClickListener(this);
        mainLayout.addView(executeButton);
        
        // Open Termux button
        openTermuxButton = new Button(this);
        openTermuxButton.setText("Open Termux");
        openTermuxButton.setOnClickListener(this);
        mainLayout.addView(openTermuxButton);
        
        // Result display
        resultText = new TextView(this);
        resultText.setText("Output will appear here...");
        resultText.setTextColor(0xFF333333);
        
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(resultText);
        
        LayoutParams scrollParams = new LayoutParams(
            LayoutParams.MATCH_PARENT, 
            LayoutParams.MATCH_PARENT
        );
        scrollParams.weight = 1.0f;
        mainLayout.addView(scrollView, scrollParams);
        
        setContentView(mainLayout);
    }
    
    @Override
    public void onClick(View v) {
        if (v == executeButton) {
            executeCommand();
        } else if (v == openTermuxButton) {
            openTermux();
        }
    }
    
    private void executeCommand() {
        String command = commandInput.getText().toString().trim();
        
        if (command.isEmpty()) {
            Toast.makeText(this, "Please enter a command", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            Intent intent = new Intent();
            intent.setClassName("com.termux", "com.termux.app.RunCommandService");
            intent.setAction("com.termux.RUN_COMMAND");
            intent.putExtra("com.termux.execute", "RUN_COMMAND");
            intent.putExtra("com.termux.RUN_COMMAND", command);
            
            startService(intent);
            resultText.setText("Command: " + command + "\n\nSent to Termux...");
            
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            resultText.setText("Error: " + e.getMessage());
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
                
                Intent playStore = new Intent(Intent.ACTION_VIEW);
                playStore.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.termux"));
                startActivity(playStore);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
