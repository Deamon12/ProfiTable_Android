package com.ucsandroid.profitable.serverclasses;


import java.io.Serializable;

public class FoodAddition implements Serializable {

    private String foodAdditionName;
    private int foodAdditionPrice;
    private boolean available;
    private int foodAdditionId;
    private int restaurantId;

    private boolean isChecked = false;
    private boolean isDefault = false;

    public FoodAddition(String foodAdditionName, int foodAdditionPrice,
                        int foodAdditionId) {
        super();
        this.foodAdditionName = foodAdditionName;
        this.foodAdditionPrice = foodAdditionPrice;
        this.foodAdditionId = foodAdditionId;
    }

    public FoodAddition(String name, int price, boolean available, int id, int restaurant) {
        super();
        this.foodAdditionName = name;
        this.foodAdditionPrice = price;
        this.available = available;
        this.foodAdditionId = id;
        this.restaurantId = restaurant;
    }

    public FoodAddition(String foodAdditionName, int foodAdditionPrice, boolean available, int foodAdditionId) {
        super();
        this.foodAdditionName = foodAdditionName;
        this.foodAdditionPrice = foodAdditionPrice;
        this.available = available;
        this.foodAdditionId = foodAdditionId;
    }

    public String getName() {
        return foodAdditionName;
    }
    public void setName(String name) {
        this.foodAdditionName = name;
    }
    public int getPrice() {
        return foodAdditionPrice;
    }
    public void setPrice(int price) {
        this.foodAdditionPrice = price;
    }
    public boolean isAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }
    public int getId() {
        return foodAdditionId;
    }
    public void setId(int id) {
        this.foodAdditionId = id;
    }
    public int getRestaurant() {
        return restaurantId;
    }
    public void setRestaurant(int restaurant) {
        this.restaurantId = restaurant;
    }
    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setIsDefault(boolean isDefault){
        this.isDefault = isDefault;
    }
    public boolean isDefault(){
        return isDefault;
    }

}