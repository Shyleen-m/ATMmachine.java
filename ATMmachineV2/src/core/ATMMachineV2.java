package core;

import interfaces.ICustomerActions;
import interfaces.ITechActions;
import interfaces.IATMStateService;
import model.Account;
import services.PrinterService;

import java.util.List;
import java.util.Optional;

public class ATMMachineV2 implements ICustomerActions, ITechActions {

    private List<Account> accounts;
    private double internalCash;
    private PrinterService printer;
    private IATMStateService persistence;
    private boolean isSessionActive;
    private String firmwareVersion;

    public ATMMachineV2(IATMStateService persistence, PrinterService printer) {
        this.persistence = persistence;
        this.printer = printer;

        this.accounts = persistence.loadAccounts();
        this.internalCash = persistence.loadCashLevel();
        this.printer.setPaperLevel(persistence.loadPaperLevel());
        this.printer.setInkLevel(persistence.loadInkLevel());
        this.firmwareVersion = persistence.loadFirmwareVersion();

        this.isSessionActive = false;
    }

    // ------------------- CUSTOMER ACTIONS -------------------
    public Account authenticateUser(String name, String pin) {
        if (isOutOfService()) {
            System.out.println("[!] ATM out of service. Please try later.");
            return null;
        }
        if (pin == null || !pin.matches("\\d{4}")) {
            System.out.println("[!] PIN must be 4 digits.");
            return null;
        }

        for (Account a : accounts) {
            if (a.getOwner().equalsIgnoreCase(name)) {
                if (a.getPin().equals(pin)) {
                    isSessionActive = true;
                    System.out.println("Welcome, " + name + "!");
                    System.out.println("Current balance: €" + String.format("%.2f", a.getBalance()));
                    return a;
                } else {
                    System.out.println("[!] Incorrect PIN.");
                    return null;
                }
            }
        }

        Account newUser = new Account(name, pin, 0.0);
        accounts.add(newUser);
        saveState();
        System.out.println("[+] Registered new account: " + name);
        isSessionActive = true;
        return newUser;
    }

    public boolean isOutOfService() {
        return internalCash <= 0 || printer.getPaperLevel() <= 0 || printer.getInkLevel() <= 0;
    }

    public boolean checkPaperInkWarning(java.util.Scanner sc) {
        if (printer.getPaperLevel() <= 3 || printer.getInkLevel() <= 3) {
            System.out.println("[!] Low paper/ink. Continue transaction? (y/n)");
            String resp = sc.nextLine();
            return resp.equalsIgnoreCase("y");
        }
        return true;
    }

    public double checkBalance(String name) {
        Optional<Account> acc = accounts.stream()
                .filter(a -> a.getOwner().equalsIgnoreCase(name))
                .findFirst();
        return acc.map(Account::getBalance).orElse(0.0);
    }

    public void deposit(String name, int amount) {
        Optional<Account> acc = accounts.stream()
                .filter(a -> a.getOwner().equalsIgnoreCase(name))
                .findFirst();
        acc.ifPresent(a -> {
            a.setBalance(a.getBalance() + amount);
            internalCash += amount;
            saveState();
            System.out.println("Successfully deposited €" + amount);
        });
    }

    public boolean withdraw(String name, int amount) {
        Optional<Account> acc = accounts.stream()
                .filter(a -> a.getOwner().equalsIgnoreCase(name))
                .findFirst();
        if (acc.isPresent()) {
            Account a = acc.get();
            if (a.getBalance() >= amount && internalCash >= amount) {
                a.setBalance(a.getBalance() - amount);
                internalCash -= amount;
                System.out.println("Desired amount reached. Please collect your cash: €" + amount);
                printReceipt();
                saveState();
                return true;
            } else {
                System.out.println("[!] Insufficient funds or ATM cash.");
            }
        }
        return false;
    }

    public void printReceipt() {
        if (!printer.hasPaper()) {
            System.out.println("[!] Printer empty. ATM out of service. Logging out user...");
            logout();
            saveState();
            return;
        }
        if (!printer.hasInk()) {
            System.out.println("[!] Printer out of ink. ATM out of service. Logging out user...");
            logout();
            saveState();
            return;
        }

        printer.usePaper();
        printer.useInk();
        System.out.println("[*] Receipt printed.");
        if (printer.getPaperLevel() <= 3) System.out.println("[!] Warning: Low paper.");
        if (printer.getInkLevel() <= 3) System.out.println("[!] Warning: Low ink.");

        // If printing consumed the last paper or ink, transition to out-of-service
        if (printer.getPaperLevel() <= 0 || printer.getInkLevel() <= 0) {
            System.out.println("[!] Printer depleted after printing. ATM out of service. Logging out user...");
            logout();
        }
        saveState();
    }

    public void logout() {
        isSessionActive = false;
    }

    // ------------------- TECHNICIAN ACTIONS -------------------
    public boolean authenticateTech(String id, String pass) {
        return id.equals("TECH1") && pass.equals("123");
    }

    public void viewATMStatus() {
        System.out.println("\n--- ATM STATUS ---");
        System.out.println("System: ONLINE");
        System.out.println("Usage: " + (isSessionActive ? "IN USE" : "IDLE"));
        System.out.println("Cash: €" + internalCash);
        System.out.println("Paper: " + printer.getPaperLevel() + " sheets");
        System.out.println("Ink: " + printer.getInkLevel() + " units");
        System.out.println("Firmware: " + firmwareVersion);
    }

    // ------------------- TECHNICIAN METHODS -------------------
    public void refillCash(int amount) {
        internalCash += amount;
        saveState();
        System.out.println("[+] Cash refilled: €" + amount);
    }

    public void collectCash(int amount) {
        if (amount > internalCash) amount = (int) internalCash;
        internalCash -= amount;
        saveState();
        System.out.println("[+] Cash collected: €" + amount);
    }

    public void refillPaper(int sheets) {
        printer.setPaperLevel(printer.getPaperLevel() + sheets);
        saveState();
        System.out.println("[+] Paper refilled: " + sheets + " sheets");
    }

    public void refillInk(int units) {
        printer.setInkLevel(printer.getInkLevel() + units);
        saveState();
        System.out.println("[+] Ink refilled: " + units + " units");
    }

    public void updateFirmware(String version) {
        if (version == null || !version.matches("\\d+\\.\\d+\\.\\d+")) {
            System.out.println("[!] Invalid firmware format. Use X.Y.Z (e.g., 1.1.1)");
            return;
        }
        this.firmwareVersion = version;
        saveState();
        System.out.println("[+] Firmware updated to " + version);
    }

    // ------------------- SAVE STATE -------------------
    private void saveState() {
        persistence.saveState(accounts, internalCash, printer.getPaperLevel(), printer.getInkLevel(), firmwareVersion);
    }

    // ------------------- GETTERS -------------------
    public double getCashAvailable() { return internalCash; }
    public int getPaperAvailable() { return printer.getPaperLevel(); }
    public int getInkAvailable() { return printer.getInkLevel(); }
    public String getFirmwareVersion() { return firmwareVersion; }
}
