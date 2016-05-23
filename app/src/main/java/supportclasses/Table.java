package supportclasses;

import java.util.ArrayList;

/**
 * Created by deamon on 5/17/16.
 */
public class Table {


    private boolean isSentToKitchen = false;
    private String label;
    private ArrayList<Customer> customer;

    public Table(){
        customer = new ArrayList<>();
        label = "";
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


}
