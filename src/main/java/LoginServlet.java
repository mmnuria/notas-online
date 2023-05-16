
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import config.DatabaseConfig;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Get the dni and password from the request parameters
		String dni = request.getParameter("dni");
		String password = request.getParameter("password");
		if (dni == null || password == null) {
			dni = request.getAttribute("dni").toString();
			password = request.getAttribute("password").toString();
		}

		try {
			// Prepare the request parameters
			String url = DatabaseConfig.CENTRO_EDUCATIVO_URL + "/login";
			String payload = "{ \"dni\": \"" + dni + "\", \"password\": \"" + password + "\"}";

			// Make the curl request
			URL urlObj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("accept", "text/plain");
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

				// Do something with the response
				String key = responseContent.toString();
				request.setAttribute("key", key);
				// ...
			} else {
				// ...
			}
			response.setStatus(statusCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
