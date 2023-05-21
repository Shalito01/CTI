<%@ page language="java" contentType="text/html" %>
<html>
<head>
    <title>Home</title>
</head>
<body>
    <%
    //allow access only if session exists
        if(session.getAttribute("user") == null){
            response.sendRedirect("login.html");
        }
    %>

<h1> Catalogazione di Immagini </h1>

</body>
</html>
