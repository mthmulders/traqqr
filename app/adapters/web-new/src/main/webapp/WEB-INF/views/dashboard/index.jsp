<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layouts" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:main title="Dashboard">
    <p>Hi ${userInfoBean.username}!</p>

    <h2>Statistics</h2>

    <div class="stats shadow w-full">
        <div class="stat">
            <div class="stat-title">Measurements</div>
            <div class="stat-value">${numMeasurements}</div>
            <div class="stat-desc">Unique measurements are registered for your vehicle(s)</div>
        </div>
    </div>
</layout:main>