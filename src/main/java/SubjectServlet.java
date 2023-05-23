
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import config.DatabaseConfig;
// import org.json.JSONArray;


@WebServlet("/Subject")
public class SubjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String dni = (String) request.getSession().getAttribute("dni");
		String key = (String) request.getSession().getAttribute("key");

		try {
			String url = null;
			// Prepare the request parameters
			if (request.isUserInRole("rolpro")) {
				url = DatabaseConfig.CENTRO_EDUCATIVO_URL + "/profesores/" + dni + "/asignaturas?key=" + key;
			} else if (request.isUserInRole("rolalu")) {
				url = DatabaseConfig.CENTRO_EDUCATIVO_URL + "/alumnos/" + dni + "/asignaturas?key=" + key;
			}

			// Make the curl request
			URL urlObj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "text/plain");

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

				response.setContentType("application/json");
				response.getWriter().write(responseContent.toString());

			} else {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Data authentication failed");
			}
			response.setStatus(statusCode);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error establishing connection to database");
		}
	}

}
