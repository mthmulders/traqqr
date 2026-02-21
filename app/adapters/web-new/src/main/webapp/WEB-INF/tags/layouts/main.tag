<%@ tag body-content="scriptless" pageEncoding="UTF-8" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="title" required="true" type="java.lang.String" %>

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title} | Traqqr</title>
    <link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/dist.css" />
</head>

<body class="bg-neutral">
<nav>
<tags:navbar />
</nav>
<main class="container p-4 mx-auto">
    <article class="prose max-w-none">
        <h1>Home</h1>

        <jsp:doBody />
    </article>
</main>
<footer class="container mx-auto p-4 border-dotted border-t-4 border-[oklch(var(--a))]">
    Traqqr v${project.version} (revision <code>${git.commit.id.abbrev}</code>) is made with ❤️ + ☕ + <a href="https://dev.java/" target="_blank">Java ${systemInfo.javaVersion}</a> + <a href="https://jakarta.ee/" target="_blank">Jakarta EE 10</a>.
    Proudly running on ${systemInfo.javaRuntime}.
</footer>
<script defer src="${pageContext.servletContext.contextPath}/webjars/htmx.org/${htmx.version}/dist/htmx.min.js"></script>
</body>
</html>