
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import config.DatabaseConfig;

public class Login extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the dni and password from the request parameters
        String dni = request.getParameter("dni");
        String password = request.getParameter("password");

        // Construct the curl command to make an HTTP POST request with two string
        // parameters
        String endpoint = DatabaseConfig.CENTRO_EDUCATIVO_URL + "/login";
        String command = "curl -X POST -d \"dni=" + dni + "&password=" + password + "\" " + endpoint;

        // Execute the curl command and capture the output and status code
        Process process = Runtime.getRuntime().exec(command);
        int statusCode = process.waitFor();

        if (statusCode == HttpServletResponse.SC_OK) {
            // Login was successful
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String key = reader.readLine();
            reader.close();
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
            response.getWriter().println("<font color=red>Invalid DNI or password</font>");
            rd.include(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
