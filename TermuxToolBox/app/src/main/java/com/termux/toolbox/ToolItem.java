package com.termux.toolbox;

public class ToolItem {
    private String id;
    private String name;
    private String description;
    private String icon;
    private String command;
    private String category;
    
    public ToolItem(String id, String name, String description, String icon, String command, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.command = command;
        this.category = category;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public String getCommand() { return command; }
    public String getCategory() { return category; }
}
