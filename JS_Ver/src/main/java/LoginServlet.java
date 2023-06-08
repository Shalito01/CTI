import javax.servlet.*;
import javax.servlet.http.*;

import com.google.gson.Gson;

import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dao.UsersDAO;
import utils.JsonVars;
import utils.Util;

@WebServlet("/login")
@MultipartConfig
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

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String user = req.getParameter("user");
        String pass = req.getParameter("pass");
        UsersDAO userDAO = new UsersDAO(connection);

        // Sanitize User input
        if(user == null || !user.matches(whitelist)) { // || !pass.matches(whitelist)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().println("Wrong fromat for username or password");
            return;
        }


        if(!userDAO.checkCredentials(user, pass)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().println("Wrong username and/or password");
            return;
        }
    
        HttpSession session = req.getSession();

        session.setAttribute("user", user);
        session.setMaxInactiveInterval(30*60);
        Util.send(res, new JsonVars("username", user));
    }

}
