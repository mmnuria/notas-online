package filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import java.util.HashMap;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		ServletContext context = httpRequest.getServletContext();
		HttpSession session = httpRequest.getSession();
		RequestDispatcher dispatcher = null;

		HashMap<String, String> usersMap;

		if (context.getAttribute("users") == null) {
			usersMap = new HashMap<String, String>();
			usersMap.put("pro0", "23456733H");
			usersMap.put("pro1", "10293756L");
			usersMap.put("pro2", "06374291A");
			usersMap.put("pro3", "65748923M");
			usersMap.put("alu0", "12345678W");
			usersMap.put("alu1", "23456387R");
			usersMap.put("alu2", "34567891F");
			usersMap.put("alu3", "93847525G");
			usersMap.put("alu4", "37264096W");
			context.setAttribute("users", usersMap);
		} else {
			usersMap = (HashMap<String, String>) context.getAttribute("users");
		}

		if (session.getAttribute("key") == null) {
			String login = httpRequest.getRemoteUser();

			if (login != null && usersMap.containsKey(login)) {
				session.setAttribute("dni", usersMap.get(login));
				session.setAttribute("password", "123456");

				// Set attributes to send to LoginServlet
				httpRequest.setAttribute("dni", session.getAttribute("dni"));
				httpRequest.setAttribute("password", session.getAttribute("password"));

				// Send dni and password to LoginServlet
				dispatcher = httpRequest.getRequestDispatcher("/login");
				dispatcher.forward(httpRequest, httpResponse);

				// Get key from LoginServlet
				String key = (String) httpRequest.getAttribute("key");

				if (key != null) {
					// Set key as session attribute
					session.setAttribute("key", key);
					Cookie cookie = new Cookie("JSESSIONID", key);
					// Set max age of cookie to 30 mins
					cookie.setMaxAge(30 * 60);
					httpResponse.addCookie(cookie);
				} else {
					error(httpResponse, "Invalid DNI or password");
				}
			} else {
				error(httpResponse, "BASIC authentication failed");
			}
		} else {
			String requestedPage = httpRequest.getRequestURI();
			if (requestedPage.endsWith("/login.html")) {
				// Redirect the user to the home page if he's already logged in
				httpResponse.sendRedirect("/home.html");
			}
		}

		chain.doFilter(request, response);
	}

	private void error(HttpServletResponse response, String message) throws IOException {
		if (!response.isCommitted()) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
			response.sendRedirect("/login.html");
		}
	}

}
