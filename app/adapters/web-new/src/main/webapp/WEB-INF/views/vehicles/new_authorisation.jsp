<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layouts" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:main title="Generated authorisation for vehicle ${vehicle.code}">
  <dl>
    <dt>Code</dt>
    <dd>${vehicle.code}</dd>
    <dt>Description</dt>
    <dd>${vehicle.description}</dd>
    <dt>Generated API key</dt>
    <dd><code>${authorisation.rawKey}</code></dd>
  </dl>

  <div class="badge badge-warning">
    <svg class="icon stroke-current" xmlns="http://www.w3.org/2000/svg">
      <use xlink:href="#shield-exclamation"></use>
    </svg>
    You will see this API key only once. Make sure to write it down!
  </div>
</layout:main>