package com.termux.toolbox;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class TermuxApiHelper {

    private static final String TERMUX_PACKAGE = "com.termux";
    private static final String TERMUX_API_PACKAGE = "com.termux.api";
    private static final String TERMUX_TASKER_PACKAGE = "com.termux.tasker";
    
    private Context context;
    
    public TermuxApiHelper(Context context) {
        this.context = context;
    }
    
    public boolean isTermuxInstalled() {
        try {
            context.getPackageManager().getPackageInfo(TERMUX_PACKAGE, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    
    public boolean isTermuxApiInstalled() {
        try {
            context.getPackageManager().getPackageInfo(TERMUX_API_PACKAGE, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    
    public boolean isTermuxTaskerInstalled() {
        try {
            context.getPackageManager().getPackageInfo(TERMUX_TASKER_PACKAGE, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    
    public void runShellCommand(String shellCommand) {
        if (!isTermuxInstalled()) {
            Toast.makeText(context, "Termux tidak terinstall!", Toast.LENGTH_LONG).show();
            return;
        }
        
        copyToClipboard(shellCommand);
        
        openTermuxWithCommand(shellCommand);
    }
    
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Termux Command", text);
        clipboard.setPrimaryClip(clip);
    }
    
    private void openTermuxWithCommand(String command) {
        try {
            Intent intent = new Intent();
            intent.setClassName(TERMUX_PACKAGE, "com.termux.app.TermuxActivity");
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, command);
            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            
            Toast.makeText(context, "Command copied! Paste di Termux dengan long-press", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            openTermux();
            Toast.makeText(context, "Command disalin ke clipboard. Paste manual di Termux.", Toast.LENGTH_LONG).show();
        }
    }
    
    public void openTermux() {
        if (!isTermuxInstalled()) {
            Toast.makeText(context, "Termux tidak terinstall!", Toast.LENGTH_LONG).show();
            return;
        }
        
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(TERMUX_PACKAGE);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    public void runWithTermuxUrl(String command) {
        if (!isTermuxInstalled()) {
            Toast.makeText(context, "Termux tidak terinstall!", Toast.LENGTH_LONG).show();
            return;
        }
        
        try {
            String encodedCommand = Uri.encode(command);
            String termuxUrl = "termux://run?command=" + encodedCommand;
            
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(termuxUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            runShellCommand(command);
        }
    }
    
    public void termuxApiCall(String apiAction, Bundle extras) {
        if (!isTermuxApiInstalled()) {
            Toast.makeText(context, "Termux:API tidak terinstall! Install dengan: pkg install termux-api", Toast.LENGTH_LONG).show();
            return;
        }
        
        try {
            Intent intent = new Intent();
            intent.setClassName(TERMUX_API_PACKAGE, "com.termux.api.activities.BroadcastReceiverActivity");
            intent.setAction("com.termux.api.action." + apiAction);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            if (extras != null) {
                intent.putExtras(extras);
            }
            
            context.startActivity(intent);
        } catch (Exception e) {
            String command = "termux-" + apiAction.toLowerCase();
            runShellCommand(command);
        }
    }
    
    public void showToast(String message) {
        String command = "termux-toast '" + message.replace("'", "\\'") + "'";
        runShellCommand(command);
    }
    
    public void vibrate(int duration) {
        String command = "termux-vibrate -d " + duration;
        runShellCommand(command);
    }
    
    public void showNotification(String title, String content) {
        String command = "termux-notification --title '" + title.replace("'", "\\'") + 
                        "' --content '" + content.replace("'", "\\'") + "'";
        runShellCommand(command);
    }
    
    public void shareCommand(String command) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, command);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        Intent chooser = Intent.createChooser(shareIntent, "Kirim command ke...");
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chooser);
    }
    
    public void createTermuxScript(String scriptName, String scriptContent) {
        copyToClipboard(scriptContent);
        String createCommand = "echo '" + scriptContent.replace("'", "'\\''") + 
                              "' > ~/bin/" + scriptName + " && chmod +x ~/bin/" + scriptName;
        runShellCommand(createCommand);
    }
}
