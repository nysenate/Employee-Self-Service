<%@tag description="Includes ess-travel assets based on the runtime level" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript" src='https://maps.google.com/maps/api/js?libraries=places&key=${googleApiKey}'></script>

<c:choose>
    <c:when test="${runtimeLevel eq 'dev'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/new-application/new-application-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/new-application/continue-saved-app-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/new-application/new-app-breadcrumbs-directive.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/review-travel-app/review-travel-app.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/review-travel-app/review-travel-app-action-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/review-travel-app/review-travel-app-approve-confirm-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/review-travel-app/review-travel-app-disapprove-confirm-modal.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/review-history/review-history-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/review-history/app-review-view-modal.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/user-config/user-config-ctrl.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/view-applications/view-applications-ctrl.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/drafts/application-drafts-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/drafts/delete-draft-modal-directive.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/edit-application/edit-application-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/edit-application/edit-app-breadcrumbs-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/edit-application/perdiem-overrides-edit-form-directive.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/delegation/delegation-ctrl.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/reconcile-travel/reconcile-travel-ctrl.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/component/travel-application/travel-application-ctrl.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/app/app-edit-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/app/app-edit-state-service.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/app/purpose-edit-form-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/app/outbound-edit-form-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/app/return-edit-form-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/app/allowances-edit-form-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/app/review-edit-form-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/app/address-county-service.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/app/modal/address-county-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/app/modal/travel-date-error-modal.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/form/app-form-body-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/form/app-form-view-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/form/app-review-form-body-directive.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/travel-badge-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/app-summary-table-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/app-status-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/review-action-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/action-type-cell-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/address-autocomplete-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/address-geocoder-service.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/google-place-service.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/app-review-summary-table-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/travel-role-service.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/transportation-summary-popover.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/meal-summary-popover.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/lodging-summary-popover.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/validators/autocomplete-address-validator.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/validators/hotel-address-validator.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/validators/date-validator.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/validators/mot-validator.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/validators/purpose-of-travel-validators.js?v=${releaseVersion}"></script>

        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/api/application-review-api.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/common/api/delegation-api.js?v=${releaseVersion}"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'test'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-travel.min.js?v=${releaseVersion}"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'prod'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-travel.min.js?v=${releaseVersion}"></script>
    </c:when>
</c:choose>
