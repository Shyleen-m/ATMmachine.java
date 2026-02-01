package services;

import interfaces.IATMStateService;
import model.Account;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileATMStateService implements IATMStateService {

    private final String PATH = "data/atm_state.json";

    @Override
    public void saveState(List<Account> accounts, double cash, int paper, int ink, String firmware) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"cash\": ").append(cash).append(",\n");
        sb.append("  \"paper\": ").append(paper).append(",\n");
        sb.append("  \"ink\": ").append(ink).append(",\n");
        sb.append("  \"firmware\":\"").append(firmware).append("\",\n");
        sb.append("  \"accounts\": [\n");

        for (int i = 0; i < accounts.size(); i++) {
            sb.append("    ").append(accounts.get(i).toJsonWithTransactions());
            if (i < accounts.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}");
        try {
            Files.createDirectories(Paths.get("data"));
            Files.write(Paths.get(PATH), sb.toString().getBytes());
        } catch (IOException e) {
            System.out.println("[!] Save Error: " + e.getMessage());
        }
    }

    @Override
    public List<Account> loadAccounts() {
        List<Account> list = new ArrayList<>();
        try {
            String data = Files.readString(Paths.get(PATH));
            int start = data.indexOf("[");
            int end = data.lastIndexOf("]");
            if (start >= 0 && end >= 0) {
                String accountsData = data.substring(start + 1, end).trim();
                if (!accountsData.isEmpty()) {
                    // Split safely by '},{' boundaries, restore braces when needed
                    String[] accs = accountsData.split("\\},\\{");
                    for (String accStr : accs) {
                        String a = accStr;
                        if (!a.startsWith("{")) a = "{" + a;
                        if (!a.endsWith("}")) a = a + "}";

                        // Extract owner
                        String owner = extractStringField(a, "owner");
                        String pin = extractStringField(a, "pin");
                        double balance = extractDoubleField(a, "balance");

                        Account account = new Account(owner, pin, balance);

                        // Extract transactions array if present
                        int tStart = a.indexOf("\"transactions\"");
                        if (tStart >= 0) {
                            int arrStart = a.indexOf("[", tStart);
                            int arrEnd = a.indexOf("]", arrStart);
                            if (arrStart >= 0 && arrEnd >= 0) {
                                String txBlock = a.substring(arrStart + 1, arrEnd).trim();
                                if (!txBlock.isEmpty()) {
                                    // Split on "," between entries while handling quoted strings
                                    // Transactions are stored as quoted strings; split by \",\" or by \",\n\" depending on formatting
                                    String[] txs = txBlock.split("\",\"");
                                    for (String tx : txs) {
                                        tx = tx.trim();
                                        tx = tx.replaceFirst("^\"", "").replaceFirst("\"$", "");
                                        if (!tx.isEmpty()) account.getTransactions().add(tx);
                                    }
                                }
                            }
                        }

                        list.add(account);
                    }
                }
            }
        } catch (Exception e) {
            // No state file or parse problem: create initial preset state
            list.add(new Account("ngaa", "2006", 100.0));
            // persist initial state (paper/ink defaults set elsewhere in constructor)
            saveState(list, loadCashLevel(), 4, 4, loadFirmwareVersion());
            return list;
        }

        // Ensure preset account exists
        boolean hasPreset = list.stream().anyMatch(a -> a.getOwner().equalsIgnoreCase("ngaa") && a.getPin().equals("2006"));
        if (!hasPreset) {
            list.add(new Account("ngaa", "2006", 100.0));
            saveState(list, loadCashLevel(), loadPaperLevel(), loadInkLevel(), loadFirmwareVersion());
        } else {
            // If preset exists but has balance less than 100, bring it up and persist
            for (Account a : list) {
                if (a.getOwner().equalsIgnoreCase("ngaa") && a.getPin().equals("2006")) {
                    if (a.getBalance() < 100.0) {
                        a.setBalance(100.0);
                        saveState(list, loadCashLevel(), loadPaperLevel(), loadInkLevel(), loadFirmwareVersion());
                    }
                }
            }
        }

        return list;
    }

    @Override
    public int loadPaperLevel() {
        try {
            String data = Files.readString(Paths.get(PATH));
            for (String line : data.split("\n")) {
                if (line.contains("\"paper\"")) return Integer.parseInt(line.split(":")[1].replace(",", "").trim());
            }
        } catch (Exception e) {}
        return 4;
    }

    @Override
    public int loadInkLevel() {
        try {
            String data = Files.readString(Paths.get(PATH));
            for (String line : data.split("\n")) {
                if (line.contains("\"ink\"")) return Integer.parseInt(line.split(":")[1].replace(",", "").trim());
            }
        } catch (Exception e) {}
        return 4;
    }

    // ---------------------- Helper parsers ----------------------
    private String extractStringField(String src, String field) {
        try {
            int idx = src.indexOf("\"" + field + "\"");
            if (idx < 0) return "";
            int colon = src.indexOf(":", idx);
            int firstQuote = src.indexOf('"', colon);
            if (firstQuote < 0) return "";
            int secondQuote = src.indexOf('"', firstQuote + 1);
            if (secondQuote < 0) return "";
            return src.substring(firstQuote + 1, secondQuote);
        } catch (Exception e) { return ""; }
    }

    private double extractDoubleField(String src, String field) {
        try {
            int idx = src.indexOf("\"" + field + "\"");
            if (idx < 0) return 0.0;
            int colon = src.indexOf(":", idx);
            if (colon < 0) return 0.0;
            int end = src.indexOf(",", colon);
            if (end < 0) end = src.indexOf("}", colon);
            String num = src.substring(colon + 1, end).trim();
            return Double.parseDouble(num);
        } catch (Exception e) { return 0.0; }
    }

    @Override
    public double loadCashLevel() {
        try {
            String data = Files.readString(Paths.get(PATH));
            for (String line : data.split("\n")) {
                if (line.contains("\"cash\"")) return Double.parseDouble(line.split(":")[1].replace(",", "").trim());
            }
        } catch (Exception e) {}
        return 5000.0;
    }

    @Override
    public String loadFirmwareVersion() {
        try {
            String data = Files.readString(Paths.get(PATH));
            for (String line : data.split("\n")) {
                if (line.contains("\"firmware\""))
                    return line.split(":")[1].replace("\"", "").replace(",", "").trim();
            }
        } catch (Exception e) {}
        return "1.0.0";
    }
}
