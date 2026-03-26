<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layouts" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:main title="${title}">
    <form method="post" action="${pageContext.request.contextPath}/app/secure/vehicles/${(empty vehicle.code) ? 'new' : vehicle.code}" class="not-prose w-full">
        <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}" />
        <fieldset class="fieldset grid grid-cols-5 gap-4">
            <c:if test="${not empty vehicle.code}">
                <label for="vehicle.code" class="fieldset-legend col-span-2">Code</label>
                <div class="col-span-3">
                    <input id="vehicle.code" name="vehicle.code" type="text" class="input w-full" value="${vehicle.code}" readonly />
                    <p class="label">Unique assigned identifier of the vehicle.</p>
                </div>
            </c:if>

            <label for="vehicle.description" class="fieldset-legend col-span-2">Description</label>
            <div class="col-span-3">
                <input id="vehicle.description" name="vehicle.description" type="text" class="input w-full" value="${vehicle.description}" />
                <p class="label">Description of the vehicle.</p>
            </div>

            <label for="vehicle.netBatteryCapacity" class="fieldset-legend col-span-2">Net battery capacity</label>
            <div class="col-span-3">
                <input id="vehicle.netBatteryCapacity" name="vehicle.netBatteryCapacity" type="text" class="input w-full" value="${vehicle.netBatteryCapacity}" />
                <p class="label">Net battery capacity of the vehicle, in kWh.</p>
            </div>
        </fieldset>

        <div class="grid grid-cols-5 gap-4">
            <div class="col-span-2">&nbsp;</div>
            <div class="col-span-3">
                <button type="submit" class="btn btn-primary">Save</button>
            </div>
        </div>
    </form>
</layout:main>