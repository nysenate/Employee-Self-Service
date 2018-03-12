<%@tag description="Includes ess-login assets based on the runtime level" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<link rel="stylesheet" type="text/css" href="${ctxPath}/assets/css/dest/app.min.css?v=${releaseVersion}"/>

<!--[if lte IE 8]>
<script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-vendor-ie.min.js?v=${releaseVersion}"/></script>
<![endif]-->

<script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-vendor.min.js?v=${releaseVersion}"></script>
<c:choose>
    <c:when test="${runtimeLevel eq 'dev'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/src/auth/login.js?v=${releaseVersion}"></script>
        <script type="text/javascript"
                src="${ctxPath}/assets/js/src/common/ess-notifications.js?v=${releaseVersion}"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'test'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-login.min.js?v=${releaseVersion}"></script>
    </c:when>
    <c:when test="${runtimeLevel eq 'prod'}">
        <script type="text/javascript" src="${ctxPath}/assets/js/dest/ess-login.min.js?v=${releaseVersion}"></script>
    </c:when>
</c:choose>