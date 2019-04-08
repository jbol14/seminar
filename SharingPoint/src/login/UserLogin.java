package login;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import content.User;
import controller.UserController;

/**
 * Servlet UserLogin handles the user login
 */
@WebServlet(urlPatterns= {"/user"})
public class UserLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public UserLogin() {
        super();
    }
    //Checks Email and Password. Creates the necessary Response
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		UserController userController = new UserController();
		User user = userController.checkUser(email, password);
		// Case: User does not exist in Database or password is wrong(both cases returned a null user)
		if(user == null) {
			request.setAttribute("wrongCred", "Email oder Passwort falsch");
			System.out.println("test");
			request.getRequestDispatcher("/index.jsp").forward(request,response);
			return;
		// Case: User does exist, create Session and redirect
		}else {
			HttpSession session = request.getSession();
			session.setAttribute("user", user);			//Add user to session
			response.sendRedirect("/SharingPoint/home");
		}
	}

}
