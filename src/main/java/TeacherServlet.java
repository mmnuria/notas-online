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
@WebServlet("/teachers")
public class TeacherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // doGet(request, response);
        // This would be used to create a new teacher
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the key from the request parameters or request attributes
        String key = request.getParameter("key");

        try {
            // Prepare the request parameters and add key from request
            String url = DatabaseConfig.CENTRO_EDUCATIVO_URL + "/profesores" + "?key=" + key;

            // Make the curl request
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "application/json");

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

                request.setAttribute("teachers", responseContent.toString());
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