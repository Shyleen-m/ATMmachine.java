package integration;

import core.ATMMachine;
import model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.PrinterService;

import static org.junit.jupiter.api.Assertions.*;

public class ATMIntegrationTest {

    private ATMMachine atm;

    @BeforeEach
    public void setup() {
        atm = new ATMMachine(); // uses internal PersistenceService
        System.out.println("\n--- ATM INITIALIZED ---");
        System.out.println("Printer paper: " + atm.checkBalance("Alice")); // just placeholder to trigger console
    }

    @Test
    public void testDepositFlow() {
        System.out.println("\n--- TEST DEPOSIT FLOW ---");
        Account user = atm.authenticateUser("Bob", "9999");
        assertNotNull(user);

        atm.deposit(user.getOwner(), 50);
        user.addTransaction("Deposit", 50);
        atm.printReceipt();

        System.out.println("User balance after deposit: €" + user.getBalance());
        assertEquals(50, user.getBalance(), 0.01);
    }

    @Test
    public void testWithdrawFlow_OutOfService() {
        System.out.println("\n--- TEST WITHDRAW FLOW / OUT OF SERVICE ---");

        Account user = atm.authenticateUser("Charlie", "1111");
        assertNotNull(user);

        // Deplete printer
        atm.withdraw(user.getOwner(), 10);
        user.addTransaction("Withdraw", 10);
        atm.printReceipt();

        atm.withdraw(user.getOwner(), 5);
        user.addTransaction("Withdraw", 5);
        atm.printReceipt();

        System.out.println("ATM out of service: " + atm.isOutOfService());
        assertTrue(atm.isOutOfService());
    }
}
