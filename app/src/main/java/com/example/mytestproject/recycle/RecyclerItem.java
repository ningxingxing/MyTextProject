package com.example.mytestproject.recycle;

import java.io.Serializable;
import java.util.List;

public class RecyclerItem implements Serializable {
    private int icon;
    private String name;
    private List<Integer> iconList;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getIconList() {
        return iconList;
    }

    public void setIconList(List<Integer> iconList) {
        this.iconList = iconList;
    }
}
