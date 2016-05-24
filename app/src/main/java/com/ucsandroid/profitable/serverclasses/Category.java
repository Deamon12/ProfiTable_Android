package com.ucsandroid.profitable.serverclasses;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private String categoryName;
    private int categoryId;
    private List<ServerMenuItem> menuItems;

    public Category() {
        super();
    }

    public Category(String name, int id) {
        super();
        this.categoryName = name;
        this.categoryId = id;
        this.menuItems = new ArrayList<ServerMenuItem>();
    }

    public Category(String name, int id, List<ServerMenuItem> menuItems) {
        super();
        this.categoryName = name;
        this.categoryId = id;
        this.menuItems = menuItems;
    }

    public String getName() {
        return categoryName;
    }
    public void setName(String name) {
        this.categoryName = name;
    }
    public int getId() {
        return categoryId;
    }
    public void setId(int id) {
        this.categoryId = id;
    }
    public List<ServerMenuItem> getMenuItems() {
        return menuItems;
    }
    public void setMenuItems(List<ServerMenuItem> menuItems) {
        this.menuItems = menuItems;
    }
    public void addToCategory(ServerMenuItem mi) {
        menuItems.add(mi);
    }
}