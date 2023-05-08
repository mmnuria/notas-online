package filters;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Log2
 */
@WebServlet("/Log2")
public class Log2 extends HttpServlet {

    private static final String LOG_FILE_PATH_PARAM = "log-file-path";
    private static final String DEFAULT_LOG_FILE_PATH = "/var/log/notas-online/access.log";

    private PrintWriter logWriter;
    private String logFilePath;

    public void init(ServletConfig servletConfig) throws ServletException {
        // Initialization code goes here

        // Get log file path from web.xml configuration
        logFilePath = servletConfig.getInitParameter(LOG_FILE_PATH_PARAM);
        if (logFilePath == null || logFilePath.trim().isEmpty()) {
            logFilePath = DEFAULT_LOG_FILE_PATH;
        }

        // Open log file for writing
        try {
            logWriter = new PrintWriter(new FileWriter(logFilePath, true));
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

    public void destroy() {
        // Cleanup code goes here

        // Close log file
        if (logWriter != null) {
            logWriter.close();
        }
    }
}
