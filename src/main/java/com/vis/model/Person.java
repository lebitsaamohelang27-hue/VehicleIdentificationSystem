package com.vis.model;

/**
 * Abstract base class — demonstrates INHERITANCE.
 * Customer, PoliceOfficer, InsuranceAgent all extend this.
 */
public abstract class Person {

    protected int    id;
    protected String name;
    protected String phone;
    protected String email;

    public Person() {}

    public Person(int id, String name, String phone, String email) {
        this.id    = id;
        this.name  = name;
        this.phone = phone;
        this.email = email;
    }

    // Getters
    public int    getId()    { return id; }
    public String getName()  { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    // Setters
    public void setId(int id)       { this.id    = id; }
    public void setName(String n)   { this.name  = n; }
    public void setPhone(String p)  { this.phone = p; }
    public void setEmail(String e)  { this.email = e; }

    /**
     * POLYMORPHISM — each subclass overrides this to return its role.
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return id + " — " + name + " [" + getRole() + "]";
    }
}