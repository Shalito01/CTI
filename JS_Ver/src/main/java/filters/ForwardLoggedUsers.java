package filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter({"/login", "/login.jsp", "/"})
public class ForwardLoggedUsers extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest HTTPRequest = (HttpServletRequest) request;
		HttpServletResponse HTTPResponse = (HttpServletResponse) response;
		HttpSession session = HTTPRequest.getSession(false);

		if(session != null && session.getAttribute("user") != null) {
			System.err.println("Redirect /home" + session.getAttribute("user"));
			HTTPResponse.sendRedirect("/home");
			return;
		}

		System.err.println("Allow /login");
		chain.doFilter(request, response);
	}
}
