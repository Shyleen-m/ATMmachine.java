import core.ATMMachine;
import model.Account;
import java.util.Scanner;

public class MainV1 {

    public static void main(String[] args) {
        ATMMachine atm = new ATMMachine();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- ATM HOME SCREEN ---");
            System.out.println("1. Customer Login");
            System.out.println("2. Technician Login");
            System.out.println("3. Exit");
            System.out.print("Select: ");

            int choice;
            try { choice = Integer.parseInt(sc.nextLine()); }
            catch (Exception e) { System.out.println("Invalid input."); continue; }

            switch (choice) {
                case 1 -> {
                    if (atm.isOutOfService()) {
                        System.out.println("[!] ATM out of service.");
                        continue;
                    }
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("PIN: ");
                    String pin = sc.nextLine();
                    var user = atm.authenticateUser(name, pin);
                    if (user != null) userMenu(atm, sc, user);
                }
                case 2 -> {
                    System.out.print("ID: ");
                    String id = sc.nextLine();
                    System.out.print("Pass: ");
                    String pass = sc.nextLine();
                    if (atm.authenticateTech(id, pass)) atm.viewATMStatus();
                    else System.out.println("Denied.");
                }
                case 3 -> { System.out.println("Goodbye!"); sc.close(); return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void userMenu(ATMMachine atm, Scanner sc, Account user) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n--- USER MENU (" + user.getOwner() + ") ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Logout");
            System.out.println("5. Transaction History");
            System.out.print("Action: ");

            int act;
            try { act = Integer.parseInt(sc.nextLine()); }
            catch (Exception e) { System.out.println("Invalid input."); continue; }

            switch (act) {
                case 1 -> System.out.println("Balance: €" + String.format("%.2f", atm.checkBalance(user.getOwner())));
                case 2 -> depositMenu(atm, sc, user);
                case 3 -> withdrawMenu(atm, sc, user);
                case 4 -> { atm.logout(); loggedIn = false; }
                case 5 -> {
                    if (user.getTransactions().isEmpty()) System.out.println("No transactions yet.");
                    else user.getTransactions().forEach(System.out::println);
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ---------------- DEPOSIT ----------------
    private static void depositMenu(ATMMachine atm, Scanner sc, Account user) {
        if (!atm.checkPaperInkWarning(sc)) return;

        System.out.print("Enter total deposit (€): ");
        int amount;
        try {
            amount = Integer.parseInt(sc.nextLine());
            if (amount <= 0 || amount % 5 != 0) {
                System.out.println("Must be positive and multiple of €5."); return;
            }
        } catch (Exception e) { System.out.println("Invalid input."); return; }

        if (!selectNotesMenu(sc, amount)) return;

        atm.deposit(user.getOwner(), amount);
        user.addTransaction("Deposit", amount);
        atm.printReceipt();
    }

    // ---------------- WITHDRAW ----------------
    private static void withdrawMenu(ATMMachine atm, Scanner sc, Account user) {
        if (!atm.checkPaperInkWarning(sc)) return;

        System.out.print("Enter total withdrawal (€): ");
        int amount;
        try {
            amount = Integer.parseInt(sc.nextLine());
            if (amount <= 0 || amount % 5 != 0) {
                System.out.println("Must be positive and multiple of €5."); return;
            }
        } catch (Exception e) { System.out.println("Invalid input."); return; }

        if (!selectNotesMenu(sc, amount)) return;

        if (!atm.withdraw(user.getOwner(), amount)) return;
        user.addTransaction("Withdraw", amount);
        atm.printReceipt();
    }

    // ---------------- NOTE SELECTION ----------------
    private static boolean selectNotesMenu(Scanner sc, int amount) {
        int[] notes = {100, 50, 20, 10, 5};
        int total = 0;
        System.out.println("Processing €" + amount + "...");

        while (total < amount) {
            System.out.println("\nCurrent total: €" + total + " / €" + amount);
            System.out.println("Select note to add:");
            System.out.println("1. €100  2. €50  3. €20  4. €10  5. €5  0. Cancel");
            System.out.print("Choice: ");

            int choice;
            try { choice = Integer.parseInt(sc.nextLine()); } catch (Exception e) { System.out.println("Invalid."); continue; }
            if (choice == 0) { System.out.println("Transaction cancelled."); return false; }
            if (choice < 1 || choice > 5) { System.out.println("Invalid choice."); continue; }

            int noteValue = notes[choice - 1];
            int remaining = amount - total;
            int maxNotes = remaining / noteValue;
            if (maxNotes == 0) { System.out.println("Cannot use this note. Remaining €" + remaining); continue; }

            System.out.print("How many €" + noteValue + " notes? (Max " + maxNotes + "): ");
            int count;
            try { count = Integer.parseInt(sc.nextLine()); } catch (Exception e) { System.out.println("Invalid."); continue; }
            if (count <= 0 || count > maxNotes) { System.out.println("Invalid count."); continue; }

            total += count * noteValue;
            System.out.println("Added " + count + " x €" + noteValue + " notes.");
            if (total == amount) { System.out.println("Desired amount reached!"); break; }
        }
        return true;
    }
}
