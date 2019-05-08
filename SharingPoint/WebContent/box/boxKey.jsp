<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="de">
<head>
	<title> CLZ Boxen Übersicht</title>
    <link rel="stylesheet" type="text/css" href="stylesheets/GuestKey.css">
    
    <div class="background">
			<img src="images/Clausthal-Zellerfeld.jpg" class="background">
	</div>

	<div class="headline"><h1> Gastkeys</h1></div>

	<div id="sidenav" Class="sidenav">
		<div class="toggle-btn" onclick="toggleSidebar()">
			<img src="images/avatar.png" class= "avatar">
		</div>
		
		<a href="logout" class="logOut"> Ausloggen</a>
		<!--TODO: TatsÃ¤chlich ausloggen, also session zerstÃ¶ren etc-->	
		<button onClick="location.href='/SharingPoint/changePin'" id="codeGen" > Code ändern</button>
	</div>
	
	
</head>
<body>
	
	

	<!--Use Button instead of Form-->
	<div class="newKey">
	<% 
   String name = (String)request.getAttribute("Button");
   out.println(name);
	%>
	</div>
	<div>
	
	</div>
</form>
	</div>
	<div class="boxliste">
		<table class="boxlist">
			<thead>
			<tr id="header"> 
				<th>Nr</th>
				<th>Key</th>
				<th>Deaktivieren</th>
			</tr>
			</thead>
			 
			<tbody id="table" class="tablec">
				<c:forEach var="box" items ="${requestScope.BoxKeyList }">
					<tr>
						<td>${box[0]}</td>
						<td>${box[1]}</td>
						<td>${box[2]}</td>
					</tr>
				</c:forEach>
			</tbody> 
			
			<!-- tbody hardgecoded 
			<tbody id="table" class="tablec">
					<tr>
						<td>1</td>
						<td>12345678</td>
						<td> 	<form method="POST" action="http://localhost:8080/SharingPoint/"?>								
  								<input type="submit" value="Löschen" class="tableBtn"/>
								</form> </td>
						
					</tr>
			</tbody>
			-->
		</table>
	</div>
<script>
	//Sidebar einblenden / ausblenden
	function toggleSidebar(){
		document.getElementById("sidenav").classList.toggle('active');
	}
</script>
</body>
</html>