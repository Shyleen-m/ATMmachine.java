package users;

import core.ATMMachineV2;
import java.util.Scanner;

public class TechnicianV2Panel {

    private ATMMachineV2 atm;
    private Scanner sc;

    public TechnicianV2Panel(ATMMachineV2 atm, Scanner sc) {
        this.atm = atm;
        this.sc = sc;
    }

    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== TECHNICIAN PANEL ===");
            System.out.println("1. View ATM Status");
            System.out.println("2. Collect Cash");
            System.out.println("3. Refill Cash");
            System.out.println("4. Refill Paper");
            System.out.println("5. Update Firmware");
            System.out.println("6. Exit");
            System.out.print("Select: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input.");
                sc.next();
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine(); // consume leftover newline

            switch (choice) {
                case 1 -> viewStatus();
                case 2 -> collectCash();
                case 3 -> refillCash();
                case 4 -> refillPaper();
                case 5 -> updateFirmware();
                case 6 -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewStatus() {
        System.out.println("\n--- ATM STATUS ---");
        System.out.println("Cash Available: $" + atm.getCashAvailable());
        System.out.println("Paper Available: " + atm.getPaperAvailable());
        System.out.println("Firmware Version: " + atm.getFirmwareVersion());
    }

    private void collectCash() {
        System.out.println("\n--- COLLECT CASH ---");
        System.out.println("ATM Cash Available: $" + atm.getCashAvailable());
        System.out.print("Enter amount to collect: ");

        if (sc.hasNextInt()) {
            int amount = sc.nextInt();
            sc.nextLine();
            if (amount <= 0) {
                System.out.println("Amount must be positive.");
            } else if (amount > atm.getCashAvailable()) {
                System.out.println("Cannot collect more than available.");
            } else {
                atm.collectCash(amount);
                System.out.println("Collected $" + amount);
                System.out.println("ATM Cash Remaining: $" + atm.getCashAvailable());
            }
        } else {
            System.out.println("Invalid input.");
            sc.nextLine();
        }
    }

    private void refillCash() {
        System.out.println("\n--- REFILL CASH ---");
        System.out.println("ATM Cash Before Refill: $" + atm.getCashAvailable());
        System.out.print("Enter amount to add: ");

        if (sc.hasNextInt()) {
            int amount = sc.nextInt();
            sc.nextLine();
            if (amount <= 0) {
                System.out.println("Amount must be positive.");
            } else {
                atm.refillCash(amount);
                System.out.println("ATM Cash After Refill: $" + atm.getCashAvailable());
            }
        } else {
            System.out.println("Invalid input.");
            sc.nextLine();
        }
    }

    private void refillPaper() {
        System.out.println("\n--- REFILL PAPER ---");
        System.out.println("Paper Before Refill: " + atm.getPaperAvailable());
        System.out.print("Enter amount to add: ");

        if (sc.hasNextInt()) {
            int amount = sc.nextInt();
            sc.nextLine();
            if (amount <= 0) {
                System.out.println("Amount must be positive.");
            } else {
                atm.refillPaper(amount);
                System.out.println("Paper After Refill: " + atm.getPaperAvailable());
            }
        } else {
            System.out.println("Invalid input.");
            sc.nextLine();
        }
    }

    private void updateFirmware() {
        System.out.println("\n--- UPDATE FIRMWARE ---");
        System.out.println("Current Firmware Version: " + atm.getFirmwareVersion());
        System.out.print("Enter new firmware version: ");

        String newVersion = sc.nextLine().trim();
        if (newVersion.isEmpty()) {
            System.out.println("Invalid version.");
            return;
        }

        atm.updateFirmware(newVersion);
        System.out.println("Firmware updated successfully to version " + atm.getFirmwareVersion());
    }
}
