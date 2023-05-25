package filter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import helper.FileSystem;

@WebFilter("/*")
public class AccessLogFilter implements Filter {

    private static final String LOG_FILE_PATH_PARAM = "log-file-path";

    private PrintWriter logWriter;
    private String logFilePath;
    private FileSystem fileSystem = new FileSystem();

    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code goes here

        // Get log file path from web.xml configuration
        logFilePath = filterConfig.getServletContext().getInitParameter(LOG_FILE_PATH_PARAM);
        Path path = Paths.get(logFilePath);
        String routeDirectory = path.getParent().toString();
        String nameFile = path.getFileName().toString();

        // Open log file for writing
        try {
            if (!fileSystem.exists(routeDirectory)) {
                fileSystem.createDirectory(routeDirectory);
            }
            fileSystem.createFile(routeDirectory, nameFile);
            logWriter = new PrintWriter(new FileWriter(logFilePath, true));
        } catch (IOException e) {
            throw new ServletException("Error opening log file", e);
        } catch (Exception e) {
            throw new ServletException("Error creating directory", e);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Get relevant information
        // String formData = httpRequest.getQueryString();
        String clientInfo = httpRequest.getRemoteUser() + " " + httpRequest.getRemoteAddr();
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String pathInfo = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        // String uri = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // Log the entry
        String logEntry = currentDate + " " + clientInfo + " " + pathInfo + " " + method;
        System.out.println(logEntry);
        if (logWriter != null) {
            logWriter.println(logEntry);
            logWriter.flush();
        }

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
