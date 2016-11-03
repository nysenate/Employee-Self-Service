<%@tag description="Includes ess-supply assets based on the runtime level" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${runtimeLevel eq 'dev'}">
        <!-- Supply Entry -->
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
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/supply/items/supply-item-autocomplete-service.js?v=${releaseVersion}"></script>

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
                src="${ctxPath}/assets/js/src/supply/order/supply-quantity-selector.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/supply/order/order-quantity-validator.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/supply/order/location-allowance-service.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/supply/order/order-destination-service.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/supply/order/order-more-prompt-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/supply/order/order-canceling-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/supply/order/special-order-item-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/supply/order/supply-line-item-service.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/supply/order/supply-item-filter-service.js?v=${releaseVersion}"></script>

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
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/supply/service/supply-location-statistics-service.js"></script>

    </c:when>
    <c:when test="${runtimeLevel eq 'test'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-supply.min.js?v=${releaseVersion}"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'prod'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-supply.min.js?v=${releaseVersion}"></script>
    </c:when>
</c:choose>