package com.ucsandroid.profitable.serverclasses;

import java.util.ArrayList;
import java.util.List;

public class ServerCustomer {

    private int customerId;
    private int tabId;
    private List<OrderedItem> order;

    public ServerCustomer(int id, int tabId) {
        super();
        this.customerId = id;
        this.tabId = tabId;
        this.order = new ArrayList<OrderedItem>();
    }

    public ServerCustomer(int customerId, int tabId, List<OrderedItem> order) {
        super();
        this.customerId = customerId;
        this.tabId = tabId;
        this.order = order;
    }

    public ServerCustomer() {
        super();
        this.order = new ArrayList<OrderedItem>();
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
    public List<OrderedItem> getOrder() {
        return order;
    }
    public void setOrder(List<OrderedItem> order) {
        this.order = order;
    }
    public void addItem(OrderedItem oi) {
        this.order.add(oi);
    }
}