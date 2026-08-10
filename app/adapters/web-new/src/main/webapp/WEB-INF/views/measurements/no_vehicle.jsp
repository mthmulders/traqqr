<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layouts" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:main title="Measurements">
    Select a vehicle to see its measurements.

    <br />

    <strong>Current implementation is a drop-down, but that would create the expectation of a life reload,
    which requires Htmx to load, etc.</strong>

    <br />

    <ul class="menu menu-vertical lg:menu-horizontal rounded-box">
        <c:forEach var="vehicle" items="${vehicles}">
            <li>
                <a href="${pageContext.request.contextPath}/app/secure/measurements/${vehicle.code()}">
                        ${vehicle.description()}</a>
            </li>
        </c:forEach>
    </ul>
</layout:main>