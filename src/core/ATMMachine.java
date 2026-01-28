package core;

import interfaces.*;
import model.Account;
import services.*;
import java.util.*;

public class ATMMachine implements ICustomerActions, ITechActions {
    private List<Account> accounts;
    private double internalCash = 5000.0;
    private PrinterService printer = new PrinterService(10);
    private PersistenceService persistence = new PersistenceService();
    private boolean isSessionActive = false; // Tracks if a user is currently logged in

    public ATMMachine() {
        this.accounts = persistence.loadAccounts();
    }

    // Logic: If user exists, check PIN. If user is new, register them!
    public Account authenticateUser(String name, String pin) {
        // Validation: Ensure PIN is 4 digits
        if (pin == null || !pin.matches("\\d{4}")) {
            System.out.println("[!] Error: PIN must be exactly 4 digits.");
            return null;
        }

        for (Account a : accounts) {
            if (a.getOwner().equalsIgnoreCase(name)) {
                if (a.getPin().equals(pin)) {
                    isSessionActive = true;
                    return a;
                } else {
                    System.out.println("[!] Error: Incorrect PIN for this account.");
                    return null;
                }
            }
        }

        // Auto-Register new user
        Account newUser = new Account(name, pin, 0.0);
        accounts.add(newUser);
        persistence.saveState(accounts, internalCash, printer.getPaperLevel());
        System.out.println("[+] New account registered for: " + name);
        isSessionActive = true;
        return newUser;
    }

    public void logout() {
        isSessionActive = false;
    }

    public boolean authenticateTech(String id, String pass) {
        return id.equals("TECH1") && pass.equals("123");
    }

    @Override public double checkBalance(String name) {
        return accounts.stream().filter(a -> a.getOwner().equalsIgnoreCase(name)).findFirst().get().getBalance();
    }

    @Override public void deposit(String name, double amount) {
        Account a = accounts.stream().filter(acc -> acc.getOwner().equalsIgnoreCase(name)).findFirst().get();
        a.setBalance(a.getBalance() + amount);
        internalCash += amount;
        persistence.saveState(accounts, internalCash, printer.getPaperLevel());
        System.out.println("Successfully deposited $" + amount);
    }

    @Override public boolean withdraw(String name, double amount) {
        Account a = accounts.stream().filter(acc -> acc.getOwner().equalsIgnoreCase(name)).findFirst().get();
        if (a.getBalance() >= amount && internalCash >= amount) {
            a.setBalance(a.getBalance() - amount);
            internalCash -= amount;
            persistence.saveState(accounts, internalCash, printer.getPaperLevel());
            printReceipt();
            return true;
        }
        return false;
    }

    @Override public void printReceipt() {
        if (!printer.hasPaper()) {
            System.out.println("[!] Notification: Printer empty.");
        } else {
            printer.usePaper();
            System.out.println("[*] Receipt printed.");
        }
    }

    @Override public void viewATMStatus() {
        System.out.println("\n--- ATM SYSTEM STATUS ---");
        System.out.println("System Status: ONLINE");
        System.out.println("Usage Status:  " + (isSessionActive ? "IN USE" : "IDLE"));
        System.out.println("Cash Level:    $" + internalCash);
        System.out.println("Paper Level:   " + printer.getPaperLevel() + " sheets");
    }
}