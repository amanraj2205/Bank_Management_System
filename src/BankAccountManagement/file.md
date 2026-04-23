# Bank Account Management System - Project Documentation

This documentation provides a comprehensive overview of the Bank Account Management system, covering its architecture, functional modules, database schema, and technical workflow.

## 1. Project Overview
The **Bank Account Management System** is a terminal-based Java application designed to handle core banking operations. It supports two primary roles: **Users** (customers) and **Admins** (bank staff). The system persists data in a **PostgreSQL** database, ensuring data durability and integrity.

---

## 2. System Architecture

The application follows a modular Object-Oriented approach with a clear separation between the UI (CLI) and business logic.

### 2.1 Core Components
- **`Main.java`**: The entry point of the application. It handles the Command Line Interface (CLI), user input, and menu navigation.
- **`Bank.java`**: The central logic layer. It manages database connections, CRUD operations, transaction logging, and validation logic.
- **`Account.java` (Base Class)**: Defines the foundational properties (`accountNumber`, `accountHolder`, `balance`, `password`) and behaviors (`deposit`, `withdraw`, `checkBalance`) of a bank account.
- **`SavingsAccount.java` & `CurrentAccount.java`**: Specialized subclasses that extend `Account`.
    - **Savings**: Includes an `interestRate`.
    - **Current**: Includes an `overdraftLimit`.
- **`Admin.java`**: A data class representing administrative users.

### 2.2 Database Schema
The system uses three primary tables in the `bank_db` database:

#### `accounts` Table
| Column | Type | Description |
| :--- | :--- | :--- |
| `account_number` | VARCHAR (PK) | Unique account identifier |
| `account_holder` | VARCHAR | Name of the user |
| `password` | VARCHAR | Plaintext password (Note: recommended to hash in production) |
| `balance` | NUMERIC | Current account balance |
| `account_type` | VARCHAR | "Savings" or "Current" |
| `interest_rate` | NUMERIC | Rate for Savings accounts |
| `overdraft_limit` | NUMERIC | Limit for Current accounts |

#### `admins` Table
| Column | Type | Description |
| :--- | :--- | :--- |
| `name` | VARCHAR | Admin's full name |
| `username` | VARCHAR (PK) | Unique login identifier |
| `password` | VARCHAR | Login password |
| `address` | VARCHAR | Physical address |

#### `transactions` Table
| Column | Type | Description |
| :--- | :--- | :--- |
| `id` | SERIAL (PK) | Unique transaction ID |
| `account_number` | VARCHAR | Associated account |
| `amount` | NUMERIC | Transaction amount |
| `transaction_type` | VARCHAR | "DEPOSIT", "WITHDRAWAL", "TRANSFER-IN/OUT" |
| `transaction_date` | TIMESTAMP | Auto-generated timestamp |

---

## 3. Workflow & User Journey

### 3.1 Main Menu
When the application starts, the user is presented with four main options:
1. **Login as User**
2. **Login as Admin**
3. **New User Create Account**
4. **New Admin Create Account**

### 3.2 User Workflow
1. **Registration / Login**: A user can create a new account (Savings/Current) or login using their account number and password.
2. **Dashboard**: Once authenticated, the user can:
   - **Check Balance**: Views the current balance fetched directly from the database to ensure accuracy.
   - **Deposit**: Adds funds to the account. Every deposit is synced to the DB and logged in the `transactions` table.
   - **Withdraw**: Deducts funds. Logic verifies if the balance (or overdraft, if applicable) is sufficient.
   - **Transfer**: Sends money to another user by account number. This performs a dual update (Debit sender, Credit receiver) in a single atomic-like operation within the `Bank` class.

### 3.3 Admin Workflow
1. **Registration / Login**: Admins use a username/password to access the dashboard.
2. **Administrative Tools**:
   - **Account Creation**: Admins can open new accounts for users.
   - **System Oversight**:
     - **List All Users**: Displays all account details except passwords for security.
     - **Total Bank Amount**: Calculates the sum of all balances across all users.
     - **Today's Transactions**: Provides a log of all banking activity performed on the current date.

---

## 4. Technical Implementation Details

### 4.1 Database Connectivity (JDBC)
The `Bank` class uses JDBC to interact with PostgreSQL. It encapsulates the connection logic:
```java
private Connection connect() throws SQLException {
    return DriverManager.getConnection(url, user, password);
}
```

### 4.2 Data Normalization & Persistence
- **Inheritance Mapping**: The system uses a single-table strategy (`accounts` table) to store both Savings and Current accounts, using `account_type` as a discriminator.
- **Transaction Safety**: Many operations update the database and then log the history:
```java
public void depositMoney(String accNo, double amount) {
    Account acc = findAccount(accNo);
    if (acc != null) {
        acc.deposit(amount); // In-memory update
        updateDBBalance(accNo, acc.balance); // DB sync
        logTransaction(accNo, amount, "DEPOSIT"); // History tracking
    }
}
```

---

## 5. Security & Best Practices
- **Encapsulation**: Fields like `password` are kept private with limited access.
- **Resource Management**: Uses **Try-With-Resources** for all SQL connections and statements to prevent memory leaks.
- **Validation**: Checks for account existence and balance sufficiency before performing transactions.

> [!TIP]
> **Future Improvements**:
> 1. **Password Hashing**: Implement BCrypt for storing passwords securely.
> 2. **GUI**: Transition from a CLI to a graphical interface using JavaFX.
> 3. **Input Validation**: Add more robust regex-based validation for account numbers and currency formats.
