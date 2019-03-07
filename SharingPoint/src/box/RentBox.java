package box;

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
 * Servlet implementation class RentBox
 */
@WebServlet("/rent")
public class RentBox extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RentBox() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		BoxController boxController = new BoxController();
		Box[] boxes = boxController.freeBoxes();
		List<Integer> areaIdList = new ArrayList<>();
		List<String> locationList = new ArrayList<>();
		List<Integer> quantityList = new ArrayList<>();
		List<String> buttonCode = new ArrayList<>();
		for(int i=0;i<boxes.length;i++) {
			if(areaIdList.contains(boxes[i].getAreaId())) {
				int index = areaIdList.indexOf(boxes[i].getAreaId());
				quantityList.set(index,quantityList.get(index)+1);
			}else {
				areaIdList.add(boxes[i].getAreaId());
				locationList.add(boxes[i].getLocation());
				quantityList.add(1);
				String buttonCodeStr = "<form method=\"POST\" action=\"http://localhost:8080/SharingPoint/rentBox\">"
						+ "<input type=\"hidden\" name=\"areaId\" value=\""+ boxes[i].getAreaId() +"\">"
						+ "<input type=\"submit\" value=\"Mieten\" />"
						+ "</form>";
				buttonCode.add(buttonCodeStr);
			}
		}
		String[][] boxInfo = new String[locationList.size()][3];
		for(int i=0; i<locationList.size();i++) {
			boxInfo[i][0] = locationList.get(i);
			boxInfo[i][1] = String.valueOf(quantityList.get(i));
			boxInfo[i][2] = buttonCode.get(i);
		}
		request.setAttribute("FreeBoxList", boxInfo);
		request.getRequestDispatcher("/box/addBoxes.jsp").forward(request,response);
	}


}
