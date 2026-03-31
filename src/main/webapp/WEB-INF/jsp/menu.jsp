<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>Menu</title>
</head>
<body>
<h1>Menu</h1>
<p>login user: ${authentication.name}</p>
<p>user id: ${appOidcUser.userId}</p>

<c:if test="${appOidcUser != null}">
    <c:if test="${appOidcUser.userInfo != null}">
        <p>login id: ${appOidcUser.userInfo.loginId()}</p>
        <p>display name: ${appOidcUser.userInfo.displayName()}</p>
        <p>email: ${appOidcUser.userInfo.email()}</p>
    </c:if>
    <c:if test="${appOidcUser.executionPermissionSet != null}">
        <p>execution permissions:</p>
        <ul>
            <c:forEach items="${appOidcUser.executionPermissionSet.permissions()}" var="permission">
                <li>${permission.code()} - ${permission.name()}</li>
            </c:forEach>
        </ul>
    </c:if>
    <c:if test="${appOidcUser.referenceScopeSet != null}">
        <p>reference scopes:</p>
        <ul>
            <c:forEach items="${appOidcUser.referenceScopeSet.scopes()}" var="scope">
                <li>${scope.scopeType()} : ${scope.scopeValue()} - ${scope.description()}</li>
            </c:forEach>
        </ul>
    </c:if>
</c:if>

<form action="/logout" method="post">
    <button type="submit">logout</button>
</form>
</body>
</html>
