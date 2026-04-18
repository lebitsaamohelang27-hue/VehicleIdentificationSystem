package com.vis.model;

/**
 * Officer — extends Person (INHERITANCE).
 * A police officer is a Person with a badge number.
 * getRole() is POLYMORPHISM — overrides abstract Person.getRole().
 */
public class Officer extends Person {

    private String badgeNumber;
    private String rank;

    public Officer() {}

    public Officer(int id, String name, String phone, String email, String badgeNumber, String rank) {
        super(id, name, phone, email);
        this.badgeNumber = badgeNumber;
        this.rank        = rank;
    }

    public String getBadgeNumber()         { return badgeNumber; }
    public void   setBadgeNumber(String v) { this.badgeNumber = v; }

    public String getRank()                { return rank; }
    public void   setRank(String v)        { this.rank = v; }

    /** POLYMORPHISM — overrides abstract Person.getRole() */
    @Override
    public String getRole() { return "Police Officer"; }
}