package unit;

import model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccountUnitTest {

    private Account account;

    @BeforeEach
    void setup() {
        account = new Account("Alice", "1234", 50.0);
        System.out.println("\n[Setup] New account created: Alice, balance €50");
    }

    @Test
    void testDepositTransaction() {
        System.out.println("\n[Test] Deposit Transaction");

        account.setBalance(account.getBalance() + 30);
        account.addTransaction("Deposit", 30);

        List<String> txns = account.getTransactions();
        System.out.println("Transaction log: " + txns.get(0));
        System.out.println("Updated balance: €" + account.getBalance());

        assertEquals(1, txns.size());
        assertTrue(txns.get(0).contains("Deposit: €30.00"));
        assertEquals(80.0, account.getBalance(), 0.01);
    }

    @Test
    void testWithdrawTransaction() {
        System.out.println("\n[Test] Withdraw Transaction");

        account.setBalance(account.getBalance() - 20);
        account.addTransaction("Withdraw", 20);

        List<String> txns = account.getTransactions();
        System.out.println("Transaction log: " + txns.get(0));
        System.out.println("Updated balance: €" + account.getBalance());

        assertEquals(1, txns.size());
        assertTrue(txns.get(0).contains("Withdraw: €20.00"));
        assertEquals(30.0, account.getBalance(), 0.01);
    }
}
