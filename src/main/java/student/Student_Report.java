package student;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Student/Report")
public class Student_Report extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (request.isUserInRole("rolalu")) {
			RequestDispatcher rd = request.getRequestDispatcher("../student_report.html");
			rd.include(request, response);
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Action unauthorized.");
		}

	}
}
