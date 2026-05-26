package dao;

import models.Bug;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BugDAO {

    public void initializeTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS bugs (" +
                     "id SERIAL PRIMARY KEY, " +
                     "title VARCHAR(100), " +
                     "severity VARCHAR(50), " +
                     "assigned_to VARCHAR(100), " +
                     "status VARCHAR(50)" +
                     ")";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public int insertBug(Bug bug) throws SQLException {
        String sql = "INSERT INTO bugs (title, severity, assigned_to, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, bug.getTitle());
            pstmt.setString(2, bug.getSeverity());
            pstmt.setString(3, bug.getAssignedTo());
            pstmt.setString(4, bug.getStatus());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    bug.setId(generatedId);
                    return generatedId;
                } else {
                    throw new SQLException("Inserting bug failed, no ID obtained.");
                }
            }
        }
    }

    public List<Bug> getAllBugs() throws SQLException {
        List<Bug> bugs = new ArrayList<>();
        String sql = "SELECT id, title, severity, assigned_to, status FROM bugs ORDER BY id ASC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String severity = rs.getString("severity");
                String assignedTo = rs.getString("assigned_to");
                String status = rs.getString("status");
                
                Bug bug = new Bug(id, title, assignedTo, severity, status);
                bugs.add(bug);
            }
        }
        return bugs;
    }

    public boolean updateStatus(int id, String newStatus) throws SQLException {
        String sql = "UPDATE bugs SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean deleteBug(int id) throws SQLException {
        String sql = "DELETE FROM bugs WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
