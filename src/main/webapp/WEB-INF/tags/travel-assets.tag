<%@tag description="Includes ess-travel assets based on the runtime level" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${runtimeLevel eq 'dev'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/upcoming-travel-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/new-application-ctrl.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/travel-app-locations-directive.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/application/origin-selection-modal.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/travel-request-history.js?v=${releaseVersion}"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/travel/manage/travel-manage-history-ctrl.js?v=${releaseVersion}"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'test'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-travel.min.js?v=${releaseVersion}"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'prod'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-travel.min.js?v=${releaseVersion}"></script>
    </c:when>
</c:choose>
