package com.ucsandroid.profitable.serverclasses;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private int customerId;
    private int tabId;
    private List<OrderedItem> order;
    private String customerNotes = "";

    public Customer(int id, int tabId) {
        super();
        this.customerId = id;
        this.tabId = tabId;
        this.order = new ArrayList();
    }

    public Customer(int customerId, int tabId, List<OrderedItem> order) {
        super();
        this.customerId = customerId;
        this.tabId = tabId;
        this.order = order;
    }

    public Customer() {
        super();
        this.order = new ArrayList();
    }

    public int getTabId() {
        return tabId;
    }
    public void setTabId(int tabId) {
        this.tabId = tabId;
    }
    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    public List<OrderedItem> getOrders() {
        return order;
    }
    public void setOrder(List<OrderedItem> order) {
        this.order = order;
    }
    public void addItem(OrderedItem oi) {
        this.order.add(oi);
    }
    public String getCustomerNotes(){
        return customerNotes;
    }
    public void setCustomerNotes(String notes){
        customerNotes = notes;
    }
    public void removeItem(int position){
        if(position <= order.size()-1)
            order.remove(position);
    }

    public double getCustomerCost(){
        double cost = 0;
        for(int a = 0; a < order.size();a++){
            cost += order.get(a).getMenuItem().getPrice();
            cost += order.get(a).getAdditionsCost();
        }
        return cost;
    }
    public boolean allOrdersReady(){
        for(OrderedItem item : order){
            if(!item.getOrderedItemStatus().equalsIgnoreCase("ready")){
                return false;
            }
        }
        return true;
    }

}