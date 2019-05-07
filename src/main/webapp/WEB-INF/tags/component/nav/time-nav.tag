<%@ tag import="gov.nysenate.ess.time.model.auth.SimpleTimePermission" %>
<%@ tag import="gov.nysenate.ess.core.model.auth.SimpleEssPermission" %>
<%@tag description="Left navigation menu for Time & Attendance screens" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<section class="left-nav" ess-navigation>
  <ess-component-nav:nav-header topicTitle="Time &amp; Attendance Menu" colorClass="teal"/>
  <h3 class="main-topic">My Attendance</h3>
  <ul class="sub-topic-list">
    <shiro:hasPermission name="<%= SimpleTimePermission.ATTENDANCE_RECORD_PAGES.getPermissionString() %>">
      <li class="sub-topic teal"><a href="${ctxPath}/time/record/entry">Attendance Record Entry</a></li>
      <li class="sub-topic teal"><a href="${ctxPath}/time/record/history">Attendance History</a></li>
    </shiro:hasPermission>
    <shiro:hasPermission name="<%= SimpleTimePermission.ALLOWANCE_PAGE.getPermissionString() %>">
      <li class="sub-topic teal"><a href="${ctxPath}/time/allowance/status">Allowed Hours</a></li>
    </shiro:hasPermission>
    <li class="sub-topic teal"><a href="${ctxPath}/time/period/calendar">Payroll Calendar</a></li>
  </ul>
  <shiro:hasPermission name="<%= SimpleTimePermission.ACCRUAL_PAGES.getPermissionString() %>">
    <h3 class="main-topic">My Accruals</h3>
    <ul class="sub-topic-list">
        <li class="sub-topic teal"><a href="${ctxPath}/time/accrual/history">Accrual History</a></li>
        <shiro:hasPermission name="<%= SimpleTimePermission.ACCRUAL_PROJECTIONS.getPermissionString() %>">
            <li class="sub-topic teal"><a href="${ctxPath}/time/accrual/projections">Accrual Projections</a></li>
        </shiro:hasPermission>
    </ul>
  </shiro:hasPermission>
  <shiro:hasPermission name="<%= SimpleTimePermission.MANAGEMENT_PAGES.getPermissionString() %>">
    <h3 class="main-topic">Manage Employees</h3>
    <ul class="sub-topic-list" ng-init="initializePendingRecordsBadge()">
      <li class="sub-topic teal">
        <a href="${ctxPath}/time/record/manage">Review Time Records</a>
        <badge title="Records needing action" style="cursor: default"
               badge-id="pendingRecordCount" hide-empty="true" color="teal"></badge>
      </li>
      <li class="sub-topic teal"><a href="${ctxPath}/time/record/emphistory">Employee Attendance History</a></li>
      <shiro:hasPermission name="<%= SimpleTimePermission.EMPLOYEE_ALLOWANCE_PAGE.getPermissionString()%>">
        <li class="sub-topic teal"><a href="${ctxPath}/time/allowance/emp-status">Employee Allowed Hours</a></li>
      </shiro:hasPermission>
      <li class="sub-topic teal"><a href="${ctxPath}/time/accrual/emphistory">Employee Accrual History</a></li>
      <li class="sub-topic teal"><a href="${ctxPath}/time/accrual/emp-projections">Employee Accrual Projections</a></li>
      <li class="sub-topic teal"><a href="${ctxPath}/time/record/grant">Grant Supervisor Access</a></li>
    </ul>
  </shiro:hasPermission>
  <shiro:hasPermission name="<%= SimpleTimePermission.PERSONNEL_PAGES.getPermissionString() %>">
    <h3 class="main-topic">Personnel</h3>
    <ul class="sub-topic-list">
      <li class="sub-topic teal"><a href="${ctxPath}/time/personnel/search">Employee Search</a></li>
    </ul>
  </shiro:hasPermission>
</section>
