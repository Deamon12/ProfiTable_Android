package com.ucsandroid.profitable.serverclasses;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private String categoryName;
    private int categoryId;
    private List<MenuItem> menuItems;

    public Category() {
        super();
    }

    public Category(String name, int id) {
        super();
        this.categoryName = name;
        this.categoryId = id;
        this.menuItems = new ArrayList<MenuItem>();
    }

    public Category(String name, int id, List<MenuItem> menuItems) {
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
    public List<MenuItem> getMenuItems() {
        return menuItems;
    }
    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
    public void addToCategory(MenuItem mi) {
        menuItems.add(mi);
    }

    public MenuItem findMenuItem(int menuId){
        System.out.println("looking for "+menuId);
        for(int a = 0; a < menuItems.size();a++){
            System.out.println("found "+menuItems.get(a).getId());
            if(menuItems.get(a).getId() == menuId)
                System.out.println("MenuItem found at "+a);
                return menuItems.get(a);
        }
        System.out.println("MenuItem Not found");
        return null;
    }

}