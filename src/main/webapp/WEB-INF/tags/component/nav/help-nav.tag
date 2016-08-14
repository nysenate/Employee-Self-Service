<%@tag description="Left navigation menu for Time & Attendance screens" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>

<section class="left-nav" ess-navigation>
    <ess-component-nav:nav-header topicTitle="Help Menu" colorClass="orange"/>
    <h3 class="main-topic">Time & Attendance Help</h3>
    <ul class="sub-topic-list">
        <li class="sub-topic"><a href="${ctxPath}/help">Overview</a></li>
        <li class="sub-topic"><a href="${ctxPath}/help">Entering a T&A Record</a></li>
        <li class="sub-topic"><a href="${ctxPath}/help">Reviewing a T&A Record</a></li>
        <li class="sub-topic"><a href="${ctxPath}/help/ta/plan">T&A Plan</a></li>
    </ul>
</section>