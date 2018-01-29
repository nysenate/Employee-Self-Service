<%@tag description="Includes common assets for the ess app based on the runtime level" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<link rel="stylesheet" type="text/css" href="${ctxPath}/assets/css/dest/app.min.css?v=${releaseVersion}"/>

<!--[if lte IE 8]>
<script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-vendor-ie.min.js?v=${releaseVersion}"/></script>
<![endif]-->

<script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-vendor.min.js?v=${releaseVersion}"></script>
<c:choose>
    <c:when test="${runtimeLevel eq 'dev'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/src/ess-app.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/ess-api.js?v=${releaseVersion}"></script>

        <!-- Navigation -->
        <script type="text/javascript" src="${ctxPath}/assets/js/src/nav/ess-nav.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/nav/ess-routes.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/nav/home.js?v=${releaseVersion}"></script>

        <!-- Common Directives -->
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/toggle-panel-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/pagination-model.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/ess-notifications.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/loader-indicator-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/odometer-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/datepicker-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/moment-filter.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/not-filter.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/modal-service.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/modal-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/internal-error-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/timeout-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/timeout-checker.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/location-service.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/badge-service.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/badge-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/ai-filter.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/text-auto-height-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/round-filter.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/err-src-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/possessive-filter.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/promise-utils.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/zero-pad-filter.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/debounce-function.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/throttle-function.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/ess-storage-service.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/common-modals.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/modal-close-button.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/highlight-filter.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/print-hook.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/phone-number-filter.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/on-scroll-to-bottom-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/iframe-on-load.js?v=${releaseVersion}"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'test'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess.min.js?v=${releaseVersion}"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'prod'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess.min.js?v=${releaseVersion}"></script>
    </c:when>
</c:choose>