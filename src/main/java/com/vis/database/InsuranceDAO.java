package com.vis.database;

import com.vis.model.Insurance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for InsurancePolicy table.
 */
public class InsuranceDAO {

    private final DatabaseManager dbManager;

    public InsuranceDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public List<Insurance> getAllPolicies() throws SQLException {
        List<Insurance> list = new ArrayList<>();
        String sql = "SELECT ip.policy_id, ip.vehicle_id, v.registration_number, ip.policy_number, " +
                "ip.insurance_company, CAST(ip.start_date AS VARCHAR), CAST(ip.end_date AS VARCHAR), ip.premium_amount " +
                "FROM InsurancePolicy ip JOIN Vehicle v ON ip.vehicle_id = v.vehicle_id " +
                "ORDER BY ip.end_date DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void addPolicy(Insurance ins) throws SQLException {
        String sql = "INSERT INTO InsurancePolicy (vehicle_id, policy_number, insurance_company, start_date, end_date, premium_amount) " +
                "VALUES (?,?,?,?,?,?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ins.getVehicleId());
            ps.setString(2, ins.getPolicyNumber());
            ps.setString(3, ins.getInsuranceCompany());
            ps.setDate(4, Date.valueOf(ins.getStartDate()));
            ps.setDate(5, Date.valueOf(ins.getEndDate()));
            ps.setDouble(6, ins.getPremiumAmount());
            ps.executeUpdate();
        }
    }

    public void updatePolicy(Insurance ins) throws SQLException {
        String sql = "UPDATE InsurancePolicy SET policy_number=?, insurance_company=?, " +
                "start_date=?, end_date=?, premium_amount=? WHERE policy_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ins.getPolicyNumber());
            ps.setString(2, ins.getInsuranceCompany());
            ps.setDate(3, Date.valueOf(ins.getStartDate()));
            ps.setDate(4, Date.valueOf(ins.getEndDate()));
            ps.setDouble(5, ins.getPremiumAmount());
            ps.setInt(6, ins.getPolicyId());
            ps.executeUpdate();
        }
    }

    public void deletePolicy(int policyId) throws SQLException {
        String sql = "DELETE FROM InsurancePolicy WHERE policy_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, policyId);
            ps.executeUpdate();
        }
    }

    private Insurance map(ResultSet rs) throws SQLException {
        return new Insurance(
                rs.getInt("policy_id"),
                rs.getInt("vehicle_id"),
                rs.getString("registration_number"),
                rs.getString("policy_number"),
                rs.getString("insurance_company"),
                rs.getString(6), // start_date
                rs.getString(7), // end_date
                rs.getDouble("premium_amount")
        );
    }
}