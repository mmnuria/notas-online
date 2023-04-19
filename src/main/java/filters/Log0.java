
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class Log0 implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code goes here
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
        System.out.println(currentDate + " " + clientInfo + " " + method + " " + uri + " " + formData);

        chain.doFilter(request, response);
    }

    public void destroy() {
        // Cleanup code goes here
    }
}
