import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dao.UsersDAO;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection;
    private static String whitelist = "^[\\w\\d!_#@$+-]*$"; //"^[a-zA-Z0-9!_#@^$+]*$";

    public void init() throws ServletException {
        try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String username = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPass");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getMessage());
			e.printStackTrace();
			//throw new UnavailableException("Couldn't get db connection");
		}
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
        res.setContentType("text/html;charset=UTF-8");
        rd.forward(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String user = req.getParameter("user");
        String pass = req.getParameter("pass");
        UsersDAO userDAO = new UsersDAO(connection);

        // Sanitize User input
        if(!user.matches(whitelist)) { // || !pass.matches(whitelist)) {
            RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
            String error = "Bad input!!! Check your creds.";
            req.setAttribute("error", error);
            rd.include(req, res);
            return;
        }


        if(userDAO.checkCredentials(user, pass)) {
            HttpSession session = req.getSession();

            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30*60);
        } else {
            RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
            String error = "Username or Password is wrong!";
            req.setAttribute("error", error);
            rd.include(req, res);
            return;
        }

        res.setContentType("text/html;charset=UTF-8");
        System.err.println("Redirecting to home");
        res.sendRedirect("/home");
    }

}
