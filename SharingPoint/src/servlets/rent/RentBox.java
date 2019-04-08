package servlets.rent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import content.Box;
import controller.BoxController;

/**
 * Servlet RentBox fetches boxes from database and passes them to the box/addBoxes.jsp
 */
@WebServlet("/rent")
public class RentBox extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    public RentBox() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BoxController boxController = new BoxController();
		Box[] boxes = boxController.freeBoxes();		//gets boxes without user
		List<Integer> areaIdList = new ArrayList<>();
		List<String> locationList = new ArrayList<>();
		List<Integer> quantityList = new ArrayList<>();
		List<String> buttonCode = new ArrayList<>();
		//Sort boxes to box clusters
		for(int i=0;i<boxes.length;i++) {
			if(areaIdList.contains(boxes[i].getAreaId())) {				//if box cluster found add more quantity
				int index = areaIdList.indexOf(boxes[i].getAreaId());
				quantityList.set(index,quantityList.get(index)+1);
			}else {														//if box cluster not yet in List, add
				areaIdList.add(boxes[i].getAreaId());
				locationList.add(boxes[i].getLocation());
				quantityList.add(1);
				String buttonCodeStr = "<form method=\"POST\" action=\"http://localhost:8080/SharingPoint/rentBox\">"
						+ "<input type=\"hidden\" name=\"areaId\" value=\""+ boxes[i].getAreaId() +"\">"
						+ "<input type=\"submit\" value=\"Mieten\" class=\"tableBtn\"/>"
						+ "</form>";
				buttonCode.add(buttonCodeStr);
			}
		}
		// creates String array for the jsp
		String[][] boxInfo = new String[locationList.size()][3];
		for(int i=0; i<locationList.size();i++) {
			boxInfo[i][0] = locationList.get(i);
			boxInfo[i][1] = String.valueOf(quantityList.get(i));
			boxInfo[i][2] = buttonCode.get(i);
		}
		request.setAttribute("FreeBoxList", boxInfo);									//set attribute for the jsp
		request.getRequestDispatcher("/box/addBoxes.jsp").forward(request,response);	//forwards to addBoxes.jsp
	}


}
