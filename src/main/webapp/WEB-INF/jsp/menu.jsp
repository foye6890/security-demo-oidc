<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>Menu</title>
</head>
<body>
<h1>Menu</h1>
<p>login user: ${authentication.name}</p>
<form action="/logout" method="post">
    <button type="submit">logout</button>
</form>
</body>
</html>
