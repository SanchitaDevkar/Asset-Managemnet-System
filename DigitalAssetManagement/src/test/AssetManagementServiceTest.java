package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.AssetManagementServiceImpl;
import entity.Asset;
import exception.AssetNotFoundException;
import exception.AssetNotMaintainException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import util.DBConnUtil;

public class AssetManagementServiceTest {

    AssetManagementServiceImpl service;
    
    @BeforeEach
    void setUp() {
        service = new AssetManagementServiceImpl();
        
        try (Connection conn = DBConnUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM assets");
            stmt.executeUpdate("DELETE FROM maintenance_records");
            
            Asset maintainableAsset = new Asset(1, "Laptop", "Computer", "LP789012", "2024-01-15", "Office", "Available", 2);
            service.addAsset(maintainableAsset);
            
            Asset retiredAsset = new Asset(999, "Old Server", "Server", "SRV34567", "2019-05-20", "Storage", "Retired", 1);
            service.addAsset(retiredAsset);
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to clean and setup database before test: " + e.getMessage());
        }
    }

    

    

    @Test
    void testAddAsset() {
        String uniqueSerialNumber = "KB" + UUID.randomUUID().toString().substring(0, 10);
        Asset asset = new Asset(0, "Keyboard", "Peripheral", uniqueSerialNumber, "2023-11-01", "Office", "Available", 1);
        assertTrue(service.addAsset(asset));
    }

    @Test
    void testAssetNotFoundException() {
        assertThrows(AssetNotFoundException.class, () -> {
            service.allocateAsset(9999, 1, "2024-04-01");
        });
    }

    @Test
    void testMaintenance() throws AssetNotMaintainException, AssetNotFoundException {
        
        assertTrue(service.performMaintenance(1, "2024-03-01", "Routine check", 150.0));
    }

    @Test
    void testReservation() throws AssetNotFoundException, AssetNotMaintainException {
        
        assertTrue(service.reserveAsset(1, 1, "2024-04-01", "2024-04-05", "2024-04-10"));
    }

    @Test
    void testAssetNotMaintainException() {
        

        try {
            
            service.performMaintenance(999, "2024-04-01", "Attempted Maintenance", 50.0);
            
            fail("AssetNotMaintainException was not thrown for asset ID 999.");
        } catch (AssetNotMaintainException e) {
            
            assertTrue(true);
            
        } catch (AssetNotFoundException e) {
            
            fail("AssetNotFoundException was thrown instead of AssetNotMaintainException: " + e.getMessage());
        }
    }
}