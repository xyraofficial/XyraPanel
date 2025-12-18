package com.termux.toolbox;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TermuxApiHelper termuxApi;
    private TextView tvTermuxStatus;
    private TextView tvApiStatus;
    private GridLayout toolsGrid;
    private EditText etCustomCommand;
    
    private List<ToolItem> tools = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        termuxApi = new TermuxApiHelper(this);
        
        initViews();
        initTools();
        checkTermuxStatus();
        populateToolsGrid();
    }
    
    private void initViews() {
        tvTermuxStatus = (TextView) findViewById(R.id.tv_termux_status);
        tvApiStatus = (TextView) findViewById(R.id.tv_api_status);
        toolsGrid = (GridLayout) findViewById(R.id.tools_grid);
        etCustomCommand = (EditText) findViewById(R.id.et_custom_command);
        
        Button btnRunCommand = (Button) findViewById(R.id.btn_run_command);
        Button btnOpenTermux = (Button) findViewById(R.id.btn_open_termux);
        Button btnInstallTermux = (Button) findViewById(R.id.btn_install_termux);
        ImageButton btnRefresh = (ImageButton) findViewById(R.id.btn_refresh);
        
        btnRunCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runCustomCommand();
            }
        });
        
        btnOpenTermux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                termuxApi.openTermux();
            }
        });
        
        btnInstallTermux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlayStore("com.termux");
            }
        });
        
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkTermuxStatus();
                Toast.makeText(MainActivity.this, "Status updated", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void initTools() {
        tools.add(new ToolItem("update", "Update System", "Update & upgrade packages", "update", "pkg update -y && pkg upgrade -y", "System"));
        tools.add(new ToolItem("storage", "Setup Storage", "Allow Termux to access storage", "storage", "termux-setup-storage", "System"));
        tools.add(new ToolItem("python", "Install Python", "Install Python 3", "python", "pkg install python -y", "Development"));
        tools.add(new ToolItem("nodejs", "Install Node.js", "Install Node.js", "nodejs", "pkg install nodejs -y", "Development"));
        tools.add(new ToolItem("git", "Install Git", "Install Git version control", "git", "pkg install git -y", "Development"));
        tools.add(new ToolItem("php", "Install PHP", "Install PHP", "php", "pkg install php -y", "Development"));
        tools.add(new ToolItem("ruby", "Install Ruby", "Install Ruby", "ruby", "pkg install ruby -y", "Development"));
        tools.add(new ToolItem("golang", "Install Go", "Install Golang", "golang", "pkg install golang -y", "Development"));
        tools.add(new ToolItem("nmap", "Install Nmap", "Network scanner", "nmap", "pkg install nmap -y", "Network"));
        tools.add(new ToolItem("wget", "Install Wget", "Download tool", "wget", "pkg install wget -y", "Network"));
        tools.add(new ToolItem("curl", "Install cURL", "Transfer data tool", "curl", "pkg install curl -y", "Network"));
        tools.add(new ToolItem("ssh", "Install OpenSSH", "SSH client & server", "ssh", "pkg install openssh -y", "Network"));
        tools.add(new ToolItem("nano", "Install Nano", "Text editor", "nano", "pkg install nano -y", "Utilities"));
        tools.add(new ToolItem("vim", "Install Vim", "Advanced text editor", "vim", "pkg install vim -y", "Utilities"));
        tools.add(new ToolItem("htop", "Install Htop", "Process monitor", "htop", "pkg install htop -y", "Utilities"));
        tools.add(new ToolItem("neofetch", "Install Neofetch", "System info display", "neofetch", "pkg install neofetch -y", "Utilities"));
        tools.add(new ToolItem("ffmpeg", "Install FFmpeg", "Media converter", "ffmpeg", "pkg install ffmpeg -y", "Media"));
        tools.add(new ToolItem("imagemagick", "Install ImageMagick", "Image processing", "imagemagick", "pkg install imagemagick -y", "Media"));
        tools.add(new ToolItem("termux_api", "Install Termux:API", "API integration", "api", "pkg install termux-api -y", "System"));
        tools.add(new ToolItem("clang", "Install Clang", "C/C++ compiler", "clang", "pkg install clang -y", "Development"));
    }
    
    private void checkTermuxStatus() {
        boolean termuxInstalled = termuxApi.isTermuxInstalled();
        boolean apiInstalled = termuxApi.isTermuxApiInstalled();
        
        if (termuxInstalled) {
            tvTermuxStatus.setText("Termux: Terinstall ✓");
            tvTermuxStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvTermuxStatus.setText("Termux: Tidak Terinstall ✗");
            tvTermuxStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
        
        if (apiInstalled) {
            tvApiStatus.setText("Termux:API: Terinstall ✓");
            tvApiStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvApiStatus.setText("Termux:API: Tidak Terinstall ✗");
            tvApiStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
    
    private void populateToolsGrid() {
        toolsGrid.removeAllViews();
        
        for (final ToolItem tool : tools) {
            View toolView = LayoutInflater.from(this).inflate(R.layout.item_tool, null);
            
            TextView tvName = (TextView) toolView.findViewById(R.id.tv_tool_name);
            TextView tvDesc = (TextView) toolView.findViewById(R.id.tv_tool_desc);
            
            tvName.setText(tool.getName());
            tvDesc.setText(tool.getDescription());
            
            toolView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showToolDialog(tool);
                }
            });
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            params.setMargins(8, 8, 8, 8);
            
            toolView.setLayoutParams(params);
            toolsGrid.addView(toolView);
        }
    }
    
    private void showToolDialog(final ToolItem tool) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(tool.getName());
        builder.setMessage("Command:\n" + tool.getCommand() + "\n\nCategory: " + tool.getCategory() + "\n\n" + tool.getDescription() + "\n\nNote: Command akan disalin dan Termux akan dibuka. Paste command dengan long-press.");
        
        builder.setPositiveButton("Run in Termux", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                termuxApi.runShellCommand(tool.getCommand());
            }
        });
        
        builder.setNeutralButton("Copy Only", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Command", tool.getCommand());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "Command disalin ke clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void runCustomCommand() {
        String command = etCustomCommand.getText().toString().trim();
        
        if (command.isEmpty()) {
            Toast.makeText(this, "Masukkan command terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }
        
        termuxApi.runShellCommand(command);
    }
    
    private void openPlayStore(String packageName) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
            startActivity(intent);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        checkTermuxStatus();
    }
}
