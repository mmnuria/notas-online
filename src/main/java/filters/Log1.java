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
import javax.servlet.http.HttpServletRequest;

public class Log1 implements Filter {

    private static final String DEFAULT_LOG_FILE_PATH = "/var/log/notas-online/access.log";

    private PrintWriter logWriter;

    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code goes here

        // Open log file for writing
        try {
            logWriter = new PrintWriter(new FileWriter(DEFAULT_LOG_FILE_PATH, true));
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
        String uri = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // Log the entry
        String logEntry = currentDate + " " + clientInfo + " " + method + " " + uri + " " + formData;
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
