<%@tag description="Left navigation menu for Time & Attendance screens" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<section class="left-nav" ess-navigation>
    <ess-component-nav:nav-header topicTitle="Time And Attendance Menu" colorClass="teal"/>
    <h3 class="main-topic">My Attendance</h3>
    <ul class="sub-topic-list">
        <li class="sub-topic"><a href="${ctxPath}/time/record/entry">Attendance Record Entry</a></li>
        <li class="sub-topic"><a href="${ctxPath}/time/record/history">Attendance History</a></li>
        <li class="sub-topic"><a href="${ctxPath}/time/period/calendar">Payroll Calendar</a></li>
    </ul>
    <h3 class="main-topic">My Accruals</h3>
        <ul class="sub-topic-list">
            <li class="sub-topic"><a href="${ctxPath}/time/accrual/history">Accrual Summary</a></li>
            <li class="sub-topic"><a href="${ctxPath}/time/accrual/projections">Accrual Projections</a></li>
        </ul>
    <shiro:hasRole name="supervisor">
        <h3 class="main-topic">Manage Employees</h3>
        <ul class="sub-topic-list" ng-init="initializePendingRecordsBadge()">
            <li class="sub-topic">
                <a href="${ctxPath}/time/record/manage">Review Time Records</a>
                <badge badge-id="pendingRecordCount" hide-empty="true"></badge>
            </li>
            <li class="sub-topic"><a href="${ctxPath}/time/record/emphistory">Employee Record History</a></li>
            <li class="sub-topic"><a href="${ctxPath}/time/record/grant">Grant Privileges</a></li>
        </ul>
    </shiro:hasRole>
</section>