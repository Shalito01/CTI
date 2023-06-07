<%@page contentType="text/html" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <title>
        Copy
    </title>
    <body>
        <h1>Catalogo</h1>
        <form action="copy" method="POST">
            <input type="hidden" value="${old_id}" name="old_id"/>
            <button class="nodoLink" type="submit"><p>&gt;&gt; Copia Qui</p></button>
            <input type="hidden" value="" name="new_id" />
        </form>
        <c:forEach var="nodo" items="${catalog_tree}">
            <c:choose>
            <c:when test="${nodo.isSelected()}">
                <div class="nodoView selected" id="${nodo.getId()}">
                    <form action="copy" method="GET">
                        <p>${nodo.toString()}</p>
                        <input type="hidden" name="old_id" value="${nodo.getId()}" />
                        <button class="nodoLink" type="submit"><p>&gt;&gt; Copia</p></button>
                    </form>
                </div>
            </c:when>
            <c:otherwise>
                <div class="nodoView" id="${nodo.getId()}">
                    <form action="copy" method="POST">
                        <p>${nodo.toString()}</p>
                        <input type="hidden" value="${old_id}" name="old_id"/>
                        <input type="hidden" value="${nodo.getId()}" name="new_id" />
                        <button class="nodoLink" type="submit"><p>&gt;&gt; Copia Qui</p></button>
                    </form>
                </div>
            </c:otherwise>
            </c:choose>
        </c:forEach>
    </body>
</html>

