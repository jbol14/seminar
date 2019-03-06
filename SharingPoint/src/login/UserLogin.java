package login;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import content.User;
import controller.UserController;

/**
 * Servlet implementation class UserLogin
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
		// Case: User does not exist in Database
		if(user == null) {
			PrintWriter writer = response.getWriter();
			writer.append("<!DOCTYPE html>\r\n")
					  .append("<html>\r\n")
					  .append("		<head>\r\n")
					  .append("			<title>CLZBoxen</title>\r\n")
					  .append("<meta http-equiv=\"refresh\" content=\"5; url=http://localhost:8080/SharingPoint/\">")
					  .append("		</head>\r\n")
					  .append("		<body>\r\n")
					  .append("<font color=\"red\"><h3>Email oder Passwort ist falsch!<h4></font>\r\n")
					  .append("<h6>Sie werden automatisch weitergeleitet falls dies nicht funktioniert bitte <a href=\"http://localhost:8080/SharingPoint/\"> hier </a> klicken<h6>\r\n")
					  .append("		</body>\r\n")
					  .append("</html>\r\n");
			return;
		// Case: User does exist, create SessionCookie and redirect
		}else {
			//safe user object
			HttpSession session = request.getSession();
			session.setAttribute("user", user);
			//create HTML response
			response.sendRedirect("/SharingPoint/home");
		}
	}

}
