package com.ucsandroid.profitable.supportclasses;

import org.json.JSONObject;

import java.util.ArrayList;

public class Location {


    private boolean isSentToKitchen = false;
    private String label;
    private ArrayList<Customer> customer;
    private JSONObject jsonLocation;


    public Location(JSONObject jsonTable){
        customer = new ArrayList<>();
        label = "";
        this.jsonLocation = jsonTable;
    }

    public Location(String label){
        customer = new ArrayList<>();
        this.label = label;
        jsonLocation = new JSONObject();
    }

    public Location(){
        customer = new ArrayList<>();
        label = "";
        jsonLocation = new JSONObject();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Customer getCustomer(int position) {
        return customer.get(position);
    }

    public ArrayList<Customer> getCustomer() {
        return customer;
    }

    public void setCustomer(ArrayList<Customer> customer) {
        this.customer = customer;
    }

    public void addCustomer(Customer order){
        customer.add(0, order);
    }

    public void removeCustomer(int position){
        customer.remove(position);
    }

    public boolean hasCost(){

        for(int a = 0; a < customer.size(); a++){
            if(customer.get(a).getCost() > 0){
                return true;
            }
        }
        return false;
    }

    public boolean hasCustomer(){

        if(customer != null & customer.size() > 0)
            return true;
        else
            return false;
    }

    public int getTableCost(){
        int total = 0;
        for(int a = 0; a < customer.size(); a++){
            total += customer.get(a).getCost();
        }
        return total;
    }

    public boolean isSentToKitchen(){
        return isSentToKitchen;
    }

    public void sentToKitchen(boolean bool){
        isSentToKitchen = bool;
    }

    public void setJsonLocation(JSONObject jsonLocation){
        this.jsonLocation = jsonLocation;
    }

    public JSONObject getJsonLocation(){
        return jsonLocation;
    }


    @Override
    public String toString() {
        return "Location{" +
                "isSentToKitchen=" + isSentToKitchen +
                ", label='" + label + '\'' +
                ", customer=" + customer +
                ", jsonLocation=" + jsonLocation +
                '}';
    }
}
