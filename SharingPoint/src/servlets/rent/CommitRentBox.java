package servlets.rent;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import content.User;
import controller.BoxController;

/**
 * Servlet CommitRentBox implements logic behind the commitment of renting a box
 */
@WebServlet("/rentBox")
public class CommitRentBox extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public CommitRentBox() {
        super();
    }

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
    	User user = (User) session.getAttribute("user");
    	//Go to Session Timeout screen if no User in Session
    	if(user == null) {
    		request.getRequestDispatcher("/box/SessionTimeout.jsp").forward(request,response);
    		return;
    	}
    	int areaId = Integer.parseInt(request.getParameter("areaId"));
    	BoxController boxController = new BoxController();
    	boolean check = boxController.rentBox(user, areaId);	//Rents Box
    	//Case: Box not available
    	if(!check) {
    		PrintWriter writer = response.getWriter();
			writer.append("<!DOCTYPE html>\r\n")
					  .append("<html>\r\n")
					  .append("		<head>\r\n")
					  .append("			<title>CLZBoxen</title>\r\n")
					  .append("<meta http-equiv=\"refresh\" content=\"5; url=http://localhost:8080/SharingPoint/rent\">")
					  .append("		</head>\r\n")
					  .append("		<body>\r\n")
					  .append("<font color=\"red\"><h3>Box nicht mehr verfügbar :/ <h4></font>\r\n")
					  .append("<h6>Sie werden automatisch weitergeleitet falls dies nicht funktioniert bitte <a href=\"http://localhost:8080/SharingPoint/\"> hier </a> klicken<h6>\r\n")
					  .append("		</body>\r\n")
					  .append("</html>\r\n");
			return;
    	}
    	//Case: Box available
    	PrintWriter writer = response.getWriter();
		writer.append("<!DOCTYPE html>\r\n")
				  .append("<html>\r\n")
				  .append("		<head>\r\n")
				  .append("			<title>CLZBoxen</title>\r\n")
				  .append("<meta http-equiv=\"refresh\" content=\"10; url=http://localhost:8080/SharingPoint/home\">")
				  .append("		</head>\r\n")
				  .append("		<body>\r\n")
				  .append("<font color=\"red\"><h3>Box erfolgreich gemietet<h4></font>\r\n")
				  .append("<h6>Sie werden automatisch weitergeleitet falls dies nicht funktioniert bitte <a href=\"http://localhost:8080/SharingPoint/home\"> hier </a> klicken<h6>\r\n")
				  .append("		</body>\r\n")
				  .append("</html>\r\n");
		return;
	}

}
