package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private String owner;
    private String pin;
    private double balance;
    private List<String> transactions;

    public Account(String owner, String pin, double balance) {
        this.owner = owner;
        this.pin = pin;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public String getOwner() { return owner; }
    public String getPin() { return pin; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public List<String> getTransactions() { return transactions; }

    // Add a transaction with timestamp + current balance
    public void addTransaction(String type, double amount) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String entry = timestamp + " | " + type + ": €" + String.format("%.2f", amount)
                + " | Balance: €" + String.format("%.2f", this.balance);
        transactions.add(entry);
    }

    // Serialize account with transactions for saving
    public String toJsonWithTransactions() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"owner\":\"").append(owner).append("\", ");
        sb.append("\"pin\":\"").append(pin).append("\", ");
        sb.append("\"balance\":").append(String.format("%.2f", balance)).append(", ");
        sb.append("\"transactions\":[");
        for (int i = 0; i < transactions.size(); i++) {
            sb.append("\"").append(transactions.get(i).replace("\"", "\\\"")).append("\"");
            if (i < transactions.size() - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }
}
