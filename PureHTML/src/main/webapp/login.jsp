<%@page contentType="text/html" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Login</title>
    </head>
    <body>
        <form action="login" method="post">
            <div class="form-cell">
                <label>Username:</label>
                <input type="text" name="user">
            </div>
            <div class="form-cell">
                <label>Password: </label>
                <input type="password" name="pass">
            </div>
            <button class="login-btn" type="submit">Login</button>

            <% if(request.getAttribute("error") != null) { %>
                <div class="error">
                    ${error}
                </div>
            <% } %>
        </form>
    </body>
</html>
