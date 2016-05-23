package com.ucsandroid.profitable.supportclasses;


import org.json.JSONException;

import java.util.ArrayList;

public class Customer {

    private String name;
    private ArrayList<MenuItem> items;
    private String comment = "";

    public Customer(){
        name = "";
        items = new ArrayList<>();
    }

    public Customer(String name){
        this.name = name;
        items = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<MenuItem> getItems() {
        return items;
    }

    public MenuItem getItem(int position){
        return items.get(position);
    }

    public void setItems(ArrayList<MenuItem> items) {
        this.items = items;
    }

    public void addMenuItem(MenuItem item){
        items.add(item);
    }

    /**
     *
     * @return
     */
    public int getCost()  {
        int total = 0;
        for(int a = 0; a < items.size();a++){

            try {
                total += items.get(a).getJsonItem().getInt("menuItemPrice");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    public void removeItem(int position){
        items.remove(position);
    }

    public String getComment(){
        return comment;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

}
