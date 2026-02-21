<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions" %>

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home | Traqqr</title>
    <link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/dist.css" />
</head>

<body>
<main class="container p-4 mx-auto">
    <article class="prose max-w-none">
        <h1>Home</h1>

        <p>Welcome to <a href="https://mthmulders.github.io/traqqr/" target="_blank">Traqqr</a>!</p>

        <p>
            Note that this application is still a work in progress, so check back frequently to stay current.
            Or star the repository on <a href="https://github.com/mthmulders/traqqr/" target="_blank">GitHub</a> so you can follow along with development.
        </p>

        <p>
            To get started, please <a href="${pageContext.servletContext.contextPath}/app/secure/dashboard">log in</a>.
        </p>
    </article>
</main>
<footer class="container mx-auto p-4 border-dotted border-t-4 border-[oklch(var(--a))]">
</footer>
<script defer src="${pageContext.servletContext.contextPath}/webjars/htmx.org/${htmx.version}/dist/htmx.min.js"></script>
</body>
</html>