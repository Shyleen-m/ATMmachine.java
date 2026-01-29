package core;

import interfaces.IATMStateService;
import services.PrinterService;

public class ATMMachineV2 {

    private int cashAvailable;
    private int paperAvailable;
    private String firmwareVersion;

    private IATMStateService stateService;
    private PrinterService printerService;

    public ATMMachineV2(IATMStateService stateService, PrinterService printerService) {
        this.stateService = stateService;
        this.printerService = printerService;

        this.cashAvailable = stateService.loadCash();
        this.paperAvailable = stateService.loadPaper();
        this.firmwareVersion = stateService.loadFirmware();
    }

    // -------------------- TECHNICIAN HELPERS --------------------
    public int getCashAvailable() {
        return cashAvailable;
    }

    public int getPaperAvailable() {
        return paperAvailable;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    // Partial cash collection
    public void collectCash(int amount) {
        if (amount <= cashAvailable && amount > 0) {
            cashAvailable -= amount;
            System.out.println("Collected $" + amount + ". Remaining cash: $" + cashAvailable);
            save();
        } else {
            System.out.println("Invalid collection amount.");
        }
    }

    // Full cash collection (collect all)
    public void collectCash() {
        System.out.println("Collected all: $" + cashAvailable);
        cashAvailable = 0;
        save();
    }

    public void refillCash(int amount) {
        if (amount > 0) {
            cashAvailable += amount;
            System.out.println("Refilled cash. New cash available: $" + cashAvailable);
            save();
        } else {
            System.out.println("Invalid amount.");
        }
    }

    public void refillPaper(int amount) {
        if (amount > 0) {
            paperAvailable += amount;
            System.out.println("Refilled paper. New paper available: " + paperAvailable);
            save();
        } else {
            System.out.println("Invalid amount.");
        }
    }

    public void updateFirmware(String newVersion) {
        if (newVersion != null && !newVersion.isEmpty()) {
            System.out.println("Current firmware: " + firmwareVersion);
            firmwareVersion = newVersion;
            System.out.println("Firmware updated to: " + firmwareVersion);
            save();
        } else {
            System.out.println("Invalid firmware version.");
        }
    }

    public void viewStatus() {
        System.out.println("\n--- ATM STATUS ---");
        System.out.println("Cash Available: $" + cashAvailable);
        System.out.println("Paper Available: " + paperAvailable);
        System.out.println("Firmware Version: " + firmwareVersion);
    }

    // -------------------- CUSTOMER FUNCTIONS --------------------
    public boolean withdraw(String owner, int amount) {
        if (amount <= cashAvailable && amount > 0) {
            cashAvailable -= amount;
            save();
            return true;
        }
        return false;
    }

    public void deposit(String owner, int amount) {
        if (amount > 0) {
            cashAvailable += amount;
            save();
        }
    }

    public int checkBalance(String owner) {
        return cashAvailable; // placeholder for compatibility with V1 menu
    }

    public void logout() {
        System.out.println("User logged out.");
    }

    // -------------------- STATE PERSISTENCE --------------------
    private void save() {
        stateService.saveState(cashAvailable, paperAvailable, firmwareVersion);
    }
}
