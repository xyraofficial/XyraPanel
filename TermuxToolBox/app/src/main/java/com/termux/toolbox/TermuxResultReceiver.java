package com.termux.toolbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TermuxResultReceiver extends BroadcastReceiver {
    
    public static final String ACTION_RESULT = "com.termux.toolbox.RESULT";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && ACTION_RESULT.equals(intent.getAction())) {
            String stdout = intent.getStringExtra("stdout");
            String stderr = intent.getStringExtra("stderr");
            int exitCode = intent.getIntExtra("exitCode", -1);
            
            if (stdout != null && !stdout.isEmpty()) {
                Toast.makeText(context, "Output: " + stdout.substring(0, Math.min(100, stdout.length())), Toast.LENGTH_LONG).show();
            }
            
            if (stderr != null && !stderr.isEmpty()) {
                Toast.makeText(context, "Error: " + stderr, Toast.LENGTH_LONG).show();
            }
        }
    }
}
