import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Customer_Login_Page")
public class Customer_Login_Page extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String SELECT_QUERY = "SELECT * FROM admin_dashboard WHERE full_name=? AND dob=?";
    private static final String INSERT_QUERY = "INSERT INTO customer_login (full_name, address, account, mobile_no, email, account_type, balance) VALUES (?, ?, ?, ?, ?, ?, ?)";

    public Customer_Login_Page() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String dob = request.getParameter("dob"); // Assuming the password field is for Date of Birth
        String password = dob; // Set default password as Date of Birth

        Connection conn = null;
        PreparedStatement selectStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingsystem", "root", "BankingSystem@12");
            
            // Retrieve customer details from admin_dashboard
            selectStmt = conn.prepareStatement(SELECT_QUERY);
            selectStmt.setString(1, username);
            selectStmt.setString(2, password);
            rs = selectStmt.executeQuery();

            if (rs.next()) {
                // Retrieve customer details
                String fullName = rs.getString("full_name");
                String address = rs.getString("address");
                String account = rs.getString("account");
                String mobileNo = rs.getString("mobile_no");
                String email = rs.getString("email");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                
                // Store customer details in customer_login table
                insertStmt = conn.prepareStatement(INSERT_QUERY);
                insertStmt.setString(1, fullName);
                insertStmt.setString(2, address);
                insertStmt.setString(3, account);
                insertStmt.setString(4, mobileNo);
                insertStmt.setString(5, email);
                insertStmt.setString(6, accountType);
                insertStmt.setDouble(7, balance);
                insertStmt.executeUpdate();
                
                // Encode customer details into URL
                String encodedFullName = URLEncoder.encode(fullName, "UTF-8");
                String encodedAccount = URLEncoder.encode(account, "UTF-8");
                String encodedAddress = URLEncoder.encode(address, "UTF-8");
                String encodedMobileNo = URLEncoder.encode(mobileNo, "UTF-8");
                String encodedEmail = URLEncoder.encode(email, "UTF-8");
                String encodedAccountType = URLEncoder.encode(accountType, "UTF-8");
                String encodedBalance = URLEncoder.encode(String.valueOf(balance), "UTF-8");

                // Redirect to customer dashboard with customer details in URL
                response.sendRedirect("Customer_Dashboard.html?fullName=" + encodedFullName + "&account=" + encodedAccount  + "&address=" + encodedAddress + "&mobileNo=" + encodedMobileNo + "&email=" + encodedEmail + "&accountType=" + encodedAccountType + "&balance=" + encodedBalance);

            } else {
                // Invalid credentials, display error message
                out.println("Invalid username or password");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (selectStmt != null) selectStmt.close();
                if (insertStmt != null) insertStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
