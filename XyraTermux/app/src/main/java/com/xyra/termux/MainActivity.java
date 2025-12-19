package com.xyra.termux;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText commandInput;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(16, 16, 16, 16);
        
        TextView titleText = new TextView(this);
        titleText.setText("XyraTermux");
        titleText.setTextSize(24);
        titleText.setTextColor(0xFF000000);
        mainLayout.addView(titleText);
        
        TextView subtitle = new TextView(this);
        subtitle.setText("Termux Command Executor");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xFF666666);
        mainLayout.addView(subtitle);
        
        commandInput = new EditText(this);
        commandInput.setHint("Enter Termux command...");
        commandInput.setMinLines(3);
        mainLayout.addView(commandInput);
        
        Button executeButton = new Button(this);
        executeButton.setText("Execute");
        executeButton.setOnClickListener(v -> executeCommand());
        mainLayout.addView(executeButton);
        
        Button openTermuxButton = new Button(this);
        openTermuxButton.setText("Open Termux");
        openTermuxButton.setOnClickListener(v -> openTermux());
        mainLayout.addView(openTermuxButton);
        
        resultText = new TextView(this);
        resultText.setText("Output will appear here...");
        resultText.setTextColor(0xFF333333);
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(resultText);
        mainLayout.addView(scrollView, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 
            LinearLayout.LayoutParams.MATCH_PARENT, 
            1f
        ));
        
        setContentView(mainLayout);
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
            intent.putExtra("com.termux.RUN_COMMAND.RESULT_RECEIVER", new android.os.ResultReceiver(null) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    if (resultCode == 0) {
                        String output = resultData.getString("com.termux.execute.result");
                        resultText.setText("Output:\n" + (output != null ? output : "Command executed"));
                    } else {
                        resultText.setText("Error: Command execution failed");
                    }
                }
            });
            
            startService(intent);
            resultText.setText("Command sent to Termux...");
            
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            resultText.setText("Error: " + e.getMessage());
        }
    }
    
    private void openTermux() {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.termux");
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
