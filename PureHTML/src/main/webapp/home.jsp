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
        <c:choose>        
            <c:when test="${not (copy != null && copy)}">
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
            </c:when>
            <c:otherwise>
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
            </c:otherwise>
        </c:choose>

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

