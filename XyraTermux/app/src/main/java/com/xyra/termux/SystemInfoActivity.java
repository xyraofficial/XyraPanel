package com.xyra.termux;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class SystemInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(16, 16, 16, 16);
        main.setBackgroundColor(0xFFF5F5F7);
        
        // Title
        TextView title = new TextView(this);
        title.setText("System Information");
        title.setTextSize(28);
        title.setTextColor(0xFF000000);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        main.addView(title);
        
        // Add info sections
        addInfoSection(main, "Device Info", 
            "Brand: " + Build.BRAND + "\n" +
            "Device: " + Build.DEVICE + "\n" +
            "Model: " + Build.MODEL);
        
        addInfoSection(main, "Android Version",
            "SDK: " + Build.VERSION.SDK_INT + "\n" +
            "Release: " + Build.VERSION.RELEASE + "\n" +
            "Fingerprint: " + Build.FINGERPRINT);
        
        addInfoSection(main, "Runtime Info",
            "Java Version: " + System.getProperty("java.version") + "\n" +
            "Java Vendor: " + System.getProperty("java.vendor") + "\n" +
            "Available Processors: " + Runtime.getRuntime().availableProcessors());
        
        addInfoSection(main, "Memory Info",
            "Total Memory: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MB\n" +
            "Free Memory: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MB\n" +
            "Max Memory: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB");
        
        ScrollView scroll = new ScrollView(this);
        scroll.addView(main);
        
        setContentView(scroll);
    }
    
    private void addInfoSection(LinearLayout parent, String title, String content) {
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(18);
        titleView.setTextColor(0xFF007AFF);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, -2);
        titleParams.setMargins(0, 16, 0, 8);
        parent.addView(titleView, titleParams);
        
        TextView contentView = new TextView(this);
        contentView.setText(content);
        contentView.setTextSize(14);
        contentView.setTextColor(0xFF333333);
        contentView.setBackgroundColor(0xFFFFFFFF);
        contentView.setPadding(12, 12, 12, 12);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(-1, -2);
        contentParams.setMargins(0, 0, 0, 8);
        parent.addView(contentView, contentParams);
    }
}
