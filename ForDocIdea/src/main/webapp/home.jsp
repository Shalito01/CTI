<%@page contentType="text/html" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <title>
        HOME
    </title>
    <body>
        <h1>Catalogo</h1>
        <c:forEach var="nodo" items="${catalog_tree}">
            <div class="nodoView" id="${nodo.getId()}">
                <form action="copy" method="GET">
                    <c:if test="${not empty nodo.getId()}">
                        <p>${nodo.toString()}</p>
                    </c:if>
                    <input type="hidden" name="old_id" value="${nodo.getId()}" />
                    <button class="nodoLink" type="submit"><p>&gt;&gt; Copia</p></button>
                </form>
            </div>
        </c:forEach>
    </body>
</html>

