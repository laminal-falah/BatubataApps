package com.palcomtech.batubataapps.model;

public class MenuDashboard {

    private int icon;
    private String title;

    public MenuDashboard() {
    }

    public MenuDashboard(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
