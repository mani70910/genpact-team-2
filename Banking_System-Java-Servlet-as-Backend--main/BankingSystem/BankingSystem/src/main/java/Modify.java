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

@WebServlet("/Modify")
public class Modify extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String UPDATE_QUERY = "UPDATE admin_dashboard SET address = ?, mobile_no = ?, email = ?, account_type = ?, balance = ?, dob = ? WHERE full_name = ?";
    
    public Modify() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        String fullName = request.getParameter("fullName");
        String address = request.getParameter("address");
        String mobileNo = request.getParameter("mobileNo");
        String email = request.getParameter("email");
        String accountType = request.getParameter("accountType");
        double balance = Double.parseDouble(request.getParameter("balance"));
        String dob = request.getParameter("dob");

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingsystem", "root", "BankingSystem@12");
             PreparedStatement ps = con.prepareStatement(UPDATE_QUERY)) {

            ps.setString(1, address);
            ps.setString(2, mobileNo);
            ps.setString(3, email);
            ps.setString(4, accountType);
            ps.setDouble(5, balance);
            ps.setString(6, dob);
            ps.setString(7, fullName);

            int count = ps.executeUpdate();

            if (count > 0) {
                pw.println("Data updated successfully.");
                response.setHeader("Refresh", "3; URL=Admin.html");
            } else {
                pw.println("Failed to update data.");
            }
        } catch (SQLException se) {
            pw.println(se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            pw.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
