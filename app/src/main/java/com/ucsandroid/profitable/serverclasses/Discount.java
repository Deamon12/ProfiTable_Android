package com.ucsandroid.profitable.serverclasses;

public class Discount {

    private int discountId;
    private String discountType;
    private double discountPercent;
    private boolean available;
    private int restaurantId;

    public Discount(int id, String type, double percent, boolean available,
                    int restaurantId) {
        super();
        this.discountId = id;
        this.discountType = type;
        this.discountPercent = percent;
        this.available = available;
        this.restaurantId = restaurantId;
    }
    public int getId() {
        return discountId;
    }
    public void setId(int id) {
        this.discountId = id;
    }
    public String getType() {
        return discountType;
    }
    public void setType(String type) {
        this.discountType = type;
    }
    public double getPercent() {
        return discountPercent;
    }
    public void setPercent(double percent) {
        this.discountPercent = percent;
    }
    public boolean isAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }
    public int getRestaurantId() {
        return restaurantId;
    }
    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }
}