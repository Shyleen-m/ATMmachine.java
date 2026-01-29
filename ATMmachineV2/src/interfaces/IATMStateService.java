package interfaces;  // must match folder

public interface IATMStateService {
    int loadCash();
    int loadPaper();
    String loadFirmware();
    void saveState(int cash, int paper, String firmware);
}
