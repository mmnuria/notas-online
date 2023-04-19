
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Login extends HttpServlet {

    // what path to put here ?
    // TODO: make it a global variable
    private static final String CENTRO_EDUCATIVO_URL = "http://dew-bpopa-2223.dsicv.upv.es:9090/CentroEducativo";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the dni and password from the request parameters
        String dni = request.getParameter("dni");
        String password = request.getParameter("password");

        // Create a JSON payload with the dni and password
        String payload = String.format("{\"dni\": \"%s\", \"password\": \"%s\"}", dni, password);

        // Create an HTTP connection to the CentroEducativo API
        URL url = new URL(CENTRO_EDUCATIVO_URL + "/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Write the JSON payload to the connection output stream
        connection.getOutputStream().write(payload.getBytes());

        // Read the response from the CentroEducativo API
        int statusCode = connection.getResponseCode();
        if (statusCode == HttpURLConnection.HTTP_OK) {
            // Login was successful
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String key = in.readLine();
            in.close();
            Cookie cookie = new Cookie("JSESSIONID", key);
            response.addCookie(cookie);
            HttpSession session = request.getSession();
            session.setAttribute("key", key);
            // setting session to expire in 30 mins for added security
            session.setMaxInactiveInterval(30 * 60);
            response.setStatus(HttpServletResponse.SC_OK);
            // response.sendRedirect("/home.html");
        } else {
            // Login failed
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
            PrintWriter out = response.getWriter();
            out.println("<font color=red>Invalid DNI or password</font>");
            rd.include(request, response);
        }

        // Close the connection
        connection.disconnect();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
