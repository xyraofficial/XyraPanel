package com.xyra.termux;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ToolsActivity extends Activity implements View.OnClickListener {

    private CheckBox[] toolCheckboxes;
    private String[] tools = {"Python", "Node.js", "Git", "OpenSSH", "FFmpeg", "Curl", "Wget", "Vim", "Nano", "Htop"};
    private Button btnInstall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(16, 16, 16, 16);
        main.setBackgroundColor(0xFFF5F5F7);
        
        // Title
        TextView title = new TextView(this);
        title.setText("Install Tools");
        title.setTextSize(28);
        title.setTextColor(0xFF000000);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        main.addView(title);
        
        TextView subtitle = new TextView(this);
        subtitle.setText("Select tools to install:");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xFF666666);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.setMargins(0, 8, 0, 16);
        main.addView(subtitle, subtitleParams);
        
        // Checkboxes
        toolCheckboxes = new CheckBox[tools.length];
        for (int i = 0; i < tools.length; i++) {
            toolCheckboxes[i] = new CheckBox(this);
            toolCheckboxes[i].setText(tools[i]);
            toolCheckboxes[i].setTextSize(16);
            toolCheckboxes[i].setTextColor(0xFF333333);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.setMargins(0, 8, 0, 8);
            main.addView(toolCheckboxes[i], params);
        }
        
        // Install Button
        btnInstall = new Button(this);
        btnInstall.setText("Install Selected");
        btnInstall.setTextColor(0xFFFFFFFF);
        btnInstall.setBackgroundColor(0xFF007AFF);
        btnInstall.setTextSize(16);
        btnInstall.setTypeface(null, android.graphics.Typeface.BOLD);
        btnInstall.setOnClickListener(this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(-1, 56);
        btnParams.setMargins(0, 24, 0, 0);
        main.addView(btnInstall, btnParams);
        
        ScrollView scroll = new ScrollView(this);
        scroll.addView(main);
        
        setContentView(scroll);
    }
    
    @Override
    public void onClick(View v) {
        if (v == btnInstall) {
            StringBuilder selected = new StringBuilder();
            for (int i = 0; i < toolCheckboxes.length; i++) {
                if (toolCheckboxes[i].isChecked()) {
                    if (selected.length() > 0) selected.append(" ");
                    selected.append(tools[i]);
                }
            }
            
            if (selected.length() == 0) {
                Toast.makeText(this, "Select at least one tool", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Toast.makeText(this, "Install: " + selected.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
