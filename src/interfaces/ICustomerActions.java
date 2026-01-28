package interfaces;

public interface ICustomerActions {
    double checkBalance(String name);
    void deposit(String name, double amount);
    boolean withdraw(String name, double amount);
    void printReceipt();
}