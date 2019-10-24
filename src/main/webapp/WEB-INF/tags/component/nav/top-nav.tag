<%@tag description="Top navigation menu" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="activeTopic" required="true" description="A key indicating which nav topic should be active." %>

<header class="ess-top-header no-print">
    <iframe class="iframe-cover" src="about:blank" style></iframe>
    <div class="fixed-width-header">
        <div class="left-header-area">
            <div class="nysslogo"></div>
            <div class="nysslogo-text-container">
                <div class="nysslogo-text">
                    <span class="highlight">NYSS </span>
                    <span>ESS</span>
                </div>
            </div>
            <ul class="top-nav-list">
                <li id="dashboardLink" class="main-topic green <c:if test='${activeTopic == "myinfo"}'>active</c:if>">
                    <a target="_self" href="${ctxPath}/myinfo"><img class="nav-icon" src="${ctxPath}/assets/img/user.png"/>My Info</a>
                </li>
                <li id="timeAttendanceLink" class="main-topic teal <c:if test='${activeTopic == "time"}'>active</c:if>">
                    <a target="_self" href="${ctxPath}/time"><img class="nav-icon" src="${ctxPath}/assets/img/20px-ffffff/clock.png"/>Time &amp; Attendance</a>
                </li>
                <li id="supplyLink" class="main-topic blue-purple <c:if test='${activeTopic == "supply"}'>active</c:if>">
                    <a target="_self" href="${ctxPath}/supply"><img class="nav-icon" src="${ctxPath}/assets/img/20px-ffffff/clock.png"/>Supply</a>
                </li>
                <li id="travelLink" class="main-topic orange <c:if test='${activeTopic == "travel"}'>active</c:if>">
                    <a target="_self" href="${ctxPath}/travel"><img class="nav-icon" src="${ctxPath}/assets/img/20px-ffffff/clock.png"/>Travel</a>
                </li>
            </ul>
        </div>
        <div class="right-header-area">
            <c:if test="${runtimeLevel != 'prod'}">
                <div class="header-label-segment dark-red">
                    <span class="dark-red">${runtimeLevel}</span>
                    <span class="dark-red margin-left-10">emp #${principal.getEmployeeId()}</span>
                </div>
            </c:if>
            <div class="header-label-segment">
                Hi, ${principal.getFullName()}
            </div>
            <div id="help-link" class="header-link">
                <a target="_self" ng-href="{{helpPageUrl}}" ng-click="openHelpWindow($event)"><span class="icon-help"></span>Help</a>
            </div>
            <div id="logoutSection" class="header-link">
                <a target="_self" href="${ctxPath}/logout">Sign Out</a>
            </div>
        </div>
    </div>
</header>
