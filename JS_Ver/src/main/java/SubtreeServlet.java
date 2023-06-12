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

@WebServlet("/catalog/subcatalog")
@MultipartConfig
public class SubtreeServlet extends HttpServlet {

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

    public void noDbUpdateId(List<NodeBean> sottoAlbero, String old_root, String new_root, int numCount) {
		String root = new_root + String.valueOf(numCount + 1);

        
        for(var nodo : sottoAlbero) {
            if(nodo == sottoAlbero.get(0)){
                nodo.setIdPadre(new_root);
                nodo.setID(root);
            } else {
                nodo.setID(root + nodo.getId().substring(old_root.length()));
                nodo.setIdPadre(root + nodo.getIdPadre().substring(old_root.length()));
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        String new_id = req.getParameter("new_id");
        String old_id = req.getParameter("old_id");
        if(old_id.equals("ROOT")) old_id = "";
        if(new_id.equals("ROOT")) new_id = "";

        TreeDAO service = new TreeDAO(connection);

        try {
            if(!old_id.matches("^[1-9]*$") ||!new_id.matches("^[1-9]*$"))
                throw new RuntimeException("Bad Input request");
            if(!service.checkId(new_id) || !service.checkId(old_id))
                throw new RuntimeException("Invalid id");
        } catch (Exception e) {
            Util.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
        
        NodeBean root;
        

        try {
            tree = service.getSottoAlbero(old_id);
            root = tree.get(0);
            int num = service.getNumChildren(new_id);

            if(num == 9) throw new Exception("Già al max");

            noDbUpdateId(tree, old_id, new_id, num);

            Util.recursionOnList(root, tree.subList(1, tree.size()));
        } catch (Exception e) {
            e.printStackTrace();
            // Error Handling
            Util.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
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
