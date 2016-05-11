package supportclasses;


import java.util.ArrayList;

public class Order {

    private String name;
    private ArrayList<MenuItem> items;


    public Order(String name){
        this.name = name;
        items = new ArrayList<>();
    }

    public ArrayList getItems() {
        return items;
    }

    public void setItems(ArrayList items) {
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addItem(String item){
        items.add(new MenuItem(item));
    }

}
