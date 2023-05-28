package filter;

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

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Get relevant information
		String clientInfo = request.getRemoteUser() + " " + request.getRemoteAddr();
		String currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		String servletName = request.getServletPath();
		String method = request.getMethod();

		// Log the entry
		System.out.println(currentDate + " " + clientInfo + " " + servletName + " " + method);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
