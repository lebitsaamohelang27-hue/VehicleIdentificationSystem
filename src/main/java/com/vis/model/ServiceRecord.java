package com.vis.model;

import javafx.beans.property.*;

public class ServiceRecord {

    private final IntegerProperty serviceId   = new SimpleIntegerProperty();
    private final IntegerProperty vehicleId   = new SimpleIntegerProperty();
    private final StringProperty  vehicleReg  = new SimpleStringProperty(); // display
    private final StringProperty  serviceDate = new SimpleStringProperty();
    private final StringProperty  serviceType = new SimpleStringProperty();
    private final StringProperty  description = new SimpleStringProperty();
    private final DoubleProperty  cost        = new SimpleDoubleProperty();

    public ServiceRecord() {}

    public ServiceRecord(int serviceId, int vehicleId, String vehicleReg,
                         String serviceDate, String serviceType, String description, double cost) {
        this.serviceId.set(serviceId);
        this.vehicleId.set(vehicleId);
        this.vehicleReg.set(vehicleReg != null ? vehicleReg : "");
        this.serviceDate.set(serviceDate != null ? serviceDate : "");
        this.serviceType.set(serviceType != null ? serviceType : "");
        this.description.set(description != null ? description : "");
        this.cost.set(cost);
    }

    public int    getServiceId()          { return serviceId.get(); }
    public void   setServiceId(int v)     { serviceId.set(v); }
    public IntegerProperty serviceIdProperty() { return serviceId; }

    public int    getVehicleId()          { return vehicleId.get(); }
    public void   setVehicleId(int v)     { vehicleId.set(v); }
    public IntegerProperty vehicleIdProperty() { return vehicleId; }

    public String getVehicleReg()         { return vehicleReg.get(); }
    public void   setVehicleReg(String v) { vehicleReg.set(v); }
    public StringProperty vehicleRegProperty() { return vehicleReg; }

    public String getServiceDate()         { return serviceDate.get(); }
    public void   setServiceDate(String v) { serviceDate.set(v); }
    public StringProperty serviceDateProperty() { return serviceDate; }

    public String getServiceType()         { return serviceType.get(); }
    public void   setServiceType(String v) { serviceType.set(v); }
    public StringProperty serviceTypeProperty() { return serviceType; }

    public String getDescription()         { return description.get(); }
    public void   setDescription(String v) { description.set(v); }
    public StringProperty descriptionProperty() { return description; }

    public double getCost()         { return cost.get(); }
    public void   setCost(double v) { cost.set(v); }
    public DoubleProperty costProperty() { return cost; }
}