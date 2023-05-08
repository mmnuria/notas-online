package filters;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Log1
 */
@WebServlet("/Log1")
public class Log1 extends HttpServlet {

    private static final String DEFAULT_LOG_FILE_PATH = "/var/log/notas-online/access.log";

    private PrintWriter logWriter;

    public void init() throws ServletException {
        // Initialization code goes here

        // Open log file for writing
        try {
            logWriter = new PrintWriter(new FileWriter(DEFAULT_LOG_FILE_PATH, true));
        } catch (IOException e) {
            throw new ServletException("Error opening log file", e);
        }
    }

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
        String logEntry = currentDate + " " + clientInfo + " " + pathInfo + " " + method;
        System.out.println(logEntry);
        logWriter.println(logEntry);
        logWriter.flush();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public void destroy() {
        // Cleanup code goes here

        // Close log file
        if (logWriter != null) {
            logWriter.close();
        }
    }
}
