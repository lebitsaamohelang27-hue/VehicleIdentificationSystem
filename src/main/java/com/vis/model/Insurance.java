package com.vis.model;

import javafx.beans.property.*;

/**
 * Insurance model — demonstrates Inheritance by extending Person
 * (insurance agent is a person in the system).
 */
public class Insurance {

    private final IntegerProperty policyId       = new SimpleIntegerProperty();
    private final IntegerProperty vehicleId      = new SimpleIntegerProperty();
    private final StringProperty  vehicleReg     = new SimpleStringProperty();
    private final StringProperty  policyNumber   = new SimpleStringProperty();
    private final StringProperty  insuranceCompany = new SimpleStringProperty();
    private final StringProperty  startDate      = new SimpleStringProperty();
    private final StringProperty  endDate        = new SimpleStringProperty();
    private final DoubleProperty  premiumAmount  = new SimpleDoubleProperty();

    public Insurance() {}

    public Insurance(int policyId, int vehicleId, String vehicleReg, String policyNumber,
                     String insuranceCompany, String startDate, String endDate, double premiumAmount) {
        this.policyId.set(policyId);
        this.vehicleId.set(vehicleId);
        this.vehicleReg.set(vehicleReg != null ? vehicleReg : "");
        this.policyNumber.set(policyNumber != null ? policyNumber : "");
        this.insuranceCompany.set(insuranceCompany != null ? insuranceCompany : "");
        this.startDate.set(startDate != null ? startDate : "");
        this.endDate.set(endDate != null ? endDate : "");
        this.premiumAmount.set(premiumAmount);
    }

    public int    getPolicyId()              { return policyId.get(); }
    public void   setPolicyId(int v)         { policyId.set(v); }
    public IntegerProperty policyIdProperty() { return policyId; }

    public int    getVehicleId()             { return vehicleId.get(); }
    public void   setVehicleId(int v)        { vehicleId.set(v); }
    public IntegerProperty vehicleIdProperty() { return vehicleId; }

    public String getVehicleReg()            { return vehicleReg.get(); }
    public void   setVehicleReg(String v)    { vehicleReg.set(v); }
    public StringProperty vehicleRegProperty() { return vehicleReg; }

    public String getPolicyNumber()          { return policyNumber.get(); }
    public void   setPolicyNumber(String v)  { policyNumber.set(v); }
    public StringProperty policyNumberProperty() { return policyNumber; }

    public String getInsuranceCompany()          { return insuranceCompany.get(); }
    public void   setInsuranceCompany(String v)  { insuranceCompany.set(v); }
    public StringProperty insuranceCompanyProperty() { return insuranceCompany; }

    public String getStartDate()             { return startDate.get(); }
    public void   setStartDate(String v)     { startDate.set(v); }
    public StringProperty startDateProperty() { return startDate; }

    public String getEndDate()               { return endDate.get(); }
    public void   setEndDate(String v)       { endDate.set(v); }
    public StringProperty endDateProperty()  { return endDate; }

    public double getPremiumAmount()         { return premiumAmount.get(); }
    public void   setPremiumAmount(double v) { premiumAmount.set(v); }
    public DoubleProperty premiumAmountProperty() { return premiumAmount; }
}