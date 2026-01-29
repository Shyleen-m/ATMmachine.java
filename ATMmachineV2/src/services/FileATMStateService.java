package services;

import interfaces.IATMStateService;
import java.io.*;

public class FileATMStateService implements IATMStateService {

    private static final String FILE = "atm_state.json";

    @Override
    public int loadCash() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            return Integer.parseInt(br.readLine());
        } catch (Exception e) {
            return 5000;
        }
    }

    @Override
    public int loadPaper() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            br.readLine();
            return Integer.parseInt(br.readLine());
        } catch (Exception e) {
            return 100;
        }
    }

    @Override
    public String loadFirmware() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            br.readLine();
            br.readLine();
            return br.readLine();
        } catch (Exception e) {
            return "1.0.0";
        }
    }

    @Override
    public void saveState(int cash, int paper, String firmware) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            pw.println(cash);
            pw.println(paper);
            pw.println(firmware);
        } catch (IOException e) {
            System.out.println("Error saving ATM state.");
        }
    }
}


