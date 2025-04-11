package dao;

import entity.Asset;

import exception.AssetNotFoundException;
import exception.AssetNotMaintainException;
import util.DBConnUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AssetManagementServiceImpl implements AssetManagementService {
    private Connection conn;

    public AssetManagementServiceImpl() {
        try {
            // Get DB Connection using utility
            conn = DBConnUtil.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addAsset(Asset asset) {
        String query = "INSERT INTO assets (asset_id, name, type, serial_number, purchase_date, location, status, owner_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, asset.getAssetId());
            pstmt.setString(2, asset.getName());
            pstmt.setString(3, asset.getType());
            pstmt.setString(4, asset.getSerialNumber());
            pstmt.setString(5, asset.getPurchaseDate());
            pstmt.setString(6, asset.getLocation());
            pstmt.setString(7, asset.getStatus());
            pstmt.setInt(8, asset.getOwnerId());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean updateAsset(Asset asset) {
        String query = "UPDATE assets SET name = ?, type = ?, serial_number = ?, purchase_date = ?, location = ?, status = ?, owner_id = ? WHERE asset_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, asset.getName());
            pstmt.setString(2, asset.getType());
            pstmt.setString(3, asset.getSerialNumber());
            pstmt.setString(4, asset.getPurchaseDate());
            pstmt.setString(5, asset.getLocation());
            pstmt.setString(6, asset.getStatus());
            pstmt.setInt(7, asset.getOwnerId());
            pstmt.setInt(8, asset.getAssetId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean deleteAsset(int assetId) throws AssetNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnUtil.getConnection();
            String query = "DELETE FROM assets WHERE asset_id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, assetId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new AssetNotFoundException("Asset with ID " + assetId + " not found.");
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

        @Override
        public boolean allocateAsset(int assetId, int employeeId, String allocationDate) throws AssetNotFoundException {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = DBConnUtil.getConnection();

                
                String checkQuery = "SELECT * FROM assets WHERE asset_id = ?";
                stmt = conn.prepareStatement(checkQuery);
                stmt.setInt(1, assetId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw new AssetNotFoundException("Asset with ID " + assetId + " not found.");
                }

                // Allocate asset
                String query = "INSERT INTO asset_allocations (asset_id, employee_id, allocation_date) VALUES (?, ?, ?)";
                stmt = conn.prepareStatement(query);
                stmt.setInt(1, assetId);
                stmt.setInt(2, employeeId);
                stmt.setString(3, allocationDate);
                int rows = stmt.executeUpdate();

                return rows > 0;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }



    @Override
    public boolean deallocateAsset(int assetId, int employeeId, String returnDate) {
        String query = "UPDATE asset_allocations SET return_date = ? WHERE asset_id = ? AND employee_id = ? AND return_date IS NULL";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(returnDate));  // Format: "yyyy-mm-dd"
            pstmt.setInt(2, assetId);
            pstmt.setInt(3, employeeId);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean performMaintenance(int assetId, String maintenanceDate, String description, double cost) throws AssetNotFoundException, AssetNotMaintainException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnUtil.getConnection();
            String checkAssetQuery = "SELECT status FROM assets WHERE asset_id = ?";
            stmt = conn.prepareStatement(checkAssetQuery);
            stmt.setInt(1, assetId);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new AssetNotFoundException("Asset with ID " + assetId + " not found.");
            }
            String assetStatus = rs.getString("status");
            if ("Retired".equalsIgnoreCase(assetStatus)) {
                throw new AssetNotMaintainException("Cannot perform maintenance on a retired asset.");
            }
            if ("Damaged".equalsIgnoreCase(assetStatus)) {
                throw new AssetNotMaintainException("Cannot perform maintenance on a damaged asset.");
            }
            String insertQuery = "INSERT INTO maintenance_records (asset_id, maintenance_date, description, cost) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(insertQuery);
            stmt.setInt(1, assetId);
            stmt.setString(2, maintenanceDate);
            stmt.setString(3, description);
            stmt.setDouble(4, cost);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public boolean reserveAsset(int assetId, int employeeId, String reservationDate, String startDate, String endDate) throws AssetNotFoundException, AssetNotMaintainException {

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnUtil.getConnection();

            // Check if asset exists
            String checkQuery = "SELECT * FROM assets WHERE asset_id = ?";
            stmt = conn.prepareStatement(checkQuery);
            stmt.setInt(1, assetId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new AssetNotFoundException("Asset with ID " + assetId + " not found.");
            }

            // Check last maintenance date
            String lastMaintenanceQuery = "SELECT MAX(maintenance_date) AS last_date FROM maintenance_records WHERE asset_id = ?";
            stmt = conn.prepareStatement(lastMaintenanceQuery);
            stmt.setInt(1, assetId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String lastDate = rs.getString("last_date");
                if (lastDate != null) {
                    LocalDate lastMaintenance = LocalDate.parse(lastDate);
                    if (lastMaintenance.isBefore(LocalDate.now().minusYears(2))) {
                        throw new AssetNotMaintainException("Asset has not been maintained in the last 2 years.");
                    }
                }
            }

            // Reserve the asset
            String insertQuery = "INSERT INTO reservations (asset_id, employee_id, reservation_date, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(insertQuery);
            stmt.setInt(1, assetId);
            stmt.setInt(2, employeeId);
            stmt.setString(3, reservationDate);
            stmt.setString(4, startDate);
            stmt.setString(5, endDate);
            stmt.setString(6, "pending");
            int rows = stmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    @Override
    public boolean withdrawReservation(int reservationId) {
        String query = "UPDATE reservations SET status = ? WHERE reservation_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "canceled");
            pstmt.setInt(2, reservationId);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Asset> getAllAssets() {
        List<Asset> assetList = new ArrayList<>();
        String query = "SELECT * FROM assets";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Asset asset = new Asset();
                asset.setAssetId(rs.getInt("asset_id"));
                asset.setName(rs.getString("name"));
                asset.setType(rs.getString("type"));
                asset.setSerialNumber(rs.getString("serial_number"));
                asset.setPurchaseDate(rs.getString("purchase_date"));
                asset.setLocation(rs.getString("location"));
                asset.setStatus(rs.getString("status"));
                asset.setOwnerId(rs.getInt("owner_id"));

                assetList.add(asset);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assetList;
    }


}
