package teacher;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Teacher
 */
@WebServlet("/Teacher/Subjects")
public class Teacher_Subjects extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		if(request.isUserInRole("rolpro")) 
		{
			RequestDispatcher rd = request.getRequestDispatcher("../teacher_subjects.html");
	        rd.include(request, response);
		}
		else 
		{
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Action unauthorized.");
		}
	}

}
