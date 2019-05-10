<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Test Change Key</title>
<link rel="stylesheet" type="text/css" href="stylesheets/pw.css">
</head>
<body>
  
<div class="background">
			<img src="images/Clausthal-Zellerfeld.jpg" class="background">
</div>

<div class="Pwbox" id="changePw">
<form method="POST" action="http://localhost:8080/SharingPoint/setPin">	
	<!-- TODO Submit soll an server /user get senden -->
	<h3>Change Universal-Key</h3>
	<font color="red"> <h5> ${empty wrongPass ? "" : wrongPass} </h5></font>
		<label for="mail"><p>Key</p></label>
		<input type="number" name="pkey" placeholder="Neuen Key eintragen" required minlength="4" maxlength="10">
		<label for="psw"><p>Passwort</p></label>
		<input type="password" name="password" placeholder="Passwort eintragen" required>
		<input type="submit" name="signin" value="ChangeKey">
</form>
</div>
</body>
</html>
