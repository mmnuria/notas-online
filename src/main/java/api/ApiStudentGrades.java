package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import config.DatabaseConfig;

/**
 * Servlet implementation class ApiStudentGrades
 */
@WebServlet("/API/Student/Grades")
public class ApiStudentGrades extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.isUserInRole("rolpro")) {
			HttpSession session = request.getSession();
			String dni = request.getParameter("dni");
			String acronimo = request.getParameter("acronimo");
			String key = (String) session.getAttribute("key");
			String cookie = (String) session.getAttribute("cookie");

			try {
				String url = null;
				// Prepare the request parameters
				url = DatabaseConfig.CENTRO_EDUCATIVO_URL + "/alumnos/" + dni + "/asignaturas/" + acronimo + "?key="
						+ key;

				// Read the request body
				BufferedReader reader = request.getReader();
				StringBuilder requestBody = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					requestBody.append(line);
				}
				reader.close();

				// Make the PUT request
				URL urlObj = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
				connection.setRequestMethod("PUT");
				connection.setRequestProperty("Accept", "text/plain");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("cookie", cookie);
				connection.setDoOutput(true);

				// Write the request body
				try (OutputStream outputStream = connection.getOutputStream()) {
					outputStream.write(requestBody.toString().getBytes());
					outputStream.flush();
				}

				// Get the response status code
				int statusCode = connection.getResponseCode();

				// Read the response
				if (statusCode == HttpServletResponse.SC_OK) {
					// Successfully updated grade for the student
					response.setStatus(statusCode);
				} else {
					// Error updating grade for the student
					response.sendError(statusCode, "Error updating grade");
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Error establishing connection to database");
			}
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Action unauthorized.");
		}
	}

}