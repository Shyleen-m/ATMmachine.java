package integration;

import core.ATMMachine;
import model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ATMIntegrationTest {

    private ATMMachine atm;

    @BeforeEach
    public void setup() {
        atm = new ATMMachine(); // V1 no-arg constructor
        System.out.println("\n--- ATM INITIALIZED ---");
    }

    @Test
    public void testDepositFlow() {
        System.out.println("\n--- TEST DEPOSIT FLOW ---");

        Account user = atm.authenticateUser("Bob", "5555");
        assertNotNull(user, "User should exist");

        double beforeBalance = user.getBalance();
        int depositAmount = 50;

        atm.deposit(user.getOwner(), depositAmount); // void
        user.addTransaction("Deposit", depositAmount);
        atm.printReceipt();

        System.out.println("User balance after deposit: €" + user.getBalance());
        assertEquals(beforeBalance + depositAmount, user.getBalance(), 0.01);
    }

    @Test
    public void testWithdrawFlow_OutOfService() {
        System.out.println("\n--- TEST WITHDRAW FLOW / OUT OF SERVICE ---");

        Account user = atm.authenticateUser("Charlie", "1111");
        assertNotNull(user, "User should exist");

        int withdrawAmount = 10;

        // Keep withdrawing until printer is depleted
        while (!atm.isOutOfService()) {
            atm.withdraw(user.getOwner(), withdrawAmount);
            user.addTransaction("Withdraw", withdrawAmount);
            atm.printReceipt();
        }

        System.out.println("ATM out of service: " + atm.isOutOfService());
        assertTrue(atm.isOutOfService(), "ATM should be out of service after printer depleted");
    }
}
