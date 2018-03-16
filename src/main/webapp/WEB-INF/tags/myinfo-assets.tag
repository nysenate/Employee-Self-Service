<%@tag description="Includes ess-myinfo assets based on the runtime level" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${runtimeLevel eq 'dev'}">
        <!-- Personnel -->
        <script type="text/javascript" src="${ctxPath}/assets/js/src/myinfo/myinfo-ctrl.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/myinfo/personnel/summary-ctrl.js"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/myinfo/personnel/alert-ctrl.js"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/myinfo/personnel/acknowledgment-ctrl.js"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/myinfo/personnel/ack-doc-report-ctrl.js"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/myinfo/personnel/ack-doc-select-directive.js"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/myinfo/personnel/ack-doc-view-ctrl.js"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/myinfo/personnel/transaction-history-ctrl.js"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/myinfo/personnel/transaction-history-filters.js"></script>
        <!-- Payroll -->
        <script type="text/javascript" src="${ctxPath}/assets/js/src/myinfo/payroll/check-history-ctrl.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/myinfo/payroll/check-history-filters.js"></script>

    </c:when>
    <c:when test="${runtimeLevel eq 'test'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-myinfo.min.js?v=${releaseVersion}"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'prod'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-myinfo.min.js?v=${releaseVersion}"></script>
    </c:when>
</c:choose>