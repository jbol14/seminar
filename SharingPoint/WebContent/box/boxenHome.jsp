<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="de">
<head>
	<title> CLZ Boxen Übersicht</title>
    <link rel="stylesheet" type="text/css" href="stylesheets/stylebox.css">
</head>
<body>
	<div class="background">
			<img src="images/Clausthal-Zellerfeld.jpg" class="background">
	</div>

	<div class="headline"><h1> SharingPoint Clausthal, Meine Boxen</h1></div>

	<div id="sidenav" Class="sidenav">
		<div class="toggle-btn" onclick="toggleSidebar()">
			<img src="images/avatar.png" class= "avatar">
		</div>
		<a href="/index.html" class="logOut"> Ausloggen</a>
		<!--TODO: TatsÃ¤chlich ausloggen, also session zerstÃ¶ren etc-->
		<a href="/SharingPoint/changeKey" class="logOut"> Code ändern</a>
	</div>
	
	<div id="codeModal" class="codeModal">

		<div class="codeModal-content">
			<span class="close">&times;</span>
			<p> Hier passieren später magische Dinge....</p>
		</div>

	</div>

	<!--Use Button instead of Form-->
	<form action="http://127.0.0.1:8080/SharingPoint/rent">
		<div class="newBox">
			<input type="submit" value="Neue Box mieten">
		</div>
	</form>
	<div>
	Absolute Path is:<%= getServletContext().getRealPath("/") %>
	</div>
	<div class="boxliste">
		<table class="boxlist">
			<thead>
			<tr id="header"> 
				<th>Standort</th>
				<th>Nummer</th>
				<th>Gemietet bis</th>
			</tr>
			</thead>
			<tbody id="table" class="tablec">
				<c:forEach var="box" items ="${requestScope.UserBoxList }">
					<tr>
						<td>${box[0]}</td>
						<td>${box[1]}</td>
						<td>${box[2]}</td>
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