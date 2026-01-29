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

            String choiceInput = sc.nextLine(); // read entire line
            int choice;

            try {
                choice = Integer.parseInt(choiceInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Only integers are allowed.");
                continue; // go back to menu
            }

            if (choice == 1) {
                System.out.print("Name: ");
                String name = sc.nextLine();

                System.out.print("PIN: ");
                String pin = sc.nextLine();

                Account user = atm.authenticateUser(name, pin);
                if (user != null) {
                    userMenu(atm, sc, user); // call the method outside main
                }

            } else if (choice == 2) {
                System.out.print("ID: ");
                String id = sc.nextLine();

                System.out.print("Pass: ");
                String pass = sc.nextLine();

                if (atm.authenticateTech(id, pass)) {
                    atm.viewATMStatus();
                } else {
                    System.out.println("Denied.");
                }

            } else if (choice == 3) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }

        sc.close();
    }

    // ---------------- USER MENU ----------------
    private static void userMenu(ATMMachine atm, Scanner sc, Account user) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n--- USER MENU (" + user.getOwner() + ") ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Logout");
            System.out.print("Action: ");

            String actionInput = sc.nextLine();
            int act;

            try {
                act = Integer.parseInt(actionInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Only integers are allowed.");
                continue;
            }

            switch (act) {
                case 1 -> System.out.println("Balance: $" + atm.checkBalance(user.getOwner()));

                case 2 -> {
                    System.out.print("Deposit amount: ");
                    String depInput = sc.nextLine();
                    int amount;

                    try {
                        amount = Integer.parseInt(depInput);
                        if (amount <= 0) {
                            System.out.println("Deposit must be a positive integer.");
                            break;
                        }
                        atm.deposit(user.getOwner(), amount);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Only integers are allowed.");
                    }
                }

                case 3 -> {
                    System.out.print("Withdraw amount: ");
                    String withInput = sc.nextLine();
                    int amt;

                    try {
                        amt = Integer.parseInt(withInput);
                        if (amt <= 0) {
                            System.out.println("Withdrawal must be a positive integer.");
                            break;
                        }

                        if (!atm.withdraw(user.getOwner(), amt)) {
                            System.out.println("Insufficient funds.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Only integers are allowed.");
                    }
                }

                case 4 -> {
                    atm.logout();
                    loggedIn = false;
                }

                default -> System.out.println("Invalid option.");
            }
        }
    }
}