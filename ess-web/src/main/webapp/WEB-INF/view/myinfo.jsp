<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ess-component" tagdir="/WEB-INF/tags/component" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="ess-layout" tagdir="/WEB-INF/tags/layout" %>

<ess-layout:head>
    <jsp:attribute name="pageTitle">ESS - Time and Attendance</jsp:attribute>
    <jsp:body>
        <ess:ts-assets/>
        <!-- Personnel -->
        <script type="text/javascript" src="${ctxPath}/assets/js/src/myinfo/personnel/summary-ctrl.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/myinfo/personnel/transaction-history-ctrl.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/myinfo/personnel/transaction-history-filters.js"></script>
        <!-- Payroll -->
        <script type="text/javascript" src="${ctxPath}/assets/js/src/myinfo/payroll/check-history-ctrl.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/myinfo/payroll/check-history-filters.js"></script>
    </jsp:body>
</ess-layout:head>

<ess-layout:body>
    <jsp:body>
        <base href="/" />
        <ess-component-nav:top-nav activeTopic="myinfo"/>
        <section class="content-wrapper" ng-controller="MainCtrl as main">
            <ess-component-nav:myinfo-nav/>
            <div class="view-animate-container">
                <div ng-view class="view-animate"></div>
            </div>
        </section>
    </jsp:body>
</ess-layout:body>