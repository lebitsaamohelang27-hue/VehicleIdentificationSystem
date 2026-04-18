package com.vis.model;

/**
 * Customer — extends Person (demonstrates Inheritance).
 */
public class Customer extends Person {

    private String address;

    public Customer() {}

    public Customer(int id, String name, String address, String phone, String email) {
        super(id, name, phone, email);
        this.address = address;
    }

    public String getAddress() { return address; }
    public void   setAddress(String a) { this.address = a; }

    /** POLYMORPHISM — overrides abstract method */
    @Override
    public String getRole() { return "Customer"; }
}
