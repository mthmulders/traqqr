<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layouts" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:main title="Vehicles">
    <table class="table table-lg w-full">
        <thead>
        <tr>
            <th></th>
            <th>Code</th>
            <th>Description</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="vehicle" items="${vehicles}">
            <tr>
                <td class="actions">
                    <div class="grid">
                        <svg class="icon stroke-current" xmlns="http://www.w3.org/2000/svg">
                            <use xlink:href="#pencil"></use>
                        </svg>
                        <svg class="icon stroke-current" xmlns="http://www.w3.org/2000/svg">
                            <use xlink:href="#trash"></use>
                        </svg>
                    </div>
                </td>
                <td>${vehicle.code()}</td>
                <td>${vehicle.description()}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</layout:main>