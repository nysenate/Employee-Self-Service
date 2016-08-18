<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ess-component" tagdir="/WEB-INF/tags/component" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="ess-layout" tagdir="/WEB-INF/tags/layout" %>

<ess-layout:head>
  <jsp:attribute name="pageTitle">ESS - Time and Attendance</jsp:attribute>
  <jsp:body>
    <ess:ts-assets/>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/nav/supply-category-nav-ctrl.js?v=${releaseVersion}"></script>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/service/supply-inventory-service.js?v=${releaseVersion}"></script>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/service/supply-category-service.js?v=${releaseVersion}"></script>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/location/supply-location-autocomplete-service.js?v=${releaseVersion}"></script>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/service/supply-cookie-service.js?v=${releaseVersion}"></script>

    <%-- History --%>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/history/supply-history-ctrl.js?v=${releaseVersion}"></script>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/history/supply-order-history-ctrl.js?v=${releaseVersion}"></script>

    <%-- Manage --%>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/manage/fulfillment/supply-fulfillment-ctrl.js?v=${releaseVersion}"></script>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/manage/supply-reconciliation-ctrl.js?v=${releaseVersion}"></script>

    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/manage/fulfillment/modal/fulfillment-editing-modal.js?v=${releaseVersion}"></script>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/manage/fulfillment/modal/fulfillment-immutable-modal.js?v=${releaseVersion}"></script>
    <%-- Order --%>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/order/supply-order-ctrl.js?v=${releaseVersion}"></script>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/order/location-allowance-service.js?v=${releaseVersion}"></script>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/order/order-destination-service.js?v=${releaseVersion}"></script>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/order/order-more-prompt-modal.js?v=${releaseVersion}"></script>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/order/order-custom-quantity-modal.js?v=${releaseVersion}"></script>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/order/special-order-item-modal.js?v=${releaseVersion}"></script>

    <%-- Cart --%>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/order/cart/supply-cart-service.js?v=${releaseVersion}"></script>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/order/cart/supply-cart-ctrl.js?v=${releaseVersion}"></script>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/order/cart/supply-cart-directives.js?v=${releaseVersion}"></script>

    <%-- Requisition --%>
    <script type="text/javascript"
            src="${ctxPath}/assets/js/src/supply/requisition/supply-view-ctrl.js?v=${releaseVersion}"></script>

    <%-- Utilities --%>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/util/supply-utils-service.js"></script>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/supply/service/supply-location-statistics-service.js"></script>

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
