import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(urlPatterns = {"/logout", "/Logout"})
public class Logout extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void init() throws ServletException {
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		HttpSession session = req.getSession(false);

        if(session != null)
            session.invalidate();

		res.sendRedirect("/login");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		HttpSession session = req.getSession(false);

        if(session != null)
            session.invalidate();

		res.sendRedirect("/login");
    }
}
