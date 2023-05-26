package api;

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
import javax.servlet.http.HttpSession;

import config.DatabaseConfig;


@WebServlet("/API/Teacher/Subject")
public class API_Teacher_Subject extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		if(request.isUserInRole("rolpro")) 
		{
			HttpSession session = request.getSession();
			String dni = (String) session.getAttribute("dni");
			String key = (String) session.getAttribute("key");
			String cookie = (String) session.getAttribute("cookie");
			
			try {
				String url = null;
				// Prepare the request parameters
				url = DatabaseConfig.CENTRO_EDUCATIVO_URL + "/profesores/" + dni + "/asignaturas?key=" + key;
				
				//String cookie = response.getHeader("Set-Cookie");
				
				// Make the curl request
				URL urlObj = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Accept", "application/json");
				connection.setRequestProperty("cookie", cookie);

				// Get the response status code
				int statusCode = connection.getResponseCode();

				// Read the response
				if (statusCode == HttpServletResponse.SC_OK) {
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
		else 
		{
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Action unauthorized.");
		}
	}
}
