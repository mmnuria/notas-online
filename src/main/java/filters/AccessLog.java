package filters;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

@WebServlet("/AccessLog")
public class AccessLog implements Filter {

    private static final String LOG_FILE_PATH_PARAM = "log-file-path";
    private static final String DEFAULT_LOG_FILE_PATH = "/var/log/notas-online/access.log";

    private PrintWriter logWriter;
    private String logFilePath;

    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code goes here

        // Get log file path from web.xml configuration
        logFilePath = filterConfig.getInitParameter(LOG_FILE_PATH_PARAM);
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

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Get relevant information
        String formData = httpRequest.getQueryString();
        String clientInfo = httpRequest.getRemoteUser() + " " + httpRequest.getRemoteAddr();
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String pathInfo = httpRequest.getPathInfo();
        // String uri = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // Log the entry
        String logEntry = currentDate + " " + clientInfo + " " + pathInfo + " " + method;
        System.out.println(logEntry);
        logWriter.println(logEntry);
        logWriter.flush();

        chain.doFilter(request, response);
    }

    public void destroy() {
        // Cleanup code goes here

        // Close log file
        if (logWriter != null) {
            logWriter.close();
        }
    }
}
