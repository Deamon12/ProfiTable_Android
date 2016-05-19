package supportclasses;

import java.util.ArrayList;

/**
 * Created by deamon on 5/17/16.
 */
public class Table {


    private String label;

    private ArrayList<Order> orders; //customer

    public Table(){
        orders = new ArrayList<>();
        orders.add(new Order("Customer 1"));
        label = "";
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Order getOrder(int position) {
        return orders.get(position);
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public void addOrder(Order order){
        orders.add(0, order);
    }

    public boolean hasOrders(){

        for(int a = 0; a < orders.size();a++){
            if(orders.get(a).getCost() > 0){
                return true;
            }
        }
        return false;
    }

    public int getTableCost(){
        int total = 0;
        for(int a = 0; a < orders.size();a++){
            total += orders.get(a).getCost();
        }
        return total;
    }

}
