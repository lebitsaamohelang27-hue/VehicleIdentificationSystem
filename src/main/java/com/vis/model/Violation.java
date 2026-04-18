package com.vis.model;

import javafx.beans.property.*;

public class Violation {

    private final IntegerProperty violationId   = new SimpleIntegerProperty();
    private final IntegerProperty vehicleId     = new SimpleIntegerProperty();
    private final StringProperty  vehicleReg    = new SimpleStringProperty();
    private final StringProperty  violationDate = new SimpleStringProperty();
    private final StringProperty  violationType = new SimpleStringProperty();
    private final DoubleProperty  fineAmount    = new SimpleDoubleProperty();
    private final StringProperty  status        = new SimpleStringProperty();

    public Violation() {}

    public Violation(int violationId, int vehicleId, String vehicleReg,
                     String violationDate, String violationType, double fineAmount, String status) {
        this.violationId.set(violationId);
        this.vehicleId.set(vehicleId);
        this.vehicleReg.set(vehicleReg != null ? vehicleReg : "");
        this.violationDate.set(violationDate != null ? violationDate : "");
        this.violationType.set(violationType != null ? violationType : "");
        this.fineAmount.set(fineAmount);
        this.status.set(status != null ? status : "Unpaid");
    }

    public int    getViolationId()          { return violationId.get(); }
    public void   setViolationId(int v)     { violationId.set(v); }
    public IntegerProperty violationIdProperty() { return violationId; }

    public int    getVehicleId()            { return vehicleId.get(); }
    public void   setVehicleId(int v)       { vehicleId.set(v); }
    public IntegerProperty vehicleIdProperty() { return vehicleId; }

    public String getVehicleReg()           { return vehicleReg.get(); }
    public void   setVehicleReg(String v)   { vehicleReg.set(v); }
    public StringProperty vehicleRegProperty() { return vehicleReg; }

    public String getViolationDate()        { return violationDate.get(); }
    public void   setViolationDate(String v){ violationDate.set(v); }
    public StringProperty violationDateProperty() { return violationDate; }

    public String getViolationType()        { return violationType.get(); }
    public void   setViolationType(String v){ violationType.set(v); }
    public StringProperty violationTypeProperty() { return violationType; }

    public double getFineAmount()           { return fineAmount.get(); }
    public void   setFineAmount(double v)   { fineAmount.set(v); }
    public DoubleProperty fineAmountProperty() { return fineAmount; }

    public String getStatus()               { return status.get(); }
    public void   setStatus(String v)       { status.set(v); }
    public StringProperty statusProperty()  { return status; }
}