<%@page contentType="text/html" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>HOME</title>
        <link rel="stylesheet" href="css/home.css" />
        <meta charset="UTF-8">
    </head>
    <body>
        <div class="header">
            <h1 id="title">TIW - Catalogazione Immagini</h1>
            <a href="/logout"><button id="logout-btn">Logout</button></a>
        </div>
        <div id="tree-wrapper">
            <% if(request.getAttribute("error") != null) { %>
                <div id="error" class="content error">
                    ${error}
                </div>
            <% } %>
        <c:forEach var="nodo" items="${catalog_tree}">
            <div class="nodoView" id="${nodo.getId()}">
                <form class="node-content" action="copy" method="GET">
                    <c:if test="${not empty nodo.getId()}">
                        <p>${nodo.toString()}</p>
                    </c:if>
                    <input type="hidden" name="old_id" value="${nodo.getId()}" />
                    <button class="nodoLink" type="submit">&gt;&gt; Copia</button>
                </form>
            </div>
        </c:forEach>

        </div>
        <div class="footer">
            <form id="insert-form" action="home" method="post" enctype="application/x-www-form-urlencoded">
                <input id="in-name" type="text" name="name" placeholder="Category Name" />
                <input id="in-pid" type="number" name="parentId" placeholder="Parent ID" />
                <button id="insert-btn" type="submit">Insert</button>
            </form>
        </div>
    </body>
</html>

