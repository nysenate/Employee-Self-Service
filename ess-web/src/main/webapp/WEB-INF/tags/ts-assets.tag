<%@tag description="Includes common assets for the Timesheets app based on the runtime level" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<link rel="stylesheet" type="text/css" href="${ctxPath}/assets/css/dest/app.min.css"/>

<!--[if lte IE 8]>
<script type="text/javascript" src="${ctxPath}/assets/js/dest/timesheets-vendor-ie.min.js"></script>
<![endif]-->

<script type="text/javascript" src="${ctxPath}/assets/js/dest/timesheets-vendor.min.js"></script>
<c:choose>
    <c:when test="${runtimeLevel eq 'dev'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/src/ess-app.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/ess-api.js"></script>

        <!-- Navigation -->
        <script type="text/javascript" src="${ctxPath}/assets/js/src/nav/ess-nav.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/nav/ess-routes.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/nav/home.js"></script>

        <!-- Common Directives -->
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/toggle-panel-directive.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/pagination-model.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/ess-notifications.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/loader-indicator-directive.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/odometer-directive.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/datepicker-directive.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/moment-filter.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/not-filter.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/modal-service.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/modal-directive.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/internal-error-modal.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/location-service.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/badge-service.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/badge-directive.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/common/ai-filter.js"></script>

    </c:when>
    <c:when test="${runtimeLevel eq 'test'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/timesheets.min.js"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'prod'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/timesheets.min.js"></script>
    </c:when>
</c:choose>