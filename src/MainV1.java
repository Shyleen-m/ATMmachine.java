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
            if (sc.hasNextInt()) {
                choice = sc.nextInt();
            } else {
                System.out.println("Access denied.");
                sc.next(); // clear invalid input
                continue;  // go back to menu
            }

            sc.nextLine();

            if (choice == 1) {
                System.out.print("Name: "); String name = sc.nextLine();
                System.out.print("PIN: "); String pin = sc.nextLine();

                Account user = atm.authenticateUser(name, pin);
                if (user != null) {
                    userMenu(atm, sc, user);
                }
            }
            else if (choice == 2) {
                System.out.print("ID: "); String id = sc.nextLine();
                System.out.print("Pass: "); String pass = sc.nextLine();
                if (atm.authenticateTech(id, pass)) {
                    atm.viewATMStatus();
                } else System.out.println("Denied.");
            }
            else break;
        }
    }

    // New User Sub-Menu
    private static void userMenu(ATMMachine atm, Scanner sc, Account user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- USER MENU (" + user.getOwner() + ") ---");
            System.out.println("1. Check Balance\n2. Deposit\n3. Withdraw\n4. Logout");
            System.out.print("Action: ");
            int act = sc.nextInt();

            switch (act) {
                case 1 -> System.out.println("Balance: $" + atm.checkBalance(user.getOwner()));
                case 2 -> {
                    System.out.print("Deposit amount: ");
                    atm.deposit(user.getOwner(), sc.nextDouble());
                }
                case 3 -> {
                    System.out.print("Withdraw amount: ");
                    double amt = sc.nextDouble();
                    if (!atm.withdraw(user.getOwner(), amt)) System.out.println("Insufficient funds.");
                }
                case 4 -> {
                    atm.logout();
                    loggedIn = false;
                }
            }
        }
    }
}