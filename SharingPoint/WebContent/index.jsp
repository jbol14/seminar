<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="de">
<head>
	<title>Login CLZBoxen</title>
	<link rel="stylesheet" type="text/css" href="stylesheets/style.css">	
	<script src="javascript/javascript.js"></script>
</head>	
		
	<body>
		<div class="background">
				<img src="images/Clausthal-Zellerfeld.jpg" class="background">
		</div>
		<div class="welcome">
			<h1> Willkommen</h2>
			<h2> beim SharingPoint Clausthal</h2>
		</div>

	<div class="loginbox" id="sign-in">	
		<form method="POST" action="http://localhost:8080/SharingPoint/user">	
		
		<img src="images/avatar.png" class="avatar">
			<!-- TODO Submit soll an server /user get senden -->
			<h3>Hier einloggen</h3>
				<font color="red"> <h5> ${empty wrongMail ? "" : wrongMail} </h5></font>
				<label for="mail"><p>Email</p></label>
				<input type="text" name="email" placeholder="Email eintragen" required>
				<label for="psw"><p>Passwort</p></label>
				<input type="password" name="password" placeholder="Passwort eintragen" required>
				<input type="submit" name="signin" value="Login">
			<!--<a id="requestPW"> Passwort vergessen?</a><br> -->
				<a onclick="toggle(state)"> Haben sie keinen Account?</a>
		</form>
	</div>
	<!--TODO: Recieve Session ID when logged in, store in cookies-->
	<div class="loginbox hidden" id="sign-up">
		<form method="POST" action="http://localhost:8080/SharingPoint/register">
			<img src="images/avatar.png" class="avatar">
				<!-- TODO: Submit soll an server /user post senden-->
				<h3>Hier registrieren</h3>
					<p>Email</p>
					<input type="text" name="email" placeholder="Email eintragen" required>
					<font color="red"> <h5> ${empty mailError ? "" : mailError} </h5></font>
					<p>Passwort</p>
					<input type="password" name="password" placeholder="Passwort eintragen" required>
					<font color="red"> <h5> ${empty pwError ? "" : pwError} </h5></font>
					<p>Passwort bestätigen</p>
					<input type="password" name="passwordCheck" placeholder="Passwort bestätigen" required>
					<font color="red"> <h5> ${empty pwCheckError ? "" : pwCheckError} </h5></font>
					<p>Allgemeiner Box Pin</p>
					<input type="number" name="boxPin" placeholder="Box Pin eintragen" required min="0" maxlength="10">
					<input type="submit" name="register" value="Registrieren">
					<font color="red"> <h5> ${empty pinError ? "" : pinError} </h5></font>
					
					<a onclick="toggle(state)"> Haben sie bereits einen Account?</a>
		</form>
	</div>
	<script>

		
		//TODO: Add state object to Session
		let state = {
			toggled : false
		}
	</script>
</body>

</html>