<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layouts" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:main title="Admin Zone">
    <h2 class="mt-8">Application information</h2>
    <dl>
        <dt>Application version</dt>
        <dd>${systemInfo.applicationVersion} (revision <code>${systemInfo.gitVersion}</code>)</dd>
        <dt>Java runtime</dt>
        <dd>${systemInfo.javaRuntime}</dd>
        <dt>Operating system</dt>
        <dd>${systemInfo.osInfo}</dd>
        <dt>Database</dt>
        <dd>${systemInfo.databaseInfo}</dd>
    </dl>
</layout:main>