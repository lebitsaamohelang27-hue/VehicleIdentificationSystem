package com.vis.database;

import com.vis.model.PoliceReport;
import com.vis.model.Violation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for PoliceReport and Violation tables.
 */
public class PoliceDAO {

    private final DatabaseManager dbManager;

    public PoliceDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    // ══════════════════════════════════════════
    //  POLICE REPORT CRUD
    // ══════════════════════════════════════════

    public List<PoliceReport> getAllReports() throws SQLException {
        List<PoliceReport> list = new ArrayList<>();
        String sql = "SELECT p.report_id, p.vehicle_id, v.registration_number, " +
                "CAST(p.report_date AS VARCHAR), p.report_type, p.description, p.officer_name " +
                "FROM PoliceReport p JOIN Vehicle v ON p.vehicle_id = v.vehicle_id " +
                "ORDER BY p.report_date DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapReport(rs));
        }
        return list;
    }

    public void addReport(PoliceReport r) throws SQLException {
        String sql = "INSERT INTO PoliceReport (vehicle_id, report_date, report_type, description, officer_name) " +
                "VALUES (?,?,?,?,?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getVehicleId());
            ps.setDate(2, Date.valueOf(r.getReportDate()));
            ps.setString(3, r.getReportType());
            ps.setString(4, r.getDescription());
            ps.setString(5, r.getOfficerName());
            ps.executeUpdate();
        }
    }

    public void deleteReport(int reportId) throws SQLException {
        String sql = "DELETE FROM PoliceReport WHERE report_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            ps.executeUpdate();
        }
    }

    // ══════════════════════════════════════════
    //  VIOLATION CRUD
    // ══════════════════════════════════════════

    public List<Violation> getAllViolations() throws SQLException {
        List<Violation> list = new ArrayList<>();
        String sql = "SELECT v.violation_id, v.vehicle_id, vh.registration_number, " +
                "CAST(v.violation_date AS VARCHAR), v.violation_type, v.fine_amount, v.status " +
                "FROM Violation v JOIN Vehicle vh ON v.vehicle_id = vh.vehicle_id " +
                "ORDER BY v.violation_date DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapViolation(rs));
        }
        return list;
    }

    public void addViolation(Violation v) throws SQLException {
        String sql = "INSERT INTO Violation (vehicle_id, violation_date, violation_type, fine_amount, status) " +
                "VALUES (?,?,?,?,'Unpaid')";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, v.getVehicleId());
            ps.setDate(2, Date.valueOf(v.getViolationDate()));
            ps.setString(3, v.getViolationType());
            ps.setDouble(4, v.getFineAmount());
            ps.executeUpdate();
        }
    }

    public void updateViolationStatus(int violationId, String status) throws SQLException {
        String sql = "UPDATE Violation SET status=? WHERE violation_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, violationId);
            ps.executeUpdate();
        }
    }

    public void deleteViolation(int violationId) throws SQLException {
        String sql = "DELETE FROM Violation WHERE violation_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, violationId);
            ps.executeUpdate();
        }
    }

    // ══════════════════════════════════════════
    //  MAPPERS
    // ══════════════════════════════════════════

    private PoliceReport mapReport(ResultSet rs) throws SQLException {
        return new PoliceReport(
                rs.getInt("report_id"),
                rs.getInt("vehicle_id"),
                rs.getString("registration_number"),
                rs.getString(4),
                rs.getString("report_type"),
                rs.getString("description"),
                rs.getString("officer_name")
        );
    }

    private Violation mapViolation(ResultSet rs) throws SQLException {
        return new Violation(
                rs.getInt("violation_id"),
                rs.getInt("vehicle_id"),
                rs.getString("registration_number"),
                rs.getString(4),
                rs.getString("violation_type"),
                rs.getDouble("fine_amount"),
                rs.getString("status")
        );
    }
}