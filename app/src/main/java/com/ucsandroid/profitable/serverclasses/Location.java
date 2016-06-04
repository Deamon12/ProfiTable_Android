package com.ucsandroid.profitable.serverclasses;

import java.io.Serializable;
import java.util.List;

public class Location implements Serializable {

    private int locationId;
    private String locationStatus;
    private String locationName;
    private Tab currentTab;
    private int restaurantId;
    private boolean editedLocally = false;
    private String foodStatus = "not_ready";

    public Location(int id, String status, String name, int restaurantId) {
        super();
        this.locationId = id;
        this.locationStatus = status;
        this.locationName = name;
        this.restaurantId = restaurantId;
    }

    public Location() {
        super();
    }

    public Location(int locationId, String locationStatus,
                    String locationName, Tab currentTab, int restaurantId) {
        super();
        this.locationId = locationId;
        this.locationStatus = locationStatus;
        this.locationName = locationName;
        this.currentTab = currentTab;
        this.restaurantId = restaurantId;
    }

    public Tab getCurrentTab() {
        return currentTab;
    }
    public void setCurrentTab(Tab currentTab) {
        this.currentTab = currentTab;
    }
    public int getId() {
        return locationId;
    }
    public void setId(int id) {
        this.locationId = id;
    }
    public String getStatus() {
        return locationStatus;
    }
    public void setStatus(String status) {
        this.locationStatus = status;
    }
    public String getName() {
        return locationName;
    }
    public void setName(String name) {
        this.locationName = name;
    }
    public int getRestaurantId() {
        return restaurantId;
    }
    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public boolean hasCost(){
        double cost = 0;
        for(int a = 0; a < getCurrentTab().getCustomers().size();a++){
            cost += getCurrentTab().getCustomers().get(a).getCustomerCost();
            if(cost > 0){
                return true;
            }
        }
        return false;
    }

    public double getLocationCost(){
        double cost = 0;
        List<Customer> custs = getCurrentTab().getCustomers();
        for(int a = 0; a < custs.size();a++){
            cost += custs.get(a).getCustomerCost();
        }
        return cost;
    }

    public void setEditedLocally(boolean edited){
        editedLocally = edited;
    }

    public boolean isEditedLocally(){
        return editedLocally;
    }

    public void setFoodStatus(String status){
        foodStatus = status;
    }

    public boolean isFoodIsReady() {
        return (foodStatus.equalsIgnoreCase("ready"));
    }


    @Override
    public String toString() {
        return "Location{" +
                "locationId=" + locationId +
                ", locationStatus='" + locationStatus + '\'' +
                ", locationName='" + locationName + '\'' +
                ", currentTab=" + currentTab +
                ", restaurantId=" + restaurantId +
                ", editedLocally=" + editedLocally +
                '}';
    }
}