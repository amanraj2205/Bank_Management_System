# Bank Account Management System 🏦

A robust, terminal-based Java application built as a **4th Semester Object-Oriented Programming (OOP)** mini-project. This system simulates real-world core banking operations using Core Java and a PostgreSQL relational database for persistent storage.

---

## 📌 Project Overview
This project demonstrates the practical application of core OOP principles (Abstraction, Encapsulation, Inheritance, Polymorphism) and Java Database Connectivity (JDBC) in a standard 2-tier architecture. It features a complete Role-Based Access Control (RBAC) system, separating operations for standard **Users** and privileged **Admins**.

## 🛠️ Technology Stack
*   **Language**: Java (JDK 21)
*   **Database**: PostgreSQL
*   **Driver**: PostgreSQL JDBC Driver (`postgresql-42.7.10.jar`)
*   **Interface**: Terminal / Command Line Interface (CLI)

---

## ✨ Key Features
### For Users (Customers):
*   **Account Creation**: Open a Savings Account (with interest rate) or a Current Account (with an overdraft limit).
*   **Secure Login**: Authenticate using Account Number and Password.
*   **Dashboard Operations**:
    *   Deposit & Withdraw Funds.
    *   Real-time Balance Check.
    *   Peer-to-Peer Fund Transfers securely handled via database transactions.

### For Admins (Bank Staff):
*   **Admin Authentication**: Secure login portal for authorized personnel.
*   **Customer Management**: Open new accounts on behalf of users.
*   **Auditing & Analytics**:
    *   View details of all registered users (passwords are hidden).
    *   Calculate the total liquidity (Total Amount) stored in the bank.
    *   View a log of **Today's Transactions** to track daily financial activity.

---

## 📚 OOP Concepts Demonstrated
This project was strictly built following object-oriented methodologies:
1.  **Encapsulation**: Account balances and passwords are `private` or `protected`, modifiable only through secure setter methods like `deposit()` and `withdraw()`.
2.  **Inheritance**: `SavingsAccount` and `CurrentAccount` inherit core properties from the base `Account` class, eliminating redundant code.
3.  **Polymorphism**: The `withdraw(double amount)` method behaves differently at runtime. A `CurrentAccount` allows withdrawals up to an overdraft limit, overriding the base behavior.
4.  **Abstraction**: The intricate JDBC SQL queries and result sets are hidden inside the `Bank.java` class, providing simple interfaces to the `Main.java` runner.

---

## 🗄️ Database Setup Instructions
Before running the application, you must set up the PostgreSQL database.

1. Open PostgreSQL (pgAdmin or psql CLI) and create a database named `bank_db`:
   ```sql
   CREATE DATABASE bank_db;
   ```
2. Run the following scripts to create the necessary tables:

   **Accounts Table:**
   ```sql
   CREATE TABLE accounts (
       account_number VARCHAR(50) PRIMARY KEY,
       account_holder VARCHAR(100) NOT NULL,
       password VARCHAR(100) NOT NULL,
       balance NUMERIC DEFAULT 0,
       account_type VARCHAR(20) NOT NULL,
       interest_rate NUMERIC,
       overdraft_limit NUMERIC
   );
   ```

   **Admins Table:**
   ```sql
   CREATE TABLE admins (
       username VARCHAR(50) PRIMARY KEY,
       name VARCHAR(100) NOT NULL,
       password VARCHAR(100) NOT NULL,
       address VARCHAR(200)
   );
   ```

   **Transactions Table:**
   ```sql
   CREATE TABLE transactions (
       id SERIAL PRIMARY KEY,
       account_number VARCHAR(50) REFERENCES accounts(account_number),
       amount NUMERIC NOT NULL,
       transaction_type VARCHAR(50) NOT NULL,
       transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   ```

---

## 🚀 How to Run the Project
1. **Clone/Download** the repository to your local machine.
2. **Open in Eclipse** (or any Java IDE).
3. **Add JDBC Driver**: Ensure `postgresql-42.x.x.jar` is added to your project's Build Path (`.classpath`).
4. **Configure Credentials**: If your PostgreSQL password is not `postgres`, update the credentials inside `Bank.java`:
   ```java
   private final String user = "postgres";
   private final String password = "your_actual_password_here";
   ```
5. **Run**: Execute `Main.java` to launch the terminal application!
