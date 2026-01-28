package services;

import model.Account;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PersistenceService {
    private final String PATH = "data/atm_state.json";

    public void saveState(List<Account> accounts, double cash, int paper) {
        StringBuilder sb = new StringBuilder("{\n  \"cash\": " + cash + ",\n  \"paper\": " + paper + ",\n  \"accounts\": [\n");
        for (int i = 0; i < accounts.size(); i++) {
            sb.append("    ").append(accounts.get(i).toJson());
            if (i < accounts.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}");
        try {
            Files.createDirectories(Paths.get("data"));
            Files.write(Paths.get(PATH), sb.toString().getBytes());
        } catch (IOException e) { System.out.println("Save Error: " + e.getMessage()); }
    }

    public List<Account> loadAccounts() {
        return new ArrayList<>(Arrays.asList(
                new Account("Alice", "1234", 1000.0),
                new Account("Bob", "5555", 500.0)
        ));
    }
}