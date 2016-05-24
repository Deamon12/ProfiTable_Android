package com.ucsandroid.profitable.serverclasses;

import java.util.ArrayList;
import java.util.List;

public class OrderedItem {

    private int orderedItemId;
    private String orderedItemNotes;
    private String orderedItemStatus;
    private boolean bringFirst;
    private ServerMenuItem menuItem;
    private List<FoodAddition> additions;

    public OrderedItem(int orderedItemId, String orderedItemNotes,
                       String orderedItemStatus, boolean bringFirst,
                       ServerMenuItem menuItem, List<FoodAddition> additions) {
        super();
        this.orderedItemId = orderedItemId;
        this.orderedItemNotes = orderedItemNotes;
        this.orderedItemStatus = orderedItemStatus;
        this.bringFirst = bringFirst;
        this.menuItem = menuItem;
        this.additions = additions;
    }

    public OrderedItem(int id, String notes, String status,
                       boolean bringFirst) {
        super();
        this.orderedItemId = id;
        this.orderedItemNotes = notes;
        this.orderedItemStatus = status;
        this.bringFirst = bringFirst;
        this.additions = new ArrayList<FoodAddition>();
    }

    public OrderedItem(int orderedItemId, String orderedItemNotes,
                       String orderedItemStatus, boolean bringFirst,
                       List<FoodAddition> additions) {
        super();
        this.orderedItemId = orderedItemId;
        this.orderedItemNotes = orderedItemNotes;
        this.orderedItemStatus = orderedItemStatus;
        this.bringFirst = bringFirst;
        this.additions = additions;
    }

    public OrderedItem() {
        super();
        this.additions = new ArrayList<FoodAddition>();
    }

    public int getOrderedItemId() {
        return orderedItemId;
    }
    public void setOrderedItemId(int orderedItemId) {
        this.orderedItemId = orderedItemId;
    }
    public String getOrderedItemNotes() {
        return orderedItemNotes;
    }
    public void setOrderedItemNotes(String orderedItemNotes) {
        this.orderedItemNotes = orderedItemNotes;
    }
    public String getOrderedItemStatus() {
        return orderedItemStatus;
    }
    public void setOrderedItemStatus(String orderedItemStatus) {
        this.orderedItemStatus = orderedItemStatus;
    }
    public boolean isBringFirst() {
        return bringFirst;
    }
    public void setBringFirst(boolean bringFirst) {
        this.bringFirst = bringFirst;
    }
    public List<FoodAddition> getAdditions() {
        return additions;
    }
    public void setAdditions(List<FoodAddition> additions) {
        this.additions = additions;
    }
    public void addAddition(FoodAddition fa){
        this.additions.add(fa);
    }
    public ServerMenuItem getMenuItem() {
        return menuItem;
    }
    public void setMenuItem(ServerMenuItem menuItem) {
        this.menuItem = menuItem;
    }
}