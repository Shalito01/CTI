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
@MultipartConfig
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
        
        // Fill Directory Tree
        NodeBean root = new NodeBean("", "ROOT", null);


        try {
            tree = service.getAlberoCompleto();

            Util.recursionOnList(root, tree);
        } catch (SQLException e) {
            e.printStackTrace();
            // Error Handling
            Util.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore DB");
            return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
        System.err.println(gson.toJson(root, NodeBean.class));
        Util.send(res, new JsonVars("tree", gson.toJson(root)));
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        TreeDAO service = new TreeDAO(connection);
        
        // String id = req.getParameter("id");
        String name = req.getParameter("name");
        String parent_id = req.getParameter("parentId");
        NodeBean root;
        JsonVars msg;

        try {
            if(!parent_id.matches("^[1-9]*$") || !name.matches(whitelist))
                throw new Exception("Formato parametri non corretto");
        } catch (Exception ex) {
            Util.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Errore nel formato dei parametri");
            return;
        }

        try {
            String id = parent_id + String.valueOf(service.getNumChildren(parent_id) + 1);
            service.inserisciUnFiglio(id, name, parent_id);
            root = new NodeBean(id, name, parent_id);
            Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
            msg = new JsonVars("msg", gson.toJson(root));
        } catch (SQLException e) {
            e.printStackTrace();
            // Error Handling
            Util.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        Util.send(res, msg);
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
