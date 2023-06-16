<%@page contentType="text/html" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>ERROR</title>
        <link rel="stylesheet" href="css/error.css" />
		<meta http-equiv='refresh' content='15; URL=/home'>
        <meta charset="UTF-8">
    </head>
    <body style="display: grid; place-items: center;">
        <div class="header">
            <h1 id="title">TIW - Catalogazione Immagini</h1>
        </div>
		<div id="content-wrapper">
		<% if(request.getAttribute("error") != null) { %>
			<div id="error" class="content error">
				${error}
			</div>
		<% } %>

		<a href="/home">
			<button class="nodoLink">
				GO TO HOME
			</button>
		</a>
	</div>
    </body>
</html>

