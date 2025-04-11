package dao;

import java.util.List;
import entity.Asset;
import exception.AssetNotFoundException;
import exception.AssetNotMaintainException;

public interface AssetManagementService {

    boolean addAsset(Asset asset);

    boolean updateAsset(Asset asset);

    boolean deleteAsset(int assetId) throws AssetNotFoundException;

    boolean allocateAsset(int assetId, int employeeId, String allocationDate) throws AssetNotFoundException;

    boolean deallocateAsset(int assetId, int employeeId, String returnDate) throws AssetNotFoundException;

    boolean performMaintenance(int assetId, String maintenanceDate, String description, double cost) throws AssetNotFoundException, AssetNotMaintainException;

    boolean reserveAsset(int assetId, int employeeId, String reservationDate, String startDate, String endDate) throws AssetNotFoundException, AssetNotMaintainException;

    boolean withdrawReservation(int reservationId);

    List<Asset> getAllAssets();
}