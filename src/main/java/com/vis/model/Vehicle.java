package com.vis.model;

import javafx.beans.property.*;

/**
 * Vehicle model — extends Person concept via a base Entity class.
 * Demonstrates JavaFX properties for TableView binding.
 */
public class Vehicle {

    private final IntegerProperty id                 = new SimpleIntegerProperty();
    private final StringProperty  registrationNumber = new SimpleStringProperty();
    private final StringProperty  make               = new SimpleStringProperty();
    private final StringProperty  model              = new SimpleStringProperty();
    private final IntegerProperty year               = new SimpleIntegerProperty();
    private final IntegerProperty ownerId            = new SimpleIntegerProperty();
    private final StringProperty  ownerName          = new SimpleStringProperty(); // for display

    public Vehicle() {}

    public Vehicle(int id, String registrationNumber, String make, String model, int year, int ownerId, String ownerName) {
        this.id.set(id);
        this.registrationNumber.set(registrationNumber);
        this.make.set(make);
        this.model.set(model);
        this.year.set(year);
        this.ownerId.set(ownerId);
        this.ownerName.set(ownerName != null ? ownerName : "");
    }

    // --- ID ---
    public int getId()           { return id.get(); }
    public void setId(int v)     { id.set(v); }
    public IntegerProperty idProperty() { return id; }

    // --- Registration ---
    public String getRegistrationNumber()            { return registrationNumber.get(); }
    public void   setRegistrationNumber(String v)    { registrationNumber.set(v); }
    public StringProperty registrationNumberProperty(){ return registrationNumber; }

    // --- Make ---
    public String getMake()         { return make.get(); }
    public void   setMake(String v) { make.set(v); }
    public StringProperty makeProperty() { return make; }

    // --- Model ---
    public String getModel()         { return model.get(); }
    public void   setModel(String v) { model.set(v); }
    public StringProperty modelProperty() { return model; }

    // --- Year ---
    public int  getYear()        { return year.get(); }
    public void setYear(int v)   { year.set(v); }
    public IntegerProperty yearProperty() { return year; }

    // --- Owner ID ---
    public int  getOwnerId()      { return ownerId.get(); }
    public void setOwnerId(int v) { ownerId.set(v); }
    public IntegerProperty ownerIdProperty() { return ownerId; }

    // --- Owner Name (display) ---
    public String getOwnerName()         { return ownerName.get(); }
    public void   setOwnerName(String v) { ownerName.set(v); }
    public StringProperty ownerNameProperty() { return ownerName; }

    @Override
    public String toString() {
        return registrationNumber.get() + " - " + make.get() + " " + model.get();
    }
}