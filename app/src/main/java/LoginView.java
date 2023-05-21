import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import com.mysql.jdbc.Driver;

@WebServlet("/login")
public class LoginView extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private String pass = "test";
    private String user = "dady";
    private String pstmt = "SELECT username, password FROM users WHERE username = ? AND password = ?";

    public void init() throws ServletException {
        // TODO: Connessione a DataBase per interrogare
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
        rd.include(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String user = req.getParameter("user");
        String pass = req.getParameter("pass");

        if(this.user.equals(user) && this.pass.equals(pass)) {
            HttpSession session = req.getSession(false);

            if(session != null) session.invalidate();

            session = req.getSession();
            session.setAttribute("user", "MakaronaMePesto");
            session.setMaxInactiveInterval(30*60);
//            Cookie username = new Cookie("user", user);
//            username.setMaxAge(30*60);
//            res.addCookie(username);
            res.setContentType("text/html");
            res.sendRedirect("index.jsp");
        } else {
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
            PrintWriter out = res.getWriter();
            out.println("<font class=\"error\"color=red> Username or Password is wrong!</font>");
            rd.include(req, res);
        }
    }
}
