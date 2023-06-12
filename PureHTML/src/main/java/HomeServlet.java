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
    private static String whitelist = "^[\\w \\d!_#@$+-]*$"; //"^[a-zA-Z0-9!_#@^$+]*$";

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
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            req.getRequestDispatcher("/error.jsp").include(req, res);
            return;
        }

    }
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        TreeDAO service = new TreeDAO(connection);
        
        // String id = req.getParameter("id");
        String name = req.getParameter("name");
        String parent_id = req.getParameter("parentId");

        try {
            if(!parent_id.matches("^[1-9]*$") || !name.matches(whitelist))
                throw new Exception("Formato parametri non corretto");
            if(!service.checkId(parent_id))
                throw new RuntimeException("Invalid id");
        } catch (Exception ex) {
            ex.printStackTrace();
            // res.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            req.setAttribute("error", ex.getMessage());
            req.getRequestDispatcher("/error.jsp").include(req, res);
            return;
        }

        try {
            int parent = service.getNumChildren(parent_id);
            System.err.println(parent);
            String id = parent_id + String.valueOf(service.getNumChildren(parent_id) + 1);
            service.inserisciUnFiglio(id, name, parent_id);
        } catch (Exception e) {
            e.printStackTrace();
            // Error Handling
            // res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/error.jsp").include(req, res);
            return;
        }

        res.sendRedirect("/home");

    }

    @Override
    public void destroy() {
        if(connection == null) return;
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
