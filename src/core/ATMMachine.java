package core;

import interfaces.*;
import model.Account;
import services.*;
import java.util.List;
import java.util.Optional;

public class ATMMachine implements ICustomerActions, ITechActions {

    private List<Account> accounts;
    private double internalCash = 5000.0;
    private PrinterService printer;
    private PersistenceService persistence = new PersistenceService();
    private boolean isSessionActive = false;
    private final String firmwareVersion = "1.1.1"; // Default firmware

    public ATMMachine() {
        // Load accounts
        this.accounts = persistence.loadAccounts();

        // Load paper and ink from persistence (simplified defaults if not saved)
        int savedPaper = persistence.loadPaperLevel(); // implement in PersistenceService
        int savedInk = persistence.loadInkLevel();     // implement in PersistenceService

        if (savedPaper <= 0) savedPaper = 3;
        if (savedInk <= 0) savedInk = 3;

        this.printer = new PrinterService(savedPaper, savedInk);
    }

    // -------------------- CUSTOMER --------------------
    public Account authenticateUser(String name, String pin) {
        if (isOutOfService()) {
            System.out.println("[!] ATM out of service. Please try later.");
            return null;
        }

        if (pin == null || !pin.matches("\\d{4}")) {
            System.out.println("[!] PIN must be exactly 4 digits.");
            return null;
        }

        for (Account a : accounts) {
            if (a.getOwner().equalsIgnoreCase(name)) {
                if (a.getPin().equals(pin)) {
                    isSessionActive = true;
                    System.out.println("Welcome back, " + name + "!");
                    System.out.println("Current balance: €" + String.format("%.2f", a.getBalance()));
                    return a;
                } else {
                    System.out.println("[!] Incorrect PIN.");
                    return null;
                }
            }
        }

        // Auto-register new user
        Account newUser = new Account(name, pin, 0.0);
        accounts.add(newUser);
        persistence.saveState(accounts, internalCash, printer.getPaperLevel(), printer.getInkLevel());
        System.out.println("[+] Registered new account: " + name);
        isSessionActive = true;
        return newUser;
    }

    public boolean isOutOfService() {
        return printer.getPaperLevel() <= 0 || printer.getInkLevel() <= 0;
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
        Optional<Account> acc = accounts.stream().filter(a -> a.getOwner().equalsIgnoreCase(name)).findFirst();
        return acc.map(Account::getBalance).orElse(0.0);
    }

    public void deposit(String name, int amount) {
        Optional<Account> acc = accounts.stream().filter(a -> a.getOwner().equalsIgnoreCase(name)).findFirst();
        if (acc.isPresent()) {
            Account a = acc.get();
            a.setBalance(a.getBalance() + amount);
            internalCash += amount;
            persistence.saveState(accounts, internalCash, printer.getPaperLevel(), printer.getInkLevel());
            System.out.println("Successfully deposited €" + amount);
        }
    }

    public boolean withdraw(String name, int amount) {
        Optional<Account> acc = accounts.stream().filter(a -> a.getOwner().equalsIgnoreCase(name)).findFirst();
        if (acc.isPresent()) {
            Account a = acc.get();
            if (a.getBalance() >= amount && internalCash >= amount) {
                a.setBalance(a.getBalance() - amount);
                internalCash -= amount;
                persistence.saveState(accounts, internalCash, printer.getPaperLevel(), printer.getInkLevel());
                printReceipt();
                return true;
            } else {
                System.out.println("[!] Insufficient funds or ATM cash.");
            }
        }
        return false;
    }

    public void printReceipt() {
        if (!printer.hasPaper()) {
            System.out.println("[!] Printer empty. Cannot print receipt.");
            return;
        }
        if (!printer.hasInk()) {
            System.out.println("[!] Printer out of ink. Cannot print receipt.");
            return;
        }

        printer.usePaper();
        printer.useInk();
        System.out.println("[*] Receipt printed.");
        if (printer.getPaperLevel() <= 3) System.out.println("[!] Warning: Low paper.");
        if (printer.getInkLevel() <= 3) System.out.println("[!] Warning: Low ink.");

        persistence.saveState(accounts, internalCash, printer.getPaperLevel(), printer.getInkLevel());
    }

    public void logout() { isSessionActive = false; }

    // -------------------- TECHNICIAN --------------------
    public boolean authenticateTech(String id, String pass) {
        return id.equals("TECH1") && pass.equals("123");
    }

    public void viewATMStatus() {
        System.out.println("\n--- ATM SYSTEM STATUS ---");
        System.out.println("System Status: ONLINE");
        System.out.println("Usage Status:  " + (isSessionActive ? "IN USE" : "IDLE"));
        System.out.println("Cash Level:    €" + internalCash);
        System.out.println("Paper Level:   " + printer.getPaperLevel() + " sheets");
        System.out.println("Ink Level:     " + printer.getInkLevel() + " units");
        System.out.println("Firmware:      " + firmwareVersion);
    }
}
