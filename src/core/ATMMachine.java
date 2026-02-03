package core;

import interfaces.*;
import model.Account;
import services.*;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ATMMachine implements ICustomerActions, ITechActions {

    private List<Account> accounts;
    private double internalCash = 5000.0;
    private PrinterService printer;
    private PersistenceService persistence = new PersistenceService();
    private boolean isSessionActive = false;
    private final String firmwareVersion = "1.1.1";

    public ATMMachine() {
        this.accounts = persistence.loadAccounts();
        int savedPaper = persistence.loadPaperLevel();
        int savedInk = persistence.loadInkLevel();

        if (savedPaper <= 0) savedPaper = 3;
        if (savedInk <= 0) savedInk = 3;

        this.printer = new PrinterService(savedPaper, savedInk);
    }

    // ------------------ PUBLIC METHODS ------------------

    // Login blocked if paper or ink = 0
    public boolean isOutOfService() {
        return printer.getPaperLevel() <= 0 || printer.getInkLevel() <= 0;
    }

    // Warn user if low paper/ink and ask if they want to continue
    public boolean checkPaperInkWarning(Scanner sc) {
        if ((printer.getPaperLevel() <= 3 && printer.getPaperLevel() > 0) ||
                (printer.getInkLevel() <= 3 && printer.getInkLevel() > 0)) {

            while (true) {
                System.out.println("[!] Low paper/ink. Continue transaction? (y/n)");
                String resp = sc.nextLine().trim().toLowerCase();
                if (resp.equals("y")) return true;
                if (resp.equals("n")) return false;
                System.out.println("[!] Invalid input. Please enter 'y' or 'n'.");
            }
        }
        return true;
    }

    public Account authenticateUser(String name, String pin) {
        // Login blocked if ATM out of service
        if (isOutOfService()) {
            System.out.println("[!] ATM out of service. Please try later.");
            return null;
        }

        // Validate PIN
        if (pin == null || !pin.matches("\\d{4}")) {
            System.out.println("[!] PIN must be exactly 4 digits.");
            return null;
        }

        // Search for existing account
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
        if (acc.isPresent()) {
            Account a = acc.get();
            a.setBalance(a.getBalance() + amount);
            internalCash += amount;
            persistence.saveState(accounts, internalCash, printer.getPaperLevel(), printer.getInkLevel());
            System.out.println("Successfully deposited €" + amount);
        }
    }

    public boolean withdraw(String name, int amount) {
        Optional<Account> acc = accounts.stream()
                .filter(a -> a.getOwner().equalsIgnoreCase(name))
                .findFirst();
        if (acc.isPresent()) {
            Account a = acc.get();
            if (a.getBalance() < amount) {
                System.out.println("[!] Insufficient funds in account.");
                return false;
            }

            if (internalCash < amount) {
                System.out.println("[!] ATM does not have enough cash to dispense.");
                return false;
            }

            a.setBalance(a.getBalance() - amount);
            internalCash -= amount;
            persistence.saveState(accounts, internalCash, printer.getPaperLevel(), printer.getInkLevel());

            if (printer.getPaperLevel() <= 0 || printer.getInkLevel() <= 0) {
                System.out.println("[!] ATM out of service due to paper/ink.");
            }
            return true;
        }
        return false;
    }

    // ------------------ RECEIPT ------------------
    private void printReceiptOnce() {
        if (printer.hasPaper() && printer.hasInk()) {
            printer.usePaper();
            printer.useInk();
            System.out.println("[*] Receipt printed.");
        } else {
            if (!printer.hasPaper()) System.out.println("[!] Printer empty. Cannot print receipt.");
            if (!printer.hasInk()) System.out.println("[!] Printer out of ink. Cannot print receipt.");
        }
    }

    @Override
    public void printReceipt() {
        printReceiptOnce();
    }

    // ------------------ SESSION ------------------
    public void logout() { isSessionActive = false; }

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
