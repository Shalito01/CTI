import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import beans.NodeBean;
import dao.TreeDAO;
import utils.JsonVars;
import utils.Util;

@WebServlet("/catalog/update")
@MultipartConfig
public class UpdateServlet extends HttpServlet {

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
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        TreeDAO dao = new TreeDAO(connection);
        String id = req.getParameter("id");
        String name = req.getParameter("catalog_name");

        try {
            dao.modificaNome(id, name);
        } catch (SQLException e) {
            e.printStackTrace();
            Util.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erroe di modifica nel DB!");
            // Error Handling
        }


        Util.send(res, new JsonVars("name", name));
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
