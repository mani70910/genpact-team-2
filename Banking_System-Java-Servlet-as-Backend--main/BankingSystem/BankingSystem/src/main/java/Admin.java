import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Admin")
public class Admin extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String INSERT_QUERY = "INSERT INTO admin_dashboard(full_name, address, mobile_no, email, account_type, balance, dob, account) VALUES(?,?,?,?,?,?,?,?)";

    public Admin() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        String full_name = request.getParameter("full_name");
        String address = request.getParameter("address");
        String mobile_no = request.getParameter("mobile_no");
        String email = request.getParameter("email");
        String account_type = request.getParameter("account_type");
        String balanceStr = request.getParameter("balance");
        double balance = Double.parseDouble(balanceStr);
        String dob = request.getParameter("dob");

        // Generate account number
        String accountNo = generateAccountNumber();

        // Generate temporary password
        String tempPassword = generateTemporaryPassword();

        Connection con = null;
        PreparedStatement ps = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingsystem", "root", "BankingSystem@12");
            ps = con.prepareStatement(INSERT_QUERY);
            ps.setString(1, full_name);
            ps.setString(2, address);
            ps.setString(3, mobile_no);
            ps.setString(4, email);
            ps.setString(5, account_type);
            ps.setDouble(6, balance);
            ps.setString(7, dob);
            ps.setString(8, accountNo); // Insert account number into the account column
            int count = ps.executeUpdate();

            if (count > 0) {
                pw.println("Customer registered successfully.");
                pw.println("Account number: " + accountNo);
                pw.println("Temporary password: " + tempPassword);
                response.setHeader("Refresh", "3; URL=Admin.html");
            } else {
                pw.println("Failed to register customer.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            pw.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis() + (int) (Math.random() * 1000);
    }

    private String generateTemporaryPassword() {
        return "PASS" + System.currentTimeMillis() + (int) (Math.random() * 1000);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
