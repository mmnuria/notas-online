package filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import config.DatabaseConfig;

import javax.servlet.http.Cookie;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import java.util.HashMap;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		ServletContext context = httpRequest.getServletContext();
		HttpSession session = httpRequest.getSession();
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

				try {
					// Prepare the request parameters
					String url = DatabaseConfig.CENTRO_EDUCATIVO_URL + "/login";
					String payload = "{ \"dni\": \"" + session.getAttribute("dni") + "\", \"password\": \"" + session.getAttribute("password") + "\"}";

					// Make the curl request
					URL urlObj = new URL(url);
					HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Accept", "text/plain");
					connection.setRequestProperty("Content-Type", "application/json");
					connection.setDoOutput(true);

					// Write the request payload
					DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
					outputStream.writeBytes(payload);
					outputStream.flush();
					outputStream.close();

					// Get the response status code
					int statusCode = connection.getResponseCode();

					// Read the response
					if (statusCode == 200) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String line;
						StringBuilder responseContent = new StringBuilder();
						while ((line = reader.readLine()) != null) {
							responseContent.append(line);
						}
						reader.close();
						connection.disconnect();

						// Set key as session attribute
						session.setAttribute("key", responseContent.toString());
						Cookie cookie = new Cookie("JSESSIONID", responseContent.toString());
						// Set max age of cookie to 30 mins
						cookie.setMaxAge(30 * 60);
						httpResponse.addCookie(cookie);
					} else {
						error(httpResponse, "Invalid DNI or password");
					}
					httpResponse.setStatus(statusCode);
				} catch (Exception e) {
					e.printStackTrace();
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
