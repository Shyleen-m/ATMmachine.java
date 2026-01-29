package model;

public class Account {
    private String owner;
    private String pin;
    private double balance;

    public Account(String owner, String pin, double balance) {
        this.owner = owner;
        this.pin = pin;
        this.balance = balance;
    }

    public String getOwner() { return owner; }
    public String getPin() { return pin; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String toJson() {
        return String.format("{\"owner\":\"%s\", \"pin\":\"%s\", \"balance\":%.2f}", owner, pin, balance);
    }
}