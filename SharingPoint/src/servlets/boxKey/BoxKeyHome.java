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
 * Servlet implementation class BoxKeyHome
 */
@WebServlet("/boxKeys")
public class BoxKeyHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BoxKeyHome() {
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
    	
    	//get location, id , leased_Until from user boxes
    	BoxController boxController = new BoxController();
    	Box box = boxController.getSingleBox(id,areaId,plz);
    	
    	if(box == null) {
    		request.getRequestDispatcher("/box/boxKey.jsp").forward(request,response);	//Evtl besserer Error
    	}
    	
    	String[] boxKey = box.getBoxKey();
    	String[][] boxKeyList = new String[boxKey.length][3];
    	
    	for(int i = 0 ; i< boxKey.length; i++) {
    		boxKeyList[i][0] = Integer.toString(i+1);
    		boxKeyList[i][1] = boxKey[i];
    		boxKeyList[i][2] = "<form method=\"POST\" action=\"http://localhost:8080/SharingPoint/boxKeydeactivate\">"
					+ "<input type=\"hidden\" name=\"areaId\" value=\""+ box.getAreaId() +"\">"
					+ "<input type=\"hidden\" name=\"id\" value=\""+ box.getId() +"\">"
					+ "<input type=\"hidden\" name=\"plz\" value=\""+ box.getPlz() +"\">"
					+ "<input type=\"hidden\" name=\"keyIndex\" value=\""+ i +"\">"
					+ "<input type=\"submit\" value=\"Deaktivieren\" class=\"tableBtn\"/>"
					+ "</form>";
    		
    	}
    	request.setAttribute("BoxKeyList", boxKeyList);	//Sets attribute for the jsp page. Contains 3 elements per row
  
    	String button = "<form method=\"POST\" action=\"http://localhost:8080/SharingPoint/boxKeyNew\">"
				+ "<input type=\"hidden\" name=\"areaId\" value=\""+ box.getAreaId() +"\">"
				+ "<input type=\"hidden\" name=\"id\" value=\""+ box.getId() +"\">"
				+ "<input type=\"hidden\" name=\"plz\" value=\""+ box.getPlz() +"\">"
				+ "<input type=\"submit\" value=\"Gastschlüssel neu generieren\" class=\"tableBtn\"/>"
				+ "</form>";
    	
    	request.setAttribute("Button", button);
    	
    	request.getRequestDispatcher("/box/boxKey.jsp").forward(request,response);	//Redirects to boxKeySite
	}

}
