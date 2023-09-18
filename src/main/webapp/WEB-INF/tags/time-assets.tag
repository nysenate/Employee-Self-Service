<%@tag description="Includes ess-time assets based on the runtime level" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${runtimeLevel eq 'dev'}">
        <!-- Time Entry -->
        <script type="text/javascript" src="${ctxPath}/assets/js/src/time/time.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-filters.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-directives.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-utils.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-entry-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-manage-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/supervisor-record-list.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-review-modals.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-history-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-validation.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-entry-modals.js?v=${releaseVersion}"></script>

        <!-- Personnel -->
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/personnel/sup-emp-group-service.js?v=${releaseVersion}"></script>

        <!-- Time Off Requests -->
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/timeoff/new-request-ctrl.js?v=${releaseVersion}"></script>

        <!-- Pay Period Viewer -->
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/period/pay-period-view-ctrl.js?v=${releaseVersion}"></script>

        <!-- Accruals -->
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/accrual/accrual-bar-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/accrual/accrual-detail-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/accrual/accrual-history-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/accrual/accrual-projections-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/accrual/accrual-utils.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/accrual/sick-time-donation-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/accrual/donation-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/accrual/donation-continue-modal.js?v=${releaseVersion}"></script>

        <!-- Allowances -->
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/allowance/allowance-bar-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/allowance/allowance-history-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/allowance/allowance-status-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/allowance/allowance-utils.js?v=${releaseVersion}"></script>

        <!-- Grants -->
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/grant/grant-ctrl.js?v=${releaseVersion}"></script>

        <!-- Tests -->
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/test/error-modal-test.js?v=${releaseVersion}"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'test'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-time.min.js?v=${releaseVersion}"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'prod'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-time.min.js?v=${releaseVersion}"></script>
    </c:when>
</c:choose>