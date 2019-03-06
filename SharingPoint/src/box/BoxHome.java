package box;

import java.io.IOException;
import java.io.PrintWriter;
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
 * Servlet implementation class BoxHome
 */
@WebServlet("/home")
public class BoxHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BoxHome() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession();
    	User user = (User) session.getAttribute("user");
    	//Go to Session Timeout screen if no User in Session
    	if(user == null) {
    		request.getRequestDispatcher("/box/SessionTimeout.jsp").forward(request,response);
    		return;
    	}
    	//get Location, Id , leased_Until from User Boxes
    	BoxController boxController = new BoxController();
    	Box[] boxes = boxController.UserBoxes(user);
    	String[][] userBoxList = new String[boxes.length][3];
    	DateFormat dateFormat =  new SimpleDateFormat("dd-MM-yyyy");
    	for(int i=0; i < boxes.length; i++) {
    		userBoxList[i][0] = boxes[i].getLocation();
    		userBoxList[i][1] = String.valueOf(boxes[i].getId());
    		String date = dateFormat.format(boxes[i].getDate());
    		userBoxList[i][2] = date;
    	}
    	request.setAttribute("UserBoxList", userBoxList);
    	
    	request.getRequestDispatcher("/box/boxenHome.jsp").forward(request,response);
	}

}
