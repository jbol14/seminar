package box;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ChangePrivateKey
 */
@WebServlet("/changeKey")
public class ChangePrivateKey extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangePrivateKey() {
        super();
        // TODO Auto-generated constructor stub
    }
    // Redirect to the jsp
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	if(request.getAttribute("wrongPass") != null) {
    		request.setAttribute("wrongPass", "Passwort falsch");
    	}
		request.getRequestDispatcher("/box/changePassword.jsp").forward(request,response);
    }
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Session Timeout check is on next Page
		request.getRequestDispatcher("/box/changePassword.jsp").forward(request,response);
	}


}
