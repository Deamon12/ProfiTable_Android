package com.ucsandroid.profitable.serverclasses;

import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tab {

    private int tabId;
    private String tabStatus;
    private Date timeIn;
    private Date timeOut;
    private Discount discount;
    private List<Customer> customers;
    private Employee server;
    public String status = "not_ready";


    public Tab(int tabId, String tabStatus, Date timeIn,
               Date timeOut, Discount discount, Employee server) {
        super();
        this.tabId = tabId;
        this.tabStatus = tabStatus;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.discount = discount;
        this.customers = new ArrayList<Customer>();
        this.server = server;
    }

    public Tab(int tabId, String tabStatus, Date timeIn,
               Date timeOut, Discount discount,
               List<Customer> customers, Employee server) {
        super();
        this.tabId = tabId;
        this.tabStatus = tabStatus;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.discount = discount;
        this.customers = customers;
        this.server = server;
    }

    public Tab(int tabId, String tabStatus, Date timeIn, Date timeOut,
               Discount discount, List<Customer> customers) {
        super();
        this.tabId = tabId;
        this.tabStatus = tabStatus;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.discount = discount;
        this.customers = customers;
    }

    public Tab(int id, String status, Date timeIn, Date timeOut) {
        super();
        this.tabId = id;
        this.tabStatus = status;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.customers = new ArrayList<Customer>();
    }

    public Tab(int tabId, String tabStatus, Discount discount) {
        super();
        this.tabId = tabId;
        this.tabStatus = tabStatus;
        this.discount = discount;
        this.customers = new ArrayList<Customer>();
    }

    public Tab(int tabId, String tabStatus, Date timeIn, Date timeOut,
               Discount discount) {
        super();
        this.tabId = tabId;
        this.tabStatus = tabStatus;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.discount = discount;
        this.customers = new ArrayList<Customer>();
    }

    public Tab(int id, String status) {
        super();
        this.tabId = id;
        this.tabStatus = status;
        this.customers = new ArrayList<Customer>();
    }

    public Tab() {
        super();
        this.customers = new ArrayList<Customer>();
    }

    public Date getTimeIn() {
        return timeIn;
    }
    public void setTimeIn(Date timeIn) {
        this.timeIn = timeIn;
    }
    public Date getTimeOut() {
        return timeOut;
    }
    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
    }
    public int getTabId() {
        return tabId;
    }

    public void setTabId(int tabId) {
        this.tabId = tabId;

        for(Customer customer : customers){
            customer.setTabId(tabId);
        }

    }
    public String getTabStatus() {
        return tabStatus;
    }
    public void setTabStatus(String tabStatus) {
        this.tabStatus = tabStatus;
    }
    public Discount getDiscount() {
        return discount;
    }
    public void setDiscount(Discount discount) {
        this.discount = discount;
    }
    public List<Customer> getCustomers() {
        return customers;
    }
    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
    public Employee getServer() {
        return server;
    }
    public void setServer(Employee server) {
        this.server = server;
    }
    public void addCustomer(Customer c) {
        this.customers.add(0, c);
    }
    public void removeCustomer(int position){
        customers.remove(position);
    }

    public boolean allOrdersReady(){
        if(customers.size() == 0){
            return false;
        }
        for(Customer customer : customers){
            if(!customer.allOrdersReady()){
                return false;
            }
        }
        status = "ready";
        return true;
    }

    public boolean hasUnOrderedItems(){
        for(Customer customer : customers){
            if(customer.hasUnordedItems()){
                return true;
            }
        }
        return false;
    }

    public boolean hasReadyOrders(){
        for(Customer customer : customers){
            if(customer.hasReadyOrder()){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Tab{" +
                "tabId=" + tabId +
                ", tabStatus='" + tabStatus + '\'' +
                ", timeIn=" + timeIn +
                ", timeOut=" + timeOut +
                ", discount=" + discount +
                ", customers=" + customers +
                ", server=" + server +
                '}';
    }
}