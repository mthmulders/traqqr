<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layouts" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:main title="${title}">
  <form method="post" action="${pageContext.request.contextPath}/app/secure/measurements/new?vehicle=${vehicle.code()}" class="not-prose w-full">
    <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}" />

    <fieldset class="fieldset grid grid-cols-5 gap-4">
      <label for="vehicle.description" class="fieldset-legend col-span-2">Vehicle</label>
      <div class="col-span-3">
        <input type="text" placeholder="${vehicle.description()}" class="input w-full" disabled />
      </div>

      <label for="measurement.odometer" class="fieldset-legend col-span-2">Odometer</label>
      <div class="col-span-3">
        <input id="measurement.odometer" type="number" class="input validator w-full" required min="0" title="Odometer" />
        <p class="label">Current value of the odometer.</p>
        <p class="validator-hint">Must be a positive number</p>
      </div>

      <label for="measurement.soc" class="fieldset-legend col-span-2">Battery SoC</label>
      <div class="col-span-3">
        <input id="measurement.soc" type="number" class="input validator w-full" required min="0" max="100" title="Battery SoC" />
        <p class="label">Current state of charge of the vehicle battery.</p>
        <p class="validator-hint">Must be between 0 and 100</p>
      </div>

      <label for="measurement.location" class="fieldset-legend col-span-2">Location</label>
      <div class="col-span-3">
        <input id="measurement.location" type="text" placeholder="${vehicle.description()}" class="input w-full" />
        <p class="label">Current location of the vehicle, as GPS coordinates.</p>
      </div>
    </fieldset>

    <div class="grid grid-cols-5 gap-4">
      <div class="col-span-2">&nbsp;</div>
      <div class="col-span-3">
        <button type="submit" class="btn btn-primary">Save</button>
      </div>
    </div>

  </form>

  <script type="application/javascript">
    if (navigator.geolocation) {
      const inputElement = document.getElementById('measurement.location');
      const updateInputField = (value) => {
        inputElement.value = value;
        inputElement.dispatchEvent(new Event('change'));
      };
      const onSuccess = (position) => {
        const coordinates = position.coords;
        const inputValue = `\${coordinates.latitude}, \${coordinates.longitude}`;
        updateInputField(inputValue);
      };
      const onFailure = (error) => {
        const inputValue = error.message;
        updateInputField(inputValue);
      };
      navigator.geolocation.getCurrentPosition(onSuccess, onFailure, {enableHighAccuracy: true});
    }
  </script>
</layout:main>