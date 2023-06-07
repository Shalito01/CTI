import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import beans.NodeBean;
import dao.TreeDAO;

@WebServlet("/copy")
public class CopyServlet extends HttpServlet {
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
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnavailableException("Couldn't get db connection");
		}
    }


    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
       // HttpSession session = req.getSession(false);

       // if(session == null) {
       //     res.sendRedirect("/login.html");
       //     return;
       // }

        String id = req.getParameter("old_id");

        TreeDAO service = new TreeDAO(connection);
        try {
            tree = service.getAlberoCompleto();
            for (NodeBean nodeBean : tree) {
                System.err.println(nodeBean.getId() + " ||| " + nodeBean.getIdPadre());

                if((nodeBean.getId().equals(id)
                            || nodeBean.getIdPadre().equals(id))
                            || (nodeBean.getIdPadre().length() > id.length() && nodeBean.getIdPadre().substring(0, id.length()).equals(id)))
                {
                    nodeBean.select(true);
                }
            }

            req.setAttribute("old_id", id);
            req.setAttribute("catalog_tree", tree);
            res.setContentType("text/html;charset=UTF-8");
            req.getRequestDispatcher("/copy.jsp").forward(req, res);
        } catch (Exception e) {
            e.printStackTrace();
            // Error Handling
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        String old_id = req.getParameter("old_id");
        String new_id = req.getParameter("new_id");

        try {
            // Eseguo i controlli sui nodi
            TreeDAO service = new TreeDAO(connection);

            int destCount = service.getNumChildren(new_id);
            if(destCount >= 9)
                throw new RuntimeException("Impossibile aggiungere figli a queseta destinazione");

            List<NodeBean> subTree = service.getSottoAlbero(old_id);

            // Preparo i nodi
            // Update a db con inserimento nodi
            service.copySubTree(subTree, subTree.get(0).getId(), destCount, new_id);


        } catch (Exception e) {
            e.printStackTrace();
            return;
            // Error Handling
        }

        res.sendRedirect("/home");
    }
}
