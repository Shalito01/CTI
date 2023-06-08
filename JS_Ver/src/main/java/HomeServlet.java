import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.catalog.Catalog;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import beans.NodeBean;
import dao.TreeDAO;
import utils.JsonVars;
import utils.Util;

@WebServlet("/catalog")
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
        TreeMap<String, List<NodeBean>> sus = new TreeMap<>();
        
        // Fill Directory Tree
        List<NodeBean> preparedForPrint = new ArrayList<>();
        NodeBean root = new NodeBean("", "ROOT", null);


        try {
            tree = service.getAlberoCompleto();

            Util.recursionOnList(root, tree);
            // Util.recursion(root, service);
        } catch (SQLException e) {
            e.printStackTrace();
            // Error Handling
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
        System.err.println(gson.toJson(root, NodeBean.class));
        Util.send(res, new JsonVars("tree", gson.toJson(root)));
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
