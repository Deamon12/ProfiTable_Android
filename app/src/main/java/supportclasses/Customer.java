package supportclasses;


import org.json.JSONException;

import java.util.ArrayList;

public class Customer {

    private String name;
    private ArrayList<MenuItem> items;

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
        System.out.println("getting: "+position + ", size: "+items.size());
        //if(position <= items.size())
            return items.get(position);
        //else
        //    return null;
    }

    public void setItems(ArrayList<MenuItem> items) {
        this.items = items;
    }

    public void addMenuItem(MenuItem item){
        items.add(item);
    }

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

}
