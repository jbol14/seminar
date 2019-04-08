package login;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import content.User;
import controller.UserController;

/**
 * Servlet UserRegister which handles the registration. Redirects to an error page if password are incorrect or email is already in use
 */
@WebServlet("/register")
public class UserRegister extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public UserRegister() {
        super();
    }


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String checkPassword = request.getParameter("passwordCheck");
		String pin = request.getParameter("boxPin");
		//Checks if both passwords are the same, if not redirect to error page
		if(!password.equals(checkPassword)) {
			PrintWriter writer = response.getWriter();
			writer.append("<!DOCTYPE html>\r\n")
					  .append("<html>\r\n")
					  .append("		<head>\r\n")
					  .append("			<title>CLZBoxen</title>\r\n")
					  .append("<meta http-equiv=\"refresh\" content=\"5; url=http://localhost:8080/SharingPoint/\">")
					  .append("		</head>\r\n")
					  .append("		<body>\r\n")
					  .append("<font color=\"red\"><h3>Passwörter stimmen nicht überein<h4></font>\r\n")
					  .append("<h6>Sie werden automatisch weitergeleitet falls dies nicht funktioniert bitte <a href=\"http://localhost:8080/SharingPoint/\"> hier </a> klicken<h6>\r\n")
					  .append("		</body>\r\n")
					  .append("</html>\r\n");
			return;
		}
		UserController userController = new UserController();
		
		boolean check = userController.RegisterUser(email, password,pin);
		//If a user with that Email is already in the system user gets an error
		if(!check) {
			PrintWriter writer = response.getWriter();
			writer.append("<!DOCTYPE html>\r\n")
					  .append("<html>\r\n")
					  .append("		<head>\r\n")
					  .append("			<title>CLZBoxen</title>\r\n")
					  .append("<meta http-equiv=\"refresh\" content=\"5; url=http://localhost:8080/SharingPoint/\">")
					  .append("		</head>\r\n")
					  .append("		<body>\r\n")
					  .append("<font color=\"red\"><h3>Email bereits vergeben<h4></font>\r\n")
					  .append("<h6>Sie werden automatisch weitergeleitet falls dies nicht funktioniert bitte <a href=\"http://localhost:8080/SharingPoint/\"> hier </a> klicken<h6>\r\n")
					  .append("		</body>\r\n")
					  .append("</html>\r\n");
			return;
		}
		User user = userController.checkUser(email, password);
		
		HttpSession session = request.getSession();
		session.setAttribute("user", user);			//Add user to session
		response.sendRedirect("/SharingPoint/home");
	}

}
