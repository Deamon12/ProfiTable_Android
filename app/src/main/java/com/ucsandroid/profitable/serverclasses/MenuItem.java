package com.ucsandroid.profitable.serverclasses;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {

    private int	menuItemId;
    private String menuName;
    private String menuItemdescription;
    private int menuItemPrice;
    private boolean available;
    private List<FoodAddition> defaultAdditions;
    private List<FoodAddition> optionalAdditions;

    public MenuItem() {
        super();
        this.defaultAdditions = new ArrayList<FoodAddition>();
        this.optionalAdditions = new ArrayList<FoodAddition>();
    }

    public MenuItem(int menu_id, String menu_name) {
        super();
        this.menuItemId = menu_id;
        this.menuName = menu_name;
        this.defaultAdditions = new ArrayList<FoodAddition>();
        this.optionalAdditions = new ArrayList<FoodAddition>();
    }

    public MenuItem(int menuItemId, String menu_name, boolean available) {
        super();
        this.menuItemId = menuItemId;
        this.menuName = menu_name;
        this.available = available;
        this.defaultAdditions = new ArrayList<FoodAddition>();
        this.optionalAdditions = new ArrayList<FoodAddition>();
    }

    public MenuItem(int menuItemId, String menu_name,
                    int price, boolean available) {
        super();
        this.menuItemId = menuItemId;
        this.menuName = menu_name;
        this.menuItemPrice = price;
        this.available = available;
        this.defaultAdditions = new ArrayList<FoodAddition>();
        this.optionalAdditions = new ArrayList<FoodAddition>();
    }

    public MenuItem(int menuItemId, String menu_name,
                    String menuItemdescription, int menuItemPrice,
                    boolean available, List<FoodAddition> defaultAdditions,
                    List<FoodAddition> optionalAdditions) {
        super();
        this.menuItemId = menuItemId;
        this.menuName = menu_name;
        this.menuItemdescription = menuItemdescription;
        this.menuItemPrice = menuItemPrice;
        this.available = available;
        this.defaultAdditions = defaultAdditions;
        this.optionalAdditions = optionalAdditions;
    }

    public MenuItem(int menu_id, String menu_name, String description, int price) {
        super();
        this.menuItemId = menu_id;
        this.menuName = menu_name;
        this.menuItemdescription = description;
        this.menuItemPrice = price;
        this.defaultAdditions = new ArrayList<FoodAddition>();
        this.optionalAdditions = new ArrayList<FoodAddition>();
    }

    public MenuItem(int menu_id, String menu_name, String description,
                    int price, List<FoodAddition> defaultAdditions,
                    List<FoodAddition> optionalAdditions) {
        super();
        this.menuItemId = menu_id;
        this.menuName = menu_name;
        this.menuItemdescription = description;
        this.menuItemPrice = price;
        this.defaultAdditions = defaultAdditions;
        this.optionalAdditions = optionalAdditions;
    }

    public MenuItem(int menuItemId, String menuName,
                    String menuItemdescription, int menuItemPrice,
                    boolean available) {
        super();
        this.menuItemId = menuItemId;
        this.menuName = menuName;
        this.menuItemdescription = menuItemdescription;
        this.menuItemPrice = menuItemPrice;
        this.available = available;
        this.defaultAdditions = new ArrayList<FoodAddition>();
        this.optionalAdditions = new ArrayList<FoodAddition>();
    }

    public int getId() {
        return menuItemId;
    }
    public void setId(int menu_id) {
        this.menuItemId = menu_id;
    }
    public String getName() {
        return menuName;
    }
    public void setName(String menu_name) {
        this.menuName = menu_name;
    }
    public String getDescription() {
        return menuItemdescription;
    }
    public void setDescription(String description) {
        this.menuItemdescription = description;
    }
    public int getPrice() {
        return menuItemPrice;
    }
    public void setPrice(int price) {
        this.menuItemPrice = price;
    }
    public List<FoodAddition> getDefaultAdditions() {
        return defaultAdditions;
    }
    public void setDefaultAdditions(List<FoodAddition> defaultAdditions) {
        this.defaultAdditions = defaultAdditions;
    }
    public List<FoodAddition> getOptionalAdditions() {
        return optionalAdditions;
    }
    public void setOptionalAdditions(List<FoodAddition> optionalAdditions) {
        this.optionalAdditions = optionalAdditions;
    }
    public boolean getAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }
}