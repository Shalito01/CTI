import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.List;

import beans.NodeBean;
import dao.TreeDAO;
import dao.UsersDAO;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private Connection connection;
    private List<NodeBean> tree;

    public void init() throws ServletException {
        try {
            ServletContext context = getServletContext();
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPass");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            // throw new UnavailableException("Can't load database driver");
        } catch (Exception e) {
            e.printStackTrace();
            // throw new UnavailableException("Couldn't get db connection");
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        TreeDAO service = new TreeDAO(connection);

        try {
            tree = service.getAlberoCompleto();
            req.setAttribute("catalog_tree", tree);
            res.setContentType("text/html;charset=UTF-8");
            req.getRequestDispatcher("/home.jsp").forward(req, res);
        } catch (Exception e) {
            e.printStackTrace();
            // Error Handling
        }

    }
}
