<%@tag description="Includes ess-travel assets based on the runtime level" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript" src='http://maps.google.com/maps/api/js?libraries=places&key=${googleApiKey}'></script>

<c:choose>
    <c:when test="${runtimeLevel eq 'dev'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/travel-application-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/travel-application-purpose-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/travel-application-outbound-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/travel-application-return-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/travel-application-allowances-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/travel-application-review-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/address-county-service.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/modal/lodging-details-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/modal/continue-saved-app-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/modal/meal-details-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/modal/mileage-details-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/modal/address-county-modal.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/form/app-form-body-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/form/app-form-print-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/form/app-form-view-modal.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/app-summary-table-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/address-autocomplete-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/travel-inner-container-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/address-geocoder-service.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/google-place-service.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/manage/review/app-review-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/manage/review/app-review-form-modal.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/user-config-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/user-apps-ctrl.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/validators/autocomplete-address-validator.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/validators/date-validator.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/validators/mot-validator.js?v=${releaseVersion}"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'test'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-travel.min.js?v=${releaseVersion}"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'prod'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-travel.min.js?v=${releaseVersion}"></script>
    </c:when>
</c:choose>
