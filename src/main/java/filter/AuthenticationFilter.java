package filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;

import model.User;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import java.util.HashMap;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) request;
        HttpServletResponse response = (HttpServletResponse) response;
        ServletContext context = request.getServletContext();
        HttpSession session = request.getSession();
        RequestDispatcher dispatcher = null;

        HashMap<String, User> usersMap;

        if (context.getAttribute("users") == null) {
            usersMap = new HashMap<String, User>();
            // How to populate the usersMap when not logged in without hardcoding it?
            usersMap.put("pro1", new User("pro1", "23456733H"));
            usersMap.put("pro2", new User("pro2", "10293756L"));
            usersMap.put("pro3", new User("pro3", "06374291A"));
            usersMap.put("pro4", new User("pro4", "65748923M"));
            usersMap.put("alu1", new User("alu1", "12345678W"));
            usersMap.put("alu2", new User("alu2", "23456387R"));
            usersMap.put("alu3", new User("alu3", "34567891F"));
            usersMap.put("alu4", new User("alu4", "93847525G"));
            usersMap.put("alu5", new User("alu5", "37264096W"));
            context.setAttribute("users", usersMap);
        } else {
            usersMap = (HashMap<String, User>) context.getAttribute("users");
        }

        if (session.getAttribute("key").equals(null)) {
            String login = request.getRemoteUser();

            if (!login.equals(null)) {
                session.setAttribute("dni", usersMap.get(login).getDni());
                session.setAttribute("password", usersMap.get(login).getPassword());
                request.setAttribute("dni", usersMap.get(login).getDni());
                request.setAttribute("password", usersMap.get(login).getPassword());
                // Send dni and password to LoginServlet
                dispatcher = request.getRequestDispatcher("/login");
                dispatcher.forward(request, response);

                // Get key from LoginServlet
                String key = (String) request.getAttribute("key");
                if (!key.equals(null)) {
                    session.setAttribute("key", key);
                    Cookie cookie = new Cookie("JSESSIONID", key);
                    // Set max age of cookie to 30 mins
                    cookie.setMaxAge(30 * 60);
                    response.addCookie(cookie);
                } else {
                    error(response, "User not found in database");
                }
            } else {
                error(response, "User not in tomcat-users.xml");
            }
        }

        String requestedPage = request.getRequestURI();
        if (requestedPage.endsWith("/login.html")) {
            // Redirect the user to the home page if he's already logged in
            response.sendRedirect("/home.html");
        }

        chain.doFilter(request, response);
    }

    private void error(HttpServletResponse response, String message)
            throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
        response.sendRedirect("/login.html");
    }

}
