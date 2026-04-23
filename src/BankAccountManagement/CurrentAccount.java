package BankAccountManagement;

class CurrentAccount extends Account {
    private double overdraftLimit;

    public CurrentAccount(String accountNumber, String accountHolder, String password, double balance, double overdraftLimit) {
        super(accountNumber, accountHolder, password, balance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(double amount) {  
        if (amount <= balance + overdraftLimit) {  
            balance -= amount;
            System.out.println(amount + " withdrawn. New balance: " + balance);
        } else {
            System.out.println("Withdrawal exceeds overdraft limit.");
        } 
    }   
}