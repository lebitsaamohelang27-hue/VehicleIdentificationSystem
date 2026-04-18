package com.vis.database;

import com.vis.model.ServiceRecord;
import com.vis.model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Vehicle and ServiceRecord tables.
 */
public class VehicleDAO {

    private final DatabaseManager dbManager;

    public VehicleDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    // ══════════════════════════════════════════
    //  VEHICLE CRUD
    // ══════════════════════════════════════════

    public List<Vehicle> getAllVehicles() throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT v.vehicle_id, v.registration_number, v.make, v.model, v.year, " +
                "v.owner_id, COALESCE(c.name, '—') AS owner_name " +
                "FROM Vehicle v LEFT JOIN Customer c ON v.owner_id = c.customer_id " +
                "ORDER BY v.registration_number";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapVehicle(rs));
        }
        return list;
    }

    public void addVehicle(Vehicle v) throws SQLException {
        String sql = "INSERT INTO Vehicle (registration_number, make, model, year, owner_id) VALUES (?,?,?,?,?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getRegistrationNumber());
            ps.setString(2, v.getMake());
            ps.setString(3, v.getModel());
            ps.setInt(4, v.getYear());
            ps.setInt(5, v.getOwnerId());
            ps.executeUpdate();
        }
    }

    public void updateVehicle(Vehicle v) throws SQLException {
        String sql = "UPDATE Vehicle SET registration_number=?, make=?, model=?, year=?, owner_id=? WHERE vehicle_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getRegistrationNumber());
            ps.setString(2, v.getMake());
            ps.setString(3, v.getModel());
            ps.setInt(4, v.getYear());
            ps.setInt(5, v.getOwnerId());
            ps.setInt(6, v.getId());
            ps.executeUpdate();
        }
    }

    public void deleteVehicle(int vehicleId) throws SQLException {
        String sql = "DELETE FROM Vehicle WHERE vehicle_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.executeUpdate();
        }
    }

    // ══════════════════════════════════════════
    //  SERVICE RECORD CRUD
    // ══════════════════════════════════════════

    public List<ServiceRecord> getAllServiceRecords() throws SQLException {
        List<ServiceRecord> list = new ArrayList<>();
        String sql = "SELECT s.service_id, s.vehicle_id, v.registration_number, " +
                "CAST(s.service_date AS VARCHAR), s.service_type, s.description, s.cost " +
                "FROM ServiceRecord s JOIN Vehicle v ON s.vehicle_id = v.vehicle_id " +
                "ORDER BY s.service_date DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapService(rs));
        }
        return list;
    }

    public void addServiceRecord(ServiceRecord sr) throws SQLException {
        String sql = "INSERT INTO ServiceRecord (vehicle_id, service_date, service_type, description, cost) " +
                "VALUES (?,?,?,?,?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sr.getVehicleId());
            ps.setDate(2, Date.valueOf(sr.getServiceDate()));
            ps.setString(3, sr.getServiceType());
            ps.setString(4, sr.getDescription());
            ps.setDouble(5, sr.getCost());
            ps.executeUpdate();
        }
    }

    public void deleteServiceRecord(int serviceId) throws SQLException {
        String sql = "DELETE FROM ServiceRecord WHERE service_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            ps.executeUpdate();
        }
    }

    // ══════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════

    /** Load all customers as "id - name" strings for ComboBox */
    public List<String> getCustomerOptions() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT customer_id, name FROM Customer ORDER BY name";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(rs.getInt("customer_id") + " | " + rs.getString("name"));
        }
        return list;
    }

    /** Load all vehicles as "id | reg - make model" strings for ComboBox */
    public List<String> getVehicleOptions() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT vehicle_id, registration_number, make, model FROM Vehicle ORDER BY registration_number";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(rs.getInt("vehicle_id") + " | " + rs.getString("registration_number")
                        + " - " + rs.getString("make") + " " + rs.getString("model"));
        }
        return list;
    }

    private Vehicle mapVehicle(ResultSet rs) throws SQLException {
        return new Vehicle(
                rs.getInt("vehicle_id"),
                rs.getString("registration_number"),
                rs.getString("make"),
                rs.getString("model"),
                rs.getInt("year"),
                rs.getInt("owner_id"),
                rs.getString("owner_name")
        );
    }

    private ServiceRecord mapService(ResultSet rs) throws SQLException {
        return new ServiceRecord(
                rs.getInt("service_id"),
                rs.getInt("vehicle_id"),
                rs.getString("registration_number"),
                rs.getString(4), // service_date as varchar
                rs.getString("service_type"),
                rs.getString("description"),
                rs.getDouble("cost")
        );
    }
}