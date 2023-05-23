package filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
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

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("AUTH FILTER");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();

		boolean isLoggedIn = (session != null && session.getAttribute("dni") != null);
		boolean isKeySet = (session.getAttribute("key") != null);

		if (!isLoggedIn && !isKeySet) {
			String login = httpRequest.getRemoteUser();

			if (login != null) {
				session.setAttribute("dni", login);
				session.setAttribute("password", "123456");

				try {
					// Prepare the request parameters
					String url = DatabaseConfig.CENTRO_EDUCATIVO_URL + "/login";
					String payload = "{ \"dni\": \"" + session.getAttribute("dni") + "\", \"password\": \""
							+ session.getAttribute("password") + "\"}";

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
						String key = responseContent.toString();
						System.out.println(key);
						session.setAttribute("key", key);
						Cookie cookie = new Cookie("JSESSIONID", key);
						// Set max age of cookie to 30 mins
						cookie.setMaxAge(30 * 60);
						httpResponse.addCookie(cookie);
					} else {
						httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Data authentication failed");
					}
					httpResponse.setStatus(statusCode);
				} catch (Exception e) {
					e.printStackTrace();
					httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"Error establishing connection to database");
				}
			} else {
				httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Basic authentication failed");
			}
		}

		// continues the filter chain
		// allows the request to reach the destination
		chain.doFilter(request, response);
	}
}
