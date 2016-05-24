package com.ucsandroid.profitable.supportclasses;

import org.json.JSONObject;

import java.util.ArrayList;

public class Location {


    private boolean isSentToKitchen = false;
    private String label;
    private ArrayList<Customer> customers;
    private JSONObject jsonLocation;
    private int tabId = -1;


    public Location(JSONObject jsonTable){
        customers = new ArrayList<>();
        label = "";
        this.jsonLocation = jsonTable;
    }

    public Location(String label){
        customers = new ArrayList<>();
        this.label = label;
        jsonLocation = new JSONObject();
    }

    public Location(){
        customers = new ArrayList<>();
        label = "";
        jsonLocation = new JSONObject();
    }

    public Customer getCustomer(int position) {
        return customers.get(position);
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public void addCustomer(Customer order){
        customers.add(0, order);
    }

    public void removeCustomer(int position){
        customers.remove(position);
    }

    public boolean hasCost(){

        for(int a = 0; a < customers.size(); a++){
            if(customers.get(a).getCost() > 0){
                return true;
            }
        }
        return false;
    }

    public boolean hasCustomer(){

        if(customers != null & customers.size() > 0)
            return true;
        else
            return false;
    }

    public int getTableCost(){
        int total = 0;
        for(int a = 0; a < customers.size(); a++){
            total += customers.get(a).getCost();
        }
        return total;
    }

    public boolean isSentToKitchen(){
        return isSentToKitchen;
    }

    public void sendToKitchen(boolean bool){
        isSentToKitchen = bool;
    }

    public void setJsonLocation(JSONObject jsonLocation){
        this.jsonLocation = jsonLocation;
    }

    public JSONObject getJsonLocation(){
        return jsonLocation;
    }

    public void setTabId(int tabId){
        this.tabId = tabId;
    }

    public int getTabId(){
        return tabId;
    }

    @Override
    public String toString() {
        return "Location{" +
                "isSentToKitchen=" + isSentToKitchen +
                ", label='" + label + '\'' +
                ", customers=" + customers +
                ", jsonLocation=" + jsonLocation +
                '}';
    }
}
