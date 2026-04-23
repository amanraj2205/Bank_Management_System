package BankAccountManagement;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Bank bank = new Bank();
        int choice;

        System.out.println("==========================================");
        System.out.println("   Welcome to Bank Management System");
        System.out.println("==========================================");

        do {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Login as User");
            System.out.println("2. Login as Admin");
            System.out.println("3. New User Create Account");
            System.out.println("4. New Admin Create Account");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    userLogin(bank, sc);
                    break;
                case 2:
                    adminLogin(bank, sc);
                    break;
                case 3:
                    newUserCreateAccount(bank, sc);
                    break;
                case 4:
                    newAdminCreateAccount(bank, sc);
                    break;
                case 5:
                    System.out.println("Thank you for using the system!");
                    break;
                default:
                    System.out.println("Invalid Option!");
            }
        } while (choice != 5);

        sc.close();
    }

    private static void userLogin(Bank bank, Scanner sc) {
        System.out.print("Enter Account No: ");
        String accNo = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine();

        Account acc = bank.verifyUser(accNo, pass);
        if (acc != null) {
            System.out.println("Login Successful! Welcome, " + acc.getAccountHolder());
            userDashboard(acc, bank, sc);
        } else {
            System.out.println("Invalid Account No or Password.");
        }
    }

    private static void adminLogin(Bank bank, Scanner sc) {
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine();

        Admin admin = bank.verifyAdmin(username, pass);
        if (admin != null) {
            System.out.println("Login Successful! Welcome, Admin " + admin.getName());
            adminDashboard(admin, bank, sc);
        } else {
            System.out.println("Invalid Username or Password.");
        }
    }

    private static void newUserCreateAccount(Bank bank, Scanner sc) {
        System.out.println("\n--- CREATE USER ACCOUNT ---");
        System.out.println("1. Savings Account");
        System.out.println("2. Current Account");
        System.out.print("Choice: ");
        int type = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Account Number: "); 
        String accNo = sc.nextLine();
        System.out.print("Enter Holder Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine();
        System.out.print("Initial Balance: ");
        double bal = sc.nextDouble();

        Account newAcc = null;
        if (type == 1) {
            newAcc = new SavingsAccount(accNo, name, pass, bal, 2.5);
            bank.addAccount(newAcc, 2.5);
        } else if (type == 2) {
            newAcc = new CurrentAccount(accNo, name, pass, bal, 2000.0);
            bank.addAccount(newAcc, 0);
        }

        if (newAcc != null) {
            System.out.println("Account Created Successfully!");
            userDashboard(newAcc, bank, sc);
        }
    }

    private static void newAdminCreateAccount(Bank bank, Scanner sc) {
        System.out.println("\n--- CREATE ADMIN ACCOUNT ---");
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Username: ");
        String user = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine();
        System.out.print("Enter Address: ");
        String addr = sc.nextLine();

        Admin newAdmin = new Admin(name, user, pass, addr); 
        bank.addAdmin(newAdmin);
        adminDashboard(newAdmin, bank, sc);
    }

    private static void userDashboard(Account acc, Bank bank, Scanner sc) {
        int choice;
        do {
            System.out.println("\n--- USER DASHBOARD (" + acc.getAccountNumber() + ") ---");
            System.out.println("1. Withdraw Amount");
            System.out.println("2. Deposit Amount");
            System.out.println("3. Check Balance");
            System.out.println("4. Transfer Fund");
            System.out.println("5. Exit to Main Menu");
            System.out.print("Choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter amount to withdraw: ");
                    bank.withdrawMoney(acc.getAccountNumber(), sc.nextDouble());
                    break;
                case 2:
                    System.out.print("Enter amount to deposit: ");
                    bank.depositMoney(acc.getAccountNumber(), sc.nextDouble());
                    break;
                case 3:
                    // Refresh balance from DB
                    acc = bank.findAccount(acc.getAccountNumber());
                    acc.checkBalance();
                    break;
                case 4:
                    System.out.print("Enter Destination Account No: ");
                    String to = sc.nextLine();
                    System.out.print("Enter Amount: ");
                    double amt = sc.nextDouble();
                    bank.transferFunds(acc.getAccountNumber(), to, amt);
                    break;
                case 5:
                    break;
                default:
                    System.out.println("Invalid Option!");
            }
        } while (choice != 5); 
    }

    private static void adminDashboard(Admin admin, Bank bank, Scanner sc) {
        int choice;
        do {
            System.out.println("\n--- ADMIN DASHBOARD (Admin: " + admin.getName() + ") ---");
            System.out.println("1. Create Saving Account");
            System.out.println("2. Create Current Account");
            System.out.println("3. See All User Details (Except Password)");
            System.out.println("4. See Total Amount in Bank");
            System.out.println("5. See Today's Total Transition");
            System.out.println("6. Exit to Main Menu");
            System.out.print("Choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter Account Number: ");
                    String sAcc = sc.nextLine();
                    System.out.print("Enter Holder Name: ");
                    String sName = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String sPass = sc.nextLine();
                    System.out.print("Initial Balance: ");
                    double sBal = sc.nextDouble();
                    System.out.print("Enter Interest Rate (%): ");
                    double rate = sc.nextDouble();
                    bank.addAccount(new SavingsAccount(sAcc, sName, sPass, sBal, rate), rate);
                    break;
                case 2:
                    System.out.print("Enter Account Number: ");
                    String cAcc = sc.nextLine();
                    System.out.print("Enter Holder Name: ");
                    String cName = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String cPass = sc.nextLine();
                    System.out.print("Initial Balance: ");
                    double cBal = sc.nextDouble();
                    bank.addAccount(new CurrentAccount(cAcc, cName, cPass, cBal, 2000.0), 0);
                    break;
                case 3:
                    bank.listAllUsersExcludingPassword();
                    break;
                case 4:
                    System.out.println("Total Amount in Bank: " + bank.getTotalBankAmount());
                    break;
                case 5:
                    bank.getTodaysTransactions();
                    break;
                case 6:
                    break;
                default:
                    System.out.println("Invalid Option!");
            }
        } while (choice != 6);
    }
}
