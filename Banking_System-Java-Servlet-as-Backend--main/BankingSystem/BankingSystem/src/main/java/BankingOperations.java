import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/BankingOperations")
public class BankingOperations extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // Retrieve form data
        String accountNumber = request.getParameter("accountNumber");
        String password = request.getParameter("password");
        String amount = request.getParameter("amount");
        String withdrawAmount = request.getParameter("withdrawAmount");
        
        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/bankingsystem";
        String user = "root";
        String passwordDB = "BankingSystem@12";
        
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            
            // Connect to the database
            Connection conn = DriverManager.getConnection(url, user, passwordDB);
            
            // Retrieve current balance
            PreparedStatement selectStmt = conn.prepareStatement("SELECT balance FROM admin_dashboard WHERE account = ? AND dob = ?");
            selectStmt.setString(1, accountNumber);
            selectStmt.setString(2, password);
            ResultSet rs = selectStmt.executeQuery();
            double currentBalance = 0.0;
            if (rs.next()) {
                currentBalance = rs.getDouble("balance");
            }
            
            // Prepare SQL statement based on deposit or withdrawal
            PreparedStatement pstmt;
            if (amount != null && !amount.isEmpty()) {
                // Deposit operation
                double depositAmount = Double.parseDouble(amount);
                currentBalance += depositAmount; // Update current balance
                pstmt = conn.prepareStatement("UPDATE admin_dashboard SET balance = ? WHERE account = ? AND dob = ?");
                pstmt.setDouble(1, currentBalance);
                
                // Insert transaction history record for deposit
                PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO transaction_history (account_number, transaction_type, amount, balance) VALUES (?, 'Deposit', ?, ?)");
                insertStmt.setString(1, accountNumber);
                insertStmt.setDouble(2, depositAmount);
                insertStmt.setDouble(3, currentBalance);
                insertStmt.executeUpdate();
            } else if (withdrawAmount != null && !withdrawAmount.isEmpty()) {
                // Withdrawal operation
                double withdrawAmt = Double.parseDouble(withdrawAmount);
                if (currentBalance >= withdrawAmt) { // Check if balance is sufficient for withdrawal
                    currentBalance -= withdrawAmt; // Update current balance
                    pstmt = conn.prepareStatement("UPDATE admin_dashboard SET balance = ? WHERE account = ? AND dob = ?");
                    pstmt.setDouble(1, currentBalance);
                    
                    // Insert transaction history record for withdrawal
                    PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO transaction_history (account_number, transaction_type, amount, balance) VALUES (?, 'Withdrawal', ?, ?)");
                    insertStmt.setString(1, accountNumber);
                    insertStmt.setDouble(2, withdrawAmt);
                    insertStmt.setDouble(3, currentBalance);
                    insertStmt.executeUpdate();
                } else {
                    out.println("<h3>Error: Insufficient Balance for Withdrawal!</h3>");
                    conn.close();
                    return; // End processing
                }
            } else {
                out.println("<h3>Error: Invalid Operation!</h3>");
                conn.close();
                return; // End processing
            }
            
            // Set account number and password parameters
            pstmt.setString(2, accountNumber);
            pstmt.setString(3, password);
            
            // Execute the update query
            int rowsAffected = pstmt.executeUpdate();
            
            // Check if any rows were affected
            if (rowsAffected > 0) {
                out.println("<h3>Transaction Successful!</h3>");
            } else {
                out.println("<h3>Transaction Failed! Please check your account details.</h3>");
            }
            
            // Close the database connection
            conn.close();
        } catch (Exception e) {
            out.println("<h3>Exception occurred: " + e.getMessage() + "</h3>");
        }
    }
}
