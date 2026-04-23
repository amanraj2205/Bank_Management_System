package BankAccountManagement;

class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(String accountNumber, String accountHolder, String password, double balance, double interestRate) {
        super(accountNumber, accountHolder, password, balance);
        this.interestRate = interestRate;
    }

    public void addInterest() {
        double interest = balance * (interestRate / 100);
        deposit(interest); 
        System.out.println("Interest added: " + interest);  
    }
}
