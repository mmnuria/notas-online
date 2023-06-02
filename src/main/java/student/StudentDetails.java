package student;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class StudentDetails
 */
@WebServlet("/Student/Details")
public class StudentDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		String dniReq = request.getParameter("dni");
		String dniSession = (String) session.getAttribute("dni");

		if (request.isUserInRole("rolalu") && dniReq == dniSession) {
			RequestDispatcher rd = request.getRequestDispatcher("../student_details.html");
			rd.include(request, response);
		} else if(request.isUserInRole("rolpro") && dniReq != null) {
			RequestDispatcher rd = request.getRequestDispatcher("../student_details.html");
			rd.include(request, response);
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Action unauthorized.");
		}
	}
}
