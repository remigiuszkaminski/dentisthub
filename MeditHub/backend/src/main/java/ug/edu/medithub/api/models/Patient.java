package ug.edu.medithub.api.models;

import java.util.ArrayList;

public class Patient {

    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private String dateOfBirth;

    private ArrayList<Order> orders;


    public Patient() {
        this.orders = new ArrayList<>();
    }

    public Patient(String name, String phoneNumber, String email, String dateOfBirth) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.orders = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    public void editOrder(Order order) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId().equals(order.getId())) {
                orders.set(i, order);
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", orders=" + orders +
                '}';
    }
}
