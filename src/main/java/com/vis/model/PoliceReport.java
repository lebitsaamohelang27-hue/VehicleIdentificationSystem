package com.vis.model;

import javafx.beans.property.*;

public class PoliceReport {

    private final IntegerProperty reportId    = new SimpleIntegerProperty();
    private final IntegerProperty vehicleId   = new SimpleIntegerProperty();
    private final StringProperty  vehicleReg  = new SimpleStringProperty();
    private final StringProperty  reportDate  = new SimpleStringProperty();
    private final StringProperty  reportType  = new SimpleStringProperty();
    private final StringProperty  description = new SimpleStringProperty();
    private final StringProperty  officerName = new SimpleStringProperty();

    public PoliceReport() {}

    public PoliceReport(int reportId, int vehicleId, String vehicleReg,
                        String reportDate, String reportType, String description, String officerName) {
        this.reportId.set(reportId);
        this.vehicleId.set(vehicleId);
        this.vehicleReg.set(vehicleReg != null ? vehicleReg : "");
        this.reportDate.set(reportDate != null ? reportDate : "");
        this.reportType.set(reportType != null ? reportType : "");
        this.description.set(description != null ? description : "");
        this.officerName.set(officerName != null ? officerName : "");
    }

    public int    getReportId()             { return reportId.get(); }
    public void   setReportId(int v)        { reportId.set(v); }
    public IntegerProperty reportIdProperty() { return reportId; }

    public int    getVehicleId()            { return vehicleId.get(); }
    public void   setVehicleId(int v)       { vehicleId.set(v); }
    public IntegerProperty vehicleIdProperty() { return vehicleId; }

    public String getVehicleReg()           { return vehicleReg.get(); }
    public void   setVehicleReg(String v)   { vehicleReg.set(v); }
    public StringProperty vehicleRegProperty() { return vehicleReg; }

    public String getReportDate()           { return reportDate.get(); }
    public void   setReportDate(String v)   { reportDate.set(v); }
    public StringProperty reportDateProperty() { return reportDate; }

    public String getReportType()           { return reportType.get(); }
    public void   setReportType(String v)   { reportType.set(v); }
    public StringProperty reportTypeProperty() { return reportType; }

    public String getDescription()          { return description.get(); }
    public void   setDescription(String v)  { description.set(v); }
    public StringProperty descriptionProperty() { return description; }

    public String getOfficerName()          { return officerName.get(); }
    public void   setOfficerName(String v)  { officerName.set(v); }
    public StringProperty officerNameProperty() { return officerName; }
}