package servlets.userKey;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet representation of box/changePin.jsp . Can insert a error into the page.
 */
@WebServlet("/changePin")
public class ChangeUserPin extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public ChangeUserPin() {
        super();
    }
    // inserts a "wrong Password" error into the jsp page
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	if(request.getAttribute("wrongPass") != null) {
    		request.setAttribute("wrongPass", "Passwort falsch");
    	}
		request.getRequestDispatcher("/box/changePin.jsp").forward(request,response);
    }
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Session Timeout check is on next Page
		request.getRequestDispatcher("/box/changePin.jsp").forward(request,response);
	}


}
