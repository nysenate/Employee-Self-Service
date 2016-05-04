<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ess-component" tagdir="/WEB-INF/tags/component" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="ess-layout" tagdir="/WEB-INF/tags/layout" %>

<ess-layout:head>
  <jsp:attribute name="pageTitle">ESS - Time and Attendance</jsp:attribute>
  <jsp:body>
    <ess:ts-assets/>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/nav/supply-category-nav-ctrl.js"></script>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/service/supply-inventory-service.js"></script>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/service/supply-category-service.js"></script>

    <%-- History --%>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/history/supply-history-ctrl.js"></script>
    <%--<script type="text/javascript" src="${ctxPath}/assets/js/src/supply/history/supply-location-history-ctrl.js"></script>--%>

    <%-- Manage --%>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/manage/supply-manage-ctrl.js"></script>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/manage/supply-reconciliation-ctrl.js"></script>

    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/manage/modal/manage-editing-modal.js"></script>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/manage/modal/manage-immutable-modal.js"></script>
    <%-- Order --%>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/order/supply-order-ctrl.js"></script>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/order/supply-requisition-directives.js"></script>

    <%-- Cart --%>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/order/cart/supply-cart.js"></script>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/order/cart/supply-cart-ctrl.js"></script>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/order/cart/supply-cart-directives.js"></script>

    <%-- Requisition --%>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/requisition/supply-view-ctrl.js"></script>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/requisition/requisition-version.js"></script>
  </jsp:body>
</ess-layout:head>

<ess-layout:body>
  <jsp:body>
    <base href="/" />
    <ess-component-nav:top-nav activeTopic="supply"/>
    <section class="content-wrapper" ng-controller="MainCtrl as main">
      <ess-component-nav:supply-nav/>
      <div class="view-animate-container">
        <div ng-view></div>
      </div>
    </section>
  </jsp:body>
</ess-layout:body>
