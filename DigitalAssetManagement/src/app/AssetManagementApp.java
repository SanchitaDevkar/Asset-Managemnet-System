package app;

import dao.AssetManagementService;
import dao.AssetManagementServiceImpl;
import entity.Asset;
import exception.AssetNotFoundException;
import exception.AssetNotMaintainException;

import java.util.Scanner;

public class AssetManagementApp {
    public static void main(String[] args) throws AssetNotMaintainException {
        Scanner sc = new Scanner(System.in);
        AssetManagementService service = new AssetManagementServiceImpl();

        int choice;
        do {
            System.out.println("\n===== Digital Asset Management System =====");
            System.out.println("1. View All Assets");
            System.out.println("2. Add Asset");
            System.out.println("3. Delete Asset");
            System.out.println("4. Allocate Asset");
            System.out.println("5. Perform Maintenance");
            System.out.println("6. Reserve Asset");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    service.getAllAssets().forEach(System.out::println);
                    break;

                case 2:
                    sc.nextLine(); // Consume the newline left-over
                    System.out.print("Enter Asset Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Asset Type: ");
                    String type = sc.nextLine();
                    System.out.print("Enter Purchase Date (yyyy-mm-dd): ");
                    String date = sc.nextLine();
                    

                    Asset asset = new Asset();
                    asset.setName(name);
                    asset.setType(type);
                    asset.setPurchaseDate(date);
                    asset.setSerialNumber("AUTO-GEN-" + System.currentTimeMillis());
 // or get input from user
                    asset.setLocation("Default"); // or get input
                    asset.setStatus("Available"); // default status
                    asset.setOwnerId(1); // hardcoded or you can take input from user

                    service.addAsset(asset);
                    System.out.println("Asset added successfully.");
                    break;


                case 3:
                    System.out.print("Enter Asset ID to delete: ");
                    int deleteId = sc.nextInt();
                    try {
                        boolean deleted = service.deleteAsset(deleteId);
                        if (deleted) {
                            System.out.println("Asset deleted successfully.");
                        }
                    } catch (AssetNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 4:
                    System.out.print("Enter Asset ID to allocate: ");
                    int allocateAssetId = sc.nextInt();
                    System.out.print("Enter Employee ID: ");
                    int empId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Allocation Date (yyyy-mm-dd): ");
                    String allocationDate = sc.nextLine();
                    try {
                        boolean allocated = service.allocateAsset(allocateAssetId, empId, allocationDate);
                        if (allocated) {
                            System.out.println("Asset allocated successfully.");
                        }
                    } catch (AssetNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 5:
                    System.out.print("Enter Asset ID for maintenance: ");
                    int maintAssetId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Maintenance Date (yyyy-mm-dd): ");
                    String mDate = sc.nextLine();
                    System.out.print("Enter Description: ");
                    String desc = sc.nextLine();
                    System.out.print("Enter Cost: ");
                    double mCost = sc.nextDouble();
                    try {
                        boolean maintained = service.performMaintenance(maintAssetId, mDate, desc, mCost);
                        if (maintained) {
                            System.out.println("Maintenance record added successfully.");
                        }
                    } catch (AssetNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 6:
                    System.out.print("Enter Asset ID to reserve: ");
                    int resAssetId = sc.nextInt();
                    System.out.print("Enter Employee ID: ");
                    int resEmpId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Reservation Date (yyyy-mm-dd): ");
                    String resDate = sc.nextLine();
                    System.out.print("Enter Start Date (yyyy-mm-dd): ");
                    String startDate = sc.nextLine();
                    System.out.print("Enter End Date (yyyy-mm-dd): ");
                    String endDate = sc.nextLine();

                    try {
                        boolean reserved = service.reserveAsset(resAssetId, resEmpId, resDate, startDate, endDate);
                        if (reserved) {
                            System.out.println("Asset reserved successfully.");
                        }
                    } catch (AssetNotFoundException | AssetNotMaintainException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 7:
                    System.out.println("Exiting... Thank you!");
                    break;

                default:
                    System.out.println("Invalid choice. Try again!");
            }

        } while (choice != 7);

        sc.close();
    }
}
