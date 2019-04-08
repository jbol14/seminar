<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="de">
<head>
	<title> CLZ Boxen Übersicht</title>
    <link rel="stylesheet" type="text/css" href="stylesheets/stylebox.css">
    
    <div class="background">
			<img src="images/Clausthal-Zellerfeld.jpg" class="background">
	</div>

	<div class="headline"><h1> SharingPoint Clausthal, Meine Boxen</h1></div>

	<div id="sidenav" Class="sidenav">
		<div class="toggle-btn" onclick="toggleSidebar()">
			<img src="images/avatar.png" class= "avatar">
		</div>
		
		<a href="logout" class="logOut"> Ausloggen</a>
			
		<button onClick="location.href='/SharingPoint/changePin'" id="codeGen" > Code ändern</button>
	</div>
	
	
</head>
<body>
	
	

	<!--Use Button instead of Form-->
	<form action="http://127.0.0.1:8080/SharingPoint/rent">
		<div class="newBox">
			<input type="submit" value="Neue Box mieten">
		</div>
	</form>
	<div>
	</div>
	<div class="boxliste">
		<table class="boxlist">
			<thead>
			<tr id="header"> 
				<th>Standort</th>
				<th>Nummer</th>
				<th>Gemietet bis</th>
				<th>GastKey </th>
			</tr>
			</thead>
			 
			<tbody id="table" class="tablec">
				<c:forEach var="box" items ="${requestScope.UserBoxList }">
					<tr>
						<td>${box[0]}</td>
						<td>${box[1]}</td>
						<td>${box[2]}</td>
						<td> 	<form method="POST" action="http://localhost:8080/SharingPoint/"?>	
  								<input type="submit" value="Anzeigen?" class="tableBtn"/>
								</form> </td>
					</tr>
				</c:forEach>
			</tbody> 
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