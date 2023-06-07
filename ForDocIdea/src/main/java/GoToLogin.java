import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dao.TreeDAO;
import dao.UsersDAO;

@WebServlet("")
public class GoToLogin extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void init() {
        try {
            System.err.println("GOTOLOGIN --- INIT");
            ServletContext context = getServletContext();
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPass");
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, user, password);
            // Init Database (serve solo per inizializzare il db)
            // UsersDAO users = new UsersDAO(conn);
            // TreeDAO tree = new TreeDAO(conn);
            // users.setupUsersTable();
            // users.initUsers();
            // tree.setupCatalogTable();
            // tree.initTree();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            // throw new UnavailableException("Can't load database driver");
        } catch (Exception e) {
            e.printStackTrace();
            // throw new UnavailableException("Couldn't get db connection");
        }
	}

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
        res.setContentType("text/html;charset=UTF-8");
        rd.forward(req, res);
    }

}
