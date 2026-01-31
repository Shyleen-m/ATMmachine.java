package model;

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
    public void addTransaction(String type, double amount) {
        String entry = type + ": €" + String.format("%.2f", amount);
        transactions.add(entry);
    }

    // ------------------- SERIALIZE -------------------
    // Basic JSON for saving accounts + transactions
    public String toJsonWithTransactions() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"owner\":\"").append(owner).append("\", ");
        sb.append("\"pin\":\"").append(pin).append("\", ");
        sb.append("\"balance\":").append(String.format("%.2f", balance)).append(", ");
        sb.append("\"transactions\":[");
        for (int i = 0; i < transactions.size(); i++) {
            sb.append("\"").append(transactions.get(i)).append("\"");
            if (i < transactions.size() - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }
}
