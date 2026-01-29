package interfaces;

public interface ICustomerActions {
    double checkBalance(String name);
    void deposit(String name, int amount);
    boolean withdraw(String name, int amount);
    void printReceipt();
}