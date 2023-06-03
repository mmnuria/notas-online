package student;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Student/Images")
public class StudentImages extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String dni = req.getParameter("dni");
		String imagePath = getServletContext().getRealPath("/img/" + dni + ".pngb64");

		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");

		try {
			BufferedReader reader = new BufferedReader(new FileReader(imagePath));
			StringBuilder imageContent = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				imageContent.append(line);
			}

			reader.close();

			PrintWriter out = res.getWriter();
			out.print("{\"dni\": \"" + dni + "\", \"img\": \"" + imageContent.toString() + "\"}");
			out.close();
		} catch (IOException e) {
			res.getWriter().print("{\"error\": \"Image not found\"}");
		}
	}
}
