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

import model.User;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import java.util.HashMap;
import org.json.JSONArray;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		ServletContext context = httpRequest.getServletContext();
		HttpSession session = httpRequest.getSession();
		RequestDispatcher dispatcher = null;

		HashMap<String, User> usersMap;

		if (context.getAttribute("users") == null) {
			usersMap = new HashMap<String, User>();
			httpRequest.setAttribute("dni", "111111111");
			httpRequest.setAttribute("password", "654321");
			// Send admin dni and password to LoginServlet
			dispatcher = httpRequest.getRequestDispatcher("/login");
			dispatcher.forward(httpRequest, httpResponse);

			// forward request with admin key to StudentServlet and TeacherServlet
			dispatcher = httpRequest.getRequestDispatcher("/students");
			dispatcher.forward(httpRequest, httpResponse);
			dispatcher = httpRequest.getRequestDispatcher("/teachers");
			dispatcher.forward(httpRequest, httpResponse);

			// Get students and teachers from request attributes
			String students = (String) httpRequest.getAttribute("students");
			String teachers = (String) httpRequest.getAttribute("teachers");

			// Parse students and teachers as JSONArray
			JSONArray studentsJSON = new JSONArray(students);
			JSONArray teachersJSON = new JSONArray(teachers);

			// Add students to usersMap, key will be "alu" + i
			for (int i = 0; i < studentsJSON.length(); i++) {
				String dni = studentsJSON.getJSONObject(i).getString("dni");
				usersMap.put("alu" + i, new User("alu" + i, dni));
			}

			// Add teachers to usersMap, key will be "pro" + i
			for (int i = 0; i < teachersJSON.length(); i++) {
				String dni = teachersJSON.getJSONObject(i).getString("dni");
				usersMap.put("pro" + i, new User("pro" + i, dni));
			}

			context.setAttribute("users", usersMap);
		} else {
			usersMap = (HashMap<String, User>) context.getAttribute("users");
		}

		if (session.getAttribute("key") == null) {
			String login = httpRequest.getRemoteUser();

			if (login != null) {
				session.setAttribute("dni", usersMap.get(login).getDni());
				session.setAttribute("password", usersMap.get(login).getPassword());
				httpRequest.setAttribute("dni", usersMap.get(login).getDni());
				httpRequest.setAttribute("password", usersMap.get(login).getPassword());

				// Send dni and password to LoginServlet
				dispatcher = httpRequest.getRequestDispatcher("/login");
				dispatcher.forward(httpRequest, httpResponse);

				// Get key from LoginServlet
				String key = (String) httpRequest.getAttribute("key");
				if (key != null) {
					session.setAttribute("key", key);
					Cookie cookie = new Cookie("JSESSIONID", key);
					// Set max age of cookie to 30 mins
					cookie.setMaxAge(30 * 60);
					httpResponse.addCookie(cookie);
				} else {
					error(httpResponse, "Your credentials are not valid");
				}
			} else {
				error(httpResponse, "Unauthorized user");
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
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
		if (!response.isCommitted()) {
			response.sendRedirect("/login.html");
		}
	}

}
