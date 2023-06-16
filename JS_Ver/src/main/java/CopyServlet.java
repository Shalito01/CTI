import javax.servlet.*;
import javax.servlet.http.*;

import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.servlet.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.NodeBean;
import dao.TreeDAO;
import utils.JsonId;
import utils.JsonVars;
import utils.Util;

@WebServlet("/copy")
@MultipartConfig
public class CopyServlet extends HttpServlet {
    private Connection connection;
    private List<NodeBean> tree;
    private static String whitelist = "^[\\w\\d!_#@$+-]*$"; //"^[a-zA-Z0-9!_#@^$+]*$";

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
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnavailableException("Couldn't get db connection");
		}
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
            jb.append(line);
        } catch (Exception e) { 
            Util.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
        
        List<JsonId> ids = new Gson().fromJson(jb.toString(), TypeToken.getParameterized(List.class, JsonId.class).getType());
        StringBuffer error = new StringBuffer();
        List<JsonVars> retTree = new ArrayList<>();
        // String old_id = req.getParameter("old_id");
        // String new_id = req.getParameter("new_id");
        
        for(var id : ids) {

            String old_id = id.getOld();
            String new_id = id.getNew();
            if(new_id.equals("ROOT")) new_id = "";
            if(old_id.equals("ROOT")) old_id = "";

            try {
                if(!old_id.matches("^[1-9]*$") || !new_id.matches("^[1-9]*$"))
                    throw new RuntimeException("Bad Input request");
            } catch (RuntimeException e) {
                error.append(String.format("Bad Format: {0} - {1} ", old_id, new_id));
                // Util.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                continue;
                // return;
            }

            try {
                // Eseguo i controlli sui nodi
                TreeDAO service = new TreeDAO(connection);

                if(!service.checkId(new_id) || !service.checkId(old_id))
                    throw new RuntimeException("Invalid id");

                int destCount = service.getNumChildren(new_id);
                if(destCount >= 9)
                    throw new RuntimeException("Impossibile aggiungere figli a questa destinazione");

                List<NodeBean> subTree = service.getSottoAlbero(old_id);

                // Preparo i nodi
                // Update a db con inserimento nodi
                service.copySubTree(subTree, subTree.get(0).getId(), destCount, new_id);

                List<NodeBean> new_tree = service.getSottoAlbero(new_id);
                NodeBean root = new_tree.get(0);
                new_tree.remove(0);
                Util.recursionOnList(root, new_tree);
                Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
                retTree.add(new JsonVars("tree", gson.toJson(root)));
            } catch (Exception e) {
                e.printStackTrace();
                // Error Handling
                error.append(e.getMessage());
                continue;
                // Util.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                // return;
            }
        }

        if(!error.toString().isEmpty()) {
            Util.sendError(res, HttpServletResponse.SC_BAD_REQUEST, error.toString());
            return;
        }

         Util.send(res, new JsonVars("msg", (retTree.size() > 1) ? "refresh" : "chill"));
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
