package filters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Log0
 */
@WebServlet("/Log0")
public class Log0 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get relevant information
        String formData = request.getQueryString();
        String clientInfo = request.getRemoteUser() + " " + request.getRemoteAddr();
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String pathInfo = request.getPathInfo();
        // String uri = request.getRequestURI();
        String method = request.getMethod();

        // Log the entry
        System.out.println(currentDate + " " + clientInfo + " " + pathInfo + " " + method);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
