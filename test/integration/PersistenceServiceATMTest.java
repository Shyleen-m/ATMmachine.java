package integration;

import core.ATMMachine;
import model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceServiceATMTest {

    private ATMMachine atm;

    @BeforeEach
    public void setup() {
        atm = new ATMMachine();
    }

    // ------------------ HELPER METHODS ------------------

    private void setInternalCash(double cash) throws Exception {
        Field cashField = ATMMachine.class.getDeclaredField("internalCash");
        cashField.setAccessible(true);
        cashField.setDouble(atm, cash);
    }

    private void setPrinterPaper(int paper) throws Exception {
        Field printerField = ATMMachine.class.getDeclaredField("printer");
        printerField.setAccessible(true);
        Object printer = printerField.get(atm);

        Field paperField = printer.getClass().getDeclaredField("paperLevel"); // ATM v1 field
        paperField.setAccessible(true);
        paperField.setInt(printer, paper);
    }

    private void setPrinterInk(int ink) throws Exception {
        Field printerField = ATMMachine.class.getDeclaredField("printer");
        printerField.setAccessible(true);
        Object printer = printerField.get(atm);

        Field inkField = printer.getClass().getDeclaredField("inkLevel"); // ATM v1 field
        inkField.setAccessible(true);
        inkField.setInt(printer, ink);
    }

    // ------------------ TESTS ------------------

    @Test
    public void testOutOfService_NoCash() throws Exception {
        setInternalCash(0);
        Account user = atm.authenticateUser("ngaa", "2006");
        assertNotNull(user, "Login should work with no cash (we only block withdrawals)");

        boolean withdrawResult = atm.withdraw("ngaa", 50);
        assertFalse(withdrawResult, "Withdrawal should be blocked when ATM cash is 0");
    }

    @Test
    public void testOutOfService_NoPaper() throws Exception {
        setPrinterPaper(0);
        Account user = atm.authenticateUser("ngaa", "2006");
        assertNull(user, "ATM should block login when paper is 0");
    }

    @Test
    public void testOutOfService_NoInk() throws Exception {
        setPrinterInk(0);
        Account user = atm.authenticateUser("ngaa", "2006");
        assertNull(user, "ATM should block login when ink is 0");
    }
}
