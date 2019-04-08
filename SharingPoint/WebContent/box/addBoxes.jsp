<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="de">
<head>

    <title> CLZ Boxen mieten</title>
        <link rel="stylesheet" type="text/css" href="stylesheets/addBoxen.css">
	<div class="background">
			<img src="images/Clausthal-Zellerfeld.jpg" class="background">
	</div>

	<div class="headline"><h1> SharingPoint Clausthal, neue Box mieten</h1></div>

    <div id="sidenav" class="sidenav">
		<div class="toggle-btn" onclick="toggleSidebar()">
			<img src="images/avatar.png" class= "avatar">
		</div>
		<a href="index.html" class="logOut"> Ausloggen</a>
		<!--TODO: Tatsächlich ausloggen, also session zerstören etc-->
		<button onClick="location.href='/SharingPoint/changePin'" id="codeGen"> Code �ndern</button>
	</div>
	
</head>
<body>
	<div class="boxliste" id="boxlist">
		<div class="headl"> <h2> Eine Box ausw�hlen</h2></div>
    <!-- 
    <div class="main-container">
   		<table class="boxlist">
			<thead>
				<tr id="header"> 
					<th>Standort</th>
					<th>Verf�gbar</th>
					<th>Mieten</th>
				</tr>
			</thead>
   
   
    <c:forEach var="box" items ="${requestScope.FreeBoxList }">
					
					--EINTRAGEN--
					
	</c:forEach>
    </div>	 
    
    -->
    
    	<!-- Hardgecoded -->
    <div class="main-container">
   		 <table class="boxlist">
			<thead>
				<tr id="header"> 
					<th>Standort</th>
					<th>Verf�gbar</th>
					<th>Mieten</th>
				</tr>
			</thead>
		<tbody id="table" class="tablec">
		<c:forEach var="box" items ="${requestScope.FreeBoxList }">
			<tr>
				<td>${box[0]}</td>
				<td>${box[1]}</td>
				<td>${box[2]}</td>	
			</tr>
		</c:forEach>
		</tbody>
    </div>
    
    
    <script>
        function toggleSidebar(){
			document.getElementById("sidenav").classList.toggle('active');
		}

        //Jetzt als Funktion, damit die Seite aktualisiert werden kann wenn eine Box gemietet wurde
    </script>
</body>
</html>