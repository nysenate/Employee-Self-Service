<%@ tag import="gov.nysenate.ess.core.model.auth.SimpleEssPermission" %>
<%@tag description="Left navigation menu for Time & Attendance screens" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<section class="left-nav" ess-navigation>
  <ess-component-nav:nav-header topicTitle="My Info Menu" colorClass="green"/>
  <h3 class="main-topic">Personnel</h3>
  <ul class="sub-topic-list">
    <li class="sub-topic green"><a href="${ctxPath}/myinfo/personnel/summary">Current Info</a></li>
    <li class="sub-topic green"><a href="${ctxPath}/myinfo/personnel/emergency-alert-info">Emergency Alert Info</a></li>
    <li class="sub-topic green">
      <a href="${ctxPath}/myinfo/personnel/acknowledgments">Acknowledgments</a>
      <badge title="Pending Acknowledgments"
             badge-id="unacknowledgedDocuments" hide-empty="false" color="green"></badge>
    </li>
  </ul>
  <shiro:hasPermission name="<%= SimpleEssPermission.COMPLIANCE_REPORT_GENERATION.getPermissionString() %>">
    <h3 class="main-topic">Acknowledgments</h3>
    <ul>
      <li class="sub-topic green">
        <a href="${ctxPath}/myinfo/personnel/ack-doc-report">Full Report</a>
      </li>
      <li class="sub-topic green">
        <a href="${ctxPath}/myinfo/personnel/emp-ack-doc-report">Employee Report</a>
      </li>
    </ul>
  </shiro:hasPermission>
  <h3 class="main-topic">Payroll</h3>
  <ul class="sub-topic-list">
    <li class="sub-topic green"><a href="${ctxPath}/myinfo/payroll/checkhistory">Paycheck History</a></li>
  </ul>
</section>