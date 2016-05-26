package com.ucsandroid.profitable.serverclasses;

public class Employee {

    private int employeeId;
    private String employeeType;
    private String accountName;
    private String firstName;
    private String lastName;
    private String password;
    private int restaurantId;

    public Employee(int id, String type, String accountName,
                    String firstName, String lastName, String password,
                    int restaurant) {
        super();
        this.employeeId = id;
        this.employeeType = type;
        this.accountName = accountName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.restaurantId = restaurant;
    }

    public Employee(int id, String type, String accountName,
                    String firstName, String lastName, int restaurant) {
        super();
        this.employeeId = id;
        this.employeeType = type;
        this.accountName = accountName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.restaurantId = restaurant;
    }

    public int getId() {
        return employeeId;
    }
    public void setId(int id) {
        this.employeeId = id;
    }
    public String getType() {
        return employeeType;
    }
    public void setType(String type) {
        this.employeeType = type;
    }
    public String getAccountName() {
        return accountName;
    }
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getRestaurant() {
        return restaurantId;
    }
    public void setRestaurant(int restaurant) {
        this.restaurantId = restaurant;
    }

}