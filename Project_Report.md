# Bank Account Management System
## Comprehensive Project Report

---

## Table of Contents
1. [Introduction](#1-introduction)
2. [Project Objectives and Scope](#2-project-objectives-and-scope)
3. [System Architecture and Technology Stack](#3-system-architecture-and-technology-stack)
4. [System Analysis and Design](#4-system-analysis-and-design)
5. [Implementation Details: Core Modules](#5-implementation-details-core-modules)
   - 5.1 Main.java Analysis
   - 5.2 Bank Database Logic
   - 5.3 Account Management
6. [Database Schema and Design](#6-database-schema-and-design)
7. [Object-Oriented Principles Applied](#7-object-oriented-principles-applied)
8. [System Workflows and Operations](#8-system-workflows-and-operations)
9. [Future Enhancements and Conclusion](#9-future-enhancements-and-conclusion)
10. [Appendix A: Complete Source Code](#10-appendix-a-complete-source-code)

---

## 1. Introduction
The Bank Account Management System is a comprehensive, terminal-based Java application designed to simulate real-world banking operations. It provides a robust platform for managing customer accounts, facilitating financial transactions, and enabling administrative oversight. By leveraging Object-Oriented Programming (OOP) principles and a relational PostgreSQL database, the system ensures data integrity, security, and scalability. This report provides an in-depth analysis of the system, heavily focusing on the entry point (`Main.java`) and how it orchestrates the various components of the application.

## 2. Project Objectives and Scope
### 2.1 Objectives
- To develop a secure and user-friendly console application for banking.
- To implement Role-Based Access Control (RBAC) separating "Users" and "Admins".
- To demonstrate practical applications of Java JDBC for database persistence.
- To use OOP concepts (Inheritance, Polymorphism, Encapsulation) effectively.

### 2.2 Scope
The system currently supports:
- **User Operations**: Account creation, authentication, deposits, withdrawals, balance inquiries, and inter-account fund transfers.
- **Admin Operations**: Administrative account creation, user oversight, total bank liquidity tracking, and daily transaction auditing.
- **Account Types**: Savings Accounts (with interest rates) and Current Accounts (with overdraft limits).

## 3. System Architecture and Technology Stack
### 3.1 Technology Stack
- **Language**: Java (JDK 21)
- **Database**: PostgreSQL 16+
- **Driver**: PostgreSQL JDBC Driver (`postgresql-42.7.10.jar`)
- **IDE**: Eclipse IDE
- **Architecture**: Monolithic Console Application with a 2-Tier Architecture (Client Logic -> Database).

### 3.2 System Architecture
The architecture is divided into three primary logical layers:
1. **Presentation Layer**: Handled entirely by `Main.java` using `Scanner` for standard I/O.
2. **Business Logic Layer**: Comprises `Bank.java`, `Account.java`, `Admin.java`, and the specialized account classes.
3. **Data Access Layer**: Integrated within `Bank.java` using JDBC to communicate with the `bank_db` PostgreSQL instance.

---

## 4. System Analysis and Design
The system is modeled around the real-world entities of a bank:
- **Bank**: The central manager that holds all records and processes requests.
- **Account**: The core financial entity holding a balance.
- **User/Holder**: The client who owns the account.
- **Admin**: The privileged user managing the system.

### Use Case Model
1. **User**: Can login, deposit money, withdraw money, check balance, and transfer funds.
2. **Admin**: Can login, create user accounts (Savings/Current), view all users, check total bank balance, and audit daily transactions.
3. **Guest**: Can create a new User or Admin account from the main menu.

---

## 5. Implementation Details: Core Modules

### 5.1 Main.java Analysis
The `Main.java` file acts as the primary controller for the application. It utilizes a state-machine-like loop to keep the user engaged until they explicitly choose to exit.

#### The `main` Method
The entry point initializes a `Scanner` for input and instantiates the `Bank` class. It presents a continuous `do-while` loop showing the Main Menu:
1. Login as User
2. Login as Admin
3. New User Create Account
4. New Admin Create Account
5. Exit

#### Authentication Flow
- **`userLogin(Bank bank, Scanner sc)`**: Prompts for `Account No` and `Password`. It calls `bank.verifyUser()`. On success, it routes to `userDashboard()`.
- **`adminLogin(Bank bank, Scanner sc)`**: Prompts for `Username` and `Password`. Calls `bank.verifyAdmin()`. Routes to `adminDashboard()`.

#### Account Creation Flow
- **`newUserCreateAccount`**: Asks the user to choose between a Savings (1) or Current (2) account. It collects necessary details (Name, Password, Initial Balance) and automatically instantiates the respective `SavingsAccount` or `CurrentAccount` object. It then registers this with the database via `bank.addAccount()`.
- **`newAdminCreateAccount`**: Similar to user creation, but gathers admin-specific details (Username, Name, Address) and saves them via `bank.addAdmin()`.

#### User Dashboard (`userDashboard`)
Once logged in, a user can:
- **Withdraw**: Invokes `bank.withdrawMoney()`.
- **Deposit**: Invokes `bank.depositMoney()`.
- **Check Balance**: Refreshes the account state from the DB using `bank.findAccount()` and displays the balance.
- **Transfer Funds**: Asks for destination account and amount, executing `bank.transferFunds()`.

#### Admin Dashboard (`adminDashboard`)
Admins have elevated privileges:
- **Create Accounts**: Can directly create Savings or Current accounts for users.
- **See All User Details**: Invokes `bank.listAllUsersExcludingPassword()`.
- **See Total Amount**: Invokes `bank.getTotalBankAmount()`.
- **Today's Transactions**: Invokes `bank.getTodaysTransactions()`.

### 5.2 Bank Database Logic (`Bank.java`)
This module handles all JDBC connections. Key operations include:
- `connect()`: Establishes the connection to `jdbc:postgresql://localhost:5432/bank_db`.
- `addAccount()` / `addAdmin()`: Executes `INSERT` statements to persist entities.
- `transferFunds()`: A complex method that ensures the sender is debited and the receiver is credited, while logging both events in the `transactions` table.

### 5.3 Account Management
- `Account.java`: The base class containing common fields (`accountNumber`, `accountHolder`, `balance`).
- `SavingsAccount.java`: Extends `Account`, adds `interestRate` and `addInterest()` capabilities.
- `CurrentAccount.java`: Extends `Account`, overrides `withdraw()` to factor in the `overdraftLimit`.

---

## 6. Database Schema and Design
The system uses three main tables to ensure data normalization and integrity.

### Tables Overview
1. **`accounts`**: Stores user accounts. Uses Single-Table Inheritance for different account types.
   - Columns: `account_number` (PK), `account_holder`, `password`, `balance`, `account_type`, `interest_rate`, `overdraft_limit`.
2. **`admins`**: Stores admin credentials.
   - Columns: `username` (PK), `name`, `password`, `address`.
3. **`transactions`**: An audit log for all financial movements.
   - Columns: `id` (PK), `account_number` (FK), `amount`, `transaction_type`, `transaction_date`.

---

## 7. Object-Oriented Principles Applied
1. **Encapsulation**: Passwords and balances are kept private/protected. They are manipulated through controlled methods (`deposit()`, `withdraw()`).
2. **Inheritance**: `SavingsAccount` and `CurrentAccount` inherit from the `Account` base class, reusing the core fields and methods.
3. **Polymorphism**: The `withdraw()` method is overridden in `CurrentAccount` to allow withdrawals up to the overdraft limit, showcasing runtime polymorphism. In `Main.java`, `Account acc` is used to hold instances of both child classes.

---

## 8. System Workflows and Operations

### The Transfer Workflow
When a user selects "Transfer Fund" in `Main.java`:
1. `Main.java` collects the `toAccount` and `amount`.
2. It passes these to `bank.transferFunds(from, to, amount)`.
3. `Bank.java` queries the DB for both accounts.
4. The logic validates that the sender has sufficient balance.
5. `sender.withdraw(amount)` and `receiver.deposit(amount)` are called in-memory.
6. `updateDBBalance()` is called twice to sync the new balances to PostgreSQL.
7. `logTransaction()` is called twice to record `TRANSFER-OUT` and `TRANSFER-IN`.

---

## 9. Future Enhancements and Conclusion
### 9.1 Limitations
- **Security**: Passwords are currently stored in plaintext. 
- **Transactions**: JDBC manual transaction management (`conn.setAutoCommit(false)`) is needed to prevent partial updates during network failures.

### 9.2 Future Scope
- Implementation of a Graphical User Interface (JavaFX or Swing).
- Integration of Spring Boot and JPA/Hibernate for better database management.
- Adding BCrypt password hashing.

### 9.3 Conclusion
The Bank Account Management System effectively demonstrates the integration of core Java concepts with relational databases. `Main.java` serves as a highly efficient and intuitive CLI controller, proving that complex, multi-layered applications can be structured cleanly using procedural flows mapped to OOP business logic.

---
---

## 10. Appendix A: Complete Source Code
To fulfill the comprehensive documentation requirement, the entire source code of the project is attached below.

### A.1 Main.java
```java
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
```

### A.2 Account.java
```java
package BankAccountManagement;

class Account {
    private String accountNumber;
    private String accountHolder;
    private String password;
    protected double balance;

    public Account(String accountNumber, String accountHolder, String password, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.password = password;
        this.balance = balance;
    }

    public String getPassword() { return password; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println(amount + " deposited. New balance: " + balance);
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive.");
            return;
        }
        if (amount <= balance) {
            balance -= amount;
            System.out.println(amount + " withdrawn. New balance: " + balance);
        } else {
            System.out.println("Insufficient balance.");
        }
    }

    public void checkBalance() {
        System.out.println("Balance: " + balance);
    }
}
```

### A.3 CurrentAccount.java
```java
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
```

### A.4 SavingsAccount.java
```java
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
```

### A.5 Admin.java
```java
package BankAccountManagement;

public class Admin {
    private String name;
    private String username;
    private String password;
    private String address;

    public Admin(String name, String username, String password, String address) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.address = address;
    }

    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getAddress() { return address; }
}
```
*(End of Document)*
