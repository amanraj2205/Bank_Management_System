package BankAccountManagement;
import java.sql.*;

class Bank {
    private final String url = "jdbc:postgresql://localhost:5432/bank_db";
    private final String user = "postgres";
    private final String password = "postgres";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void addAccount(Account account, double interestRate) {
        String sql = "INSERT INTO accounts(account_number, account_holder, password, balance, account_type, interest_rate, overdraft_limit) VALUES(?,?,?,?,?,?,?)";
        
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getAccountHolder()); 
            pstmt.setString(3, account.getPassword());
            pstmt.setDouble(4, account.balance);
            
            if (account instanceof SavingsAccount) { 
                pstmt.setString(5, "Savings");
                pstmt.setDouble(6, interestRate); 
                pstmt.setDouble(7, 0);
            } else {
                pstmt.setString(5, "Current");
                pstmt.setDouble(6, 0);
                pstmt.setDouble(7, 2000); // Fixed limit 
            }
            
            pstmt.executeUpdate();
            System.out.println("Account saved to database.");
        } catch (SQLException e) {
            System.out.println("Error saving account: " + e.getMessage());
        }
    }

    public void addAdmin(Admin admin) {
        String sql = "INSERT INTO admins (name, username, password, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, admin.getName());
            pstmt.setString(2, admin.getUsername());
            pstmt.setString(3, admin.getPassword());
            pstmt.setString(4, admin.getAddress());
            pstmt.executeUpdate();
            System.out.println("Admin account created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating admin: " + e.getMessage());
        }
    }

    public void updateBalance(String accNo, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accNo);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    public void listAllAccounts() {
        String sql = "SELECT * FROM accounts";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- DB REGISTERED ACCOUNTS ---");
            while (rs.next()) {
                System.out.println("No: " + rs.getString("account_number") + 
                                   " | Holder: " + rs.getString("account_holder") + 
                                   " | Balance: " + rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching accounts: " + e.getMessage());
        }
    }
        // 2. REQUIRED: findAccount Method (Database Version)
        public Account findAccount(String accountNumber) {
            String sql = "SELECT * FROM accounts WHERE account_number = ?";
            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, accountNumber);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String accNo = rs.getString("account_number");
                    String name = rs.getString("account_holder");
                    String pass = rs.getString("password");
                    double bal = rs.getDouble("balance");
                    String type = rs.getString("account_type");
                    double rate = rs.getDouble("interest_rate");
                    double limit = rs.getDouble("overdraft_limit");

                    if ("Savings".equals(type)) {
                        return new SavingsAccount(accNo, name, pass, bal, rate);
                    } else {
                        return new CurrentAccount(accNo, name, pass, bal, limit);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Database Error: " + e.getMessage());
            }
            System.out.println("Account not found.");
            return null;
        }

        public Account verifyUser(String accNo, String password) {
            Account acc = findAccount(accNo);
            if (acc != null && acc.getPassword().equals(password)) {
                return acc;
            }
            return null;
        }

        public Admin verifyAdmin(String username, String password) {
            String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";
            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return new Admin(rs.getString("name"), rs.getString("username"), rs.getString("password"), rs.getString("address"));
                }
            } catch (SQLException e) {
                System.out.println("Admin verify error: " + e.getMessage());
            }
            return null;
        }

        public void logTransaction(String accNo, double amount, String type) {
            String sql = "INSERT INTO transactions (account_number, amount, transaction_type) VALUES (?, ?, ?)";
            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, accNo);
                pstmt.setDouble(2, amount);
                pstmt.setString(3, type);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Transaction log error: " + e.getMessage());
            }
        }

        public void depositMoney(String accNo, double amount) {
            if (accNo == null || amount <= 0) {
                System.out.println("Invalid deposit parameters.");
                return;
            }
            Account acc = findAccount(accNo);
            if (acc != null) {
                acc.deposit(amount);
                updateDBBalance(accNo, acc.balance);
                logTransaction(accNo, amount, "DEPOSIT");
            }
        }

        public void withdrawMoney(String accNo, double amount) {
            if (accNo == null || amount <= 0) {
                System.out.println("Invalid withdrawal parameters.");
                return;
            }
            Account acc = findAccount(accNo);
            if (acc != null) {
                double oldBal = acc.balance;
                acc.withdraw(amount);
                if (acc.balance != oldBal) { // Withdrawal successful
                    updateDBBalance(accNo, acc.balance);
                    logTransaction(accNo, amount, "WITHDRAWAL");
                }
            }
        }

        // 3. REQUIRED: transferFunds Method (Database Version)
        public void transferFunds(String fromAcc, String toAcc, double amount) {
            if (amount <= 0) {
                System.out.println("Transfer amount must be positive.");
                return;
            }
            Account sender = findAccount(fromAcc);
            Account receiver = findAccount(toAcc);

            if (sender != null && receiver != null) {
                sender.withdraw(amount);
                receiver.deposit(amount);
                
                // Sync balances back to database
                updateDBBalance(sender.getAccountNumber(), sender.balance);
                updateDBBalance(receiver.getAccountNumber(), receiver.balance);
                
                logTransaction(fromAcc, amount, "TRANSFER-OUT");
                logTransaction(toAcc, amount, "TRANSFER-IN");
                
                System.out.println("Transferred " + amount + " successfully.");
            }
        }

        public double getTotalBankAmount() {
            String sql = "SELECT SUM(balance) FROM accounts";
            try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            } catch (SQLException e) {
                System.out.println("Total balance query error: " + e.getMessage());
            }
            return 0;
        }

        public void getTodaysTransactions() {
            String sql = "SELECT * FROM transactions WHERE transaction_date::date = CURRENT_DATE";
            try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("\n--- TODAY'S TRANSACTIONS ---");
                while (rs.next()) {
                    System.out.println("Acc: " + rs.getString("account_number") + 
                                       " | Amt: " + rs.getDouble("amount") + 
                                       " | Type: " + rs.getString("transaction_type") + 
                                       " | Time: " + rs.getTimestamp("transaction_date"));
                }
            } catch (SQLException e) {
                System.out.println("Transactions query error: " + e.getMessage());
            }
        }

        public void listAllUsersExcludingPassword() {
            String sql = "SELECT account_number, account_holder, balance, account_type FROM accounts";
            try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("\n--- ALL USER DETAILS ---");
                while (rs.next()) {
                    System.out.println("No: " + rs.getString("account_number") + 
                                       " | Holder: " + rs.getString("account_holder") + 
                                       " | Type: " + rs.getString("account_type") + 
                                       " | Balance: " + rs.getDouble("balance"));
                }
            } catch (SQLException e) {
                System.out.println("Error fetching user details: " + e.getMessage());
            }
        }

        private void updateDBBalance(String accNo, double newBalance) {
            String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, newBalance);
                pstmt.setString(2, accNo);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Sync Error: " + e.getMessage());
            }
        }
}
