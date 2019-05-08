package servlets.boxKey;

import java.io.IOException;
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
 * Servlet implementation class BoxKeyDeactivate
 */
@WebServlet("/boxKeydeactivate")
public class BoxKeyDeactivate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BoxKeyDeactivate() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
    	User user = (User) session.getAttribute("user");
    	
    	//Redirects to session timeout screen if no user in session
    	if(user == null) {
    		request.getRequestDispatcher("/box/SessionTimeout.jsp").forward(request,response);
    		return;
    	}
    	int areaId = Integer.parseInt(request.getParameter("areaId"));
    	int id = Integer.parseInt(request.getParameter("id"));
    	String plz = request.getParameter("plz");
    	int keyIndex = Integer.parseInt(request.getParameter("keyIndex"));
    	
    	
    	//get location, id , leased_Until from user boxes
    	BoxController boxController = new BoxController();
    	Box box = boxController.getSingleBox(id,areaId,plz);
    	
    	if(box == null) {
    		request.getRequestDispatcher("/box/boxKey.jsp").forward(request,response);	//Evtl besserer Error
    	}
    	
    	String[] boxKey = box.getBoxKey();
    	boxKey[keyIndex] = "[DEAKT]";
    	box.setBoxKey(boxKey);
    	System.out.println("Test");
    	boxController.updateBoxKeys(box);
    	
    	request.setAttribute("areaId", areaId);
    	request.setAttribute("id", id);
    	request.setAttribute("plz", plz);//Sets attribute for the jsp page. Contains 3 elements per row
    	
    	request.getRequestDispatcher("/boxKeys").forward(request,response);	//Redirects to boxKeySite
	}

}
