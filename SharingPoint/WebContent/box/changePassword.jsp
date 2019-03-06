<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Test Change Key</title>
</head>
<body>
<form method="POST" action="http://localhost:8080/SharingPoint/setKey">	
	<!-- TODO Submit soll an server /user get senden -->
	<h3>Change Universal-Key</h3>
		<label for="mail"><p>Key</p></label>
		<input type="text" name="pkey" placeholder="Neuen Key eintragen" required>
		<label for="psw"><p>Passwort</p></label>
		<input type="password" name="password" placeholder="Passwort eintragen" required>
		<font color="red"><h5>${empty wrongPass ? "" : wrongPass}</h5></font>
		<input type="submit" name="signin" value="ChangeKey">
</form>
</body>
</html>