package com.vis.database;

import com.vis.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Customer table.
 * All SQL exceptions are caught and re-thrown so the Controller can handle them.
 */
public class CustomerDAO {

    private final DatabaseManager dbManager;

    public CustomerDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /** Return all customers ordered by name */
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT customer_id, name, address, phone, email FROM Customer ORDER BY name";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    /** Insert a new customer; returns the generated ID */
    public int addCustomer(Customer c) throws SQLException {
        String sql = "INSERT INTO Customer (name, address, phone, email) VALUES (?, ?, ?, ?) RETURNING customer_id";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getAddress());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getEmail());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    /** Update an existing customer by ID */
    public void updateCustomer(Customer c) throws SQLException {
        String sql = "UPDATE Customer SET name=?, address=?, phone=?, email=? WHERE customer_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getAddress());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getEmail());
            ps.setInt(5, c.getId());
            ps.executeUpdate();
        }
    }

    /** Delete a customer by ID */
    public void deleteCustomer(int id) throws SQLException {
        String sql = "DELETE FROM Customer WHERE customer_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Map a ResultSet row to a Customer object */
    private Customer map(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("customer_id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getString("email")
        );
    }
}