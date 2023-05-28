package student;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Certificado
 */
@WebServlet("/Student/Certificado")
public class Certificado extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		{
			if(request.isUserInRole("rolalu")) 
			{
				RequestDispatcher rd = request.getRequestDispatcher("../certificado.html");
		        rd.include(request, response);
			}
			else 
			{
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Action unauthorized.");
			}
		}
	}

}
