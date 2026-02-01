package interfaces;

import model.Account;
import java.util.List;

public interface IATMStateService {
    void saveState(List<Account> accounts, double cash, int paper, int ink, String firmware);

    List<Account> loadAccounts();
    int loadPaperLevel();
    int loadInkLevel();
    double loadCashLevel();
    String loadFirmwareVersion();
}
