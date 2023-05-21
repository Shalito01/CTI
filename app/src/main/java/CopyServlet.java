import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import com.mysql.jdbc.Driver;

@WebServlet("/copy")
public class CopyServlet extends HttpServlet {

    public void init() throws ServletException {
        // TODO: Connessione a DataBase per interrogare
    }

    public void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if(session == null) {
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
            rd.include(req, res);
            return;
        }

    }
}
