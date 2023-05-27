import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/Logout")
public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Clear session
		request.getSession().invalidate();

		// Clear cookie
		response.setHeader("Set-Cookie", "JSESSIONID=; Max-Age=0; Path=/");

		// Trigger basic authentication
		// Trigger basic authentication
        response.setHeader("WWW-Authenticate", "Basic realm=\"Protected\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println("You have been logged out.");

		// Redirect to welcome page
		response.sendRedirect(request.getContextPath() + "/welcome.html");
	}
}
