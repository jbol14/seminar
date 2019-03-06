package box;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import content.User;
import controller.UserController;


@WebServlet("/setKey")
public class SetPrivateKey extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SetPrivateKey() {
        super();
    }
    //checks entered Password, if correct change PrivateKey
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String pKey = request.getParameter("pkey");
		String password = request.getParameter("password");
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		//Go to Session Timeout screen if no User in Session
		if(user == null) {
    		request.getRequestDispatcher("/box/SessionTimeout.jsp").forward(request,response);
    		return;
    	}
		UserController userController = new UserController();
		user = userController.checkUser(user.getEmail(), password); //can be optimized
		if(user == null) {
			request.setAttribute("wrongPass", true);
			RequestDispatcher rd = request.getRequestDispatcher("/changeKey");
			rd.forward(request,response);
			return;
		}
		
		//Here would be the Key encryption
		
		userController.changePrivateKey(pKey, user);
		
		//Update Data on Rasberry
		
		response.sendRedirect("/SharingPoint/home"); //To do: Change to successfully changed Key
		
	}

}
