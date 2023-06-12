<%@page contentType="text/html" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
    <title>
        Copy
    </title>
        <link rel="stylesheet" href="css/home.css">
        <meta charset="UTF-8">
    </head>
    <body>
        <div class="header">
            <h1 id="title">TIW - Catalogazione Immagini</h1>
            <a href="/logout">
                <button id="logout-btn" type="submit">Logout</button>
        </a>
        </div>
        <div id="tree-wrapper" style="bottom: 1vh;">
        <form class="node-content" action="copy" method="POST">
            <input type="hidden" value="${old_id}" name="old_id"/>
            <button class="nodoLink" type="submit">&gt;&gt;Root - Copia Qui</button>
            <input type="hidden" value="" name="new_id" />
        </form>
        <c:forEach var="nodo" items="${catalog_tree}">
            <c:choose>
            <c:when test="${nodo.isSelected()}">
                <div class="nodoView selected" id="${nodo.getId()}">
                    <p>${nodo.toString()}</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="nodoView" id="${nodo.getId()}">
                    <form class="node-content" action="copy" method="POST">
                        <p>${nodo.toString()}</p>
                        <input type="hidden" value="${old_id}" name="old_id"/>
                        <input type="hidden" value="${nodo.getId()}" name="new_id" />
                        <button class="nodoLink" type="submit">&gt;&gt; Copia Qui</button>
                    </form>
                </div>
            </c:otherwise>
            </c:choose>
        </c:forEach>
        </div>
    </body>
</html>

