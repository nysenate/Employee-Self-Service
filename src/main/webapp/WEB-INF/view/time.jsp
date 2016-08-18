<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ess-component" tagdir="/WEB-INF/tags/component" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="ess-layout" tagdir="/WEB-INF/tags/layout" %>

<ess-layout:head>
    <jsp:attribute name="pageTitle">ESS - Time and Attendance</jsp:attribute>
    <jsp:body>
        <ess:ts-assets/>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/time/time.js?v=${releaseVersion}"></script>

        <!-- Time Entry -->
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-filters.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-directives.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-utils.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-entry-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-history-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-manage-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/supervisor-record-list.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-review-modals.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-emp-history-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/record/record-validation.js?v=${releaseVersion}"></script>

        <!-- Time Off Requests -->
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/timeoff/new-request-ctrl.js?v=${releaseVersion}"></script>

        <!-- Pay Period Viewer -->
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/period/pay-period-view-ctrl.js?v=${releaseVersion}"></script>

        <!-- Accruals -->
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/accrual/accrual-history-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/accrual/accrual-projection-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/accrual/accrual-utils.js?v=${releaseVersion}"></script>

        <!-- Grants -->
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/time/grant/grant-ctrl.js?v=${releaseVersion}"></script>

    </jsp:body>
</ess-layout:head>

<ess-layout:body>
    <jsp:body>
        <ess-component-nav:top-nav activeTopic="time"/>
        <section class="content-wrapper" ng-controller="TimeMainCtrl">
            <ess-component-nav:time-nav/>
            <div class="view-animate-container">
                <div ng-view class="view-animate"></div>
            </div>
        </section>
    </jsp:body>
</ess-layout:body>