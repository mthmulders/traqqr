<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layouts" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<fmt:setLocale value="${user.locale}" />

<layout:main title="Measurements for ${vehicle.description()}">
    <table class="table table-md w-full not-prose">
        <thead>
        <tr>
            <th></th>
            <th>Registered at</th>
            <th>Measured at</th>
            <th>Odometer</th>
            <th>Battery SoC</th>
            <th>Location</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="measurement" items="${measurements}">
            <tr>
                <td class="actions">
                    <div class="grid">
                        <c:choose>
                            <c:when test="${measurement.source() == 'API'}">
                                <svg class="icon stroke-current" xmlns="http://www.w3.org/2000/svg">
                                    <use xlink:href="#cog-6-tooth"></use>
                                </svg>
                            </c:when>
                            <c:otherwise>
                                <svg class="icon stroke-current" xmlns="http://www.w3.org/2000/svg">
                                    <use xlink:href="#user"></use>
                                </svg>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </td>
                <td>
                        ${instantFormatter.format(measurement.registrationTimestamp())}
                </td>
                <td>
                        ${instantFormatter.format(measurement.measurementTimestamp())}
                </td>
                <td>
                    <fmt:formatNumber type="number"
                                      value="${measurement.odometer()}" />
                </td>
                <td>
                    <fmt:formatNumber type="percent"
                                      value="${measurement.battery().soc()/100}" />
                </td>
                <td>
                    <c:choose>
                        <c:when test="${empty measurement.location().description()}">
                            <a href="https://www.google.com/maps/search/?api=1&amp;query=${gpsCoordinateFormatter.format(measurement.location())}"
                               target="_blank"
                               class="link">
                                ${gpsCoordinateFormatter.format(measurement.location())}
                            </a>
                        </c:when>
                        <c:otherwise>
                            ${measurement.location().description()}
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot>
            <c:if test="${maximumPageNumber > 0}">
                <tr class="hover:stroke-none">
                    <td colspan="6">
                        <div class="join grid grid-cols-5">
                            <%--first--%>
                            <a href="${pageContext.request.contextPath}/app/secure/measurements?vehicle=${vehicle.code()}&page=0"
                               role="button" class="join-item btn ${pageNumber == 0 ? 'btn-disabled' : ''}">
                                    ${pageNumber == 0 ? '...' : '« 1'}
                            </a>
                            <%-- previous --%>
                            <a href="${pageContext.request.contextPath}/app/secure/measurements?vehicle=${vehicle.code()}&page=${pageNumber - 1}"
                               role="button" class="join-item btn ${maximumPageNumber < 1 || pageNumber <= 1 ? 'btn-disabled' : ''}">
                                    ${maximumPageNumber < 1 || pageNumber <= 1 ? '...' : '‹ '.concat(pageNumber - 1 + 1)}
                            </a>
                            <%-- current --%>
                            <a href="${pageContext.request.contextPath}/app/secure/measurements?vehicle=${vehicle.code()}&page=${pageNumber}"
                               role="button" class="join-item btn btn-active">
                                    ${pageNumber + 1}
                            </a>
                            <%-- next --%>
                            <a href="${pageContext.request.contextPath}/app/secure/measurements?vehicle=${vehicle.code()}&page=${pageNumber + 1}"
                               role="button" class="join-item btn ${pageNumber >= maximumPageNumber - 1 ? 'btn-disabled' : ''}">
                                    ${pageNumber >= maximumPageNumber ? '...' : ''.concat(pageNumber + 1 + 1).concat('›')}
                            </a>
                            <%-- last --%>
                            <a href="${pageContext.request.contextPath}/app/secure/measurements?vehicle=${vehicle.code()}&page=${maximumPageNumber}"
                               role="button" class="join-item btn ${pageNumber == maximumPageNumber ? 'btn-disabled' : ''}">
                                    ${pageNumber == maximumPageNumber ? '...' : ''.concat(maximumPageNumber + 1).concat(' »')}
                            </a>
                        </div>
                    </td>
                </tr>
            </c:if>
        </tfoot>
    </table>

    <a href="${pageContext.request.contextPath}/app/secure/measurements/new?vehicle=${vehicle.code()}" class="btn btn-primary btn-wide">
        <svg class="icon stroke-current" xmlns="http://www.w3.org/2000/svg">
            <use xlink:href="#plus-circle"></use>
        </svg>
        Create
    </a>
</layout:main>