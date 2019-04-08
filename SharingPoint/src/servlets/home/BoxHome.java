package servlets.home;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import content.Box;
import content.User;
import controller.BoxController;

/**
 * Servlet representation of the homescreen of a logged in User.
 * It fetches all the boxes, over the database, which belong to the user.
 * This data get send to the representing jsp. 
 */
@WebServlet("/home")
public class BoxHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    public BoxHome() {
        super();
    }
    //Get method of the page
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	HttpSession session = request.getSession();
    	User user = (User) session.getAttribute("user");
    	
    	//Redirects to session timeout screen if no user in session
    	if(user == null) {
    		request.getRequestDispatcher("/box/SessionTimeout.jsp").forward(request,response);
    		return;
    	}
    	//get location, id , leased_Until from user boxes
    	BoxController boxController = new BoxController();
    	Box[] boxes = boxController.UserBoxes(user);
    	String[][] userBoxList = new String[boxes.length][3];
    	DateFormat dateFormat =  new SimpleDateFormat("dd-MM-yyyy");
    	for(int i=0; i < boxes.length; i++) {				//fills Data
    		userBoxList[i][0] = boxes[i].getLocation();
    		userBoxList[i][1] = String.valueOf(boxes[i].getId());
    		String date = dateFormat.format(boxes[i].getDate());
    		userBoxList[i][2] = date;
    	}
    	request.setAttribute("UserBoxList", userBoxList);	//Sets attribute for the jsp page. Contains 3 elements per row
    	
    	request.getRequestDispatcher("/box/boxenHome.jsp").forward(request,response);	//Redirects to homescreenpage
	}

}
