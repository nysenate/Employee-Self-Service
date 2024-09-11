<%@ tag import="gov.nysenate.ess.travel.authorization.permission.SimpleTravelPermission" %>
<%@tag description="Left navigation menu for Travel screens" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="shir" uri="http://shiro.apache.org/tags" %>

<div class="left-nav-div flex-column-box">
  <section class="left-nav-content flex-header" ess-navigation>

    <ess-component-nav:nav-header topicTitle="Travel Menu" colorClass="orange"/>
    <shiro:hasPermission name="<%= SimpleTravelPermission.TRAVEL_SUBMIT_APP.getPermissionString() %>">
    <h3 class="main-topic">My Travel</h3>
    <ul class="sub-topic-list">
      <li class="sub-topic orange"><a href="${ctxPath}/travel/application/new">Submit Travel Application</a></li>
      <li class="sub-topic orange"><a href="${ctxPath}/travel/applications">Travel History</a></li>
      <li class="sub-topic orange"><a href="${ctxPath}/travel/drafts">Drafts</a></li>
      <%--<li class="sub-topic"><a href="${ctxPath}/travel/config">User Configuration</a></li>--%>
    </ul>
    </shiro:hasPermission>

    <shiro:hasPermission name="<%= SimpleTravelPermission.TRAVEL_UI_MANAGE.getPermissionString() %>">
      <h3 class="main-topic">Manage Travel</h3>
      <ul class="sub-topic-list">
        <shiro:hasPermission name="<%= SimpleTravelPermission.TRAVEL_UI_REVIEW.getPermissionString() %>">
          <li class="sub-topic orange">
            <a href="${ctxPath}/travel/manage/review">Review Travel</a>
            <shiro:hasRole name="DEPARTMENT_HEAD">
            <badge title="Applications pending department head review"
                   badge-id="travelPendingDeptHdCount" hide-empty="false" color="orange"></badge>
            </shiro:hasRole>
            <shiro:hasRole name="TRAVEL_ADMIN">
            <badge title="Applications pending travel admin review"
                   badge-id="travelPendingAdminCount" hide-empty="false" color="teal"></badge>
            </shiro:hasRole>
            <shiro:hasRole name="SECRETARY_OF_THE_SENATE">
            <badge title="Applications pending secretary review"
                   badge-id="travelPendingSecretaryCount" hide-empty="false" color="green"></badge>
            </shiro:hasRole>
          </li>
        </shiro:hasPermission>
        <shiro:hasPermission name="<%= SimpleTravelPermission.TRAVEL_UI_REVIEW_HISTORY.getPermissionString() %>">
          <li class="sub-topic orange"><a href="${ctxPath}/travel/manage/history">Review History</a></li>
        </shiro:hasPermission>
        <shiro:hasPermission name="<%= SimpleTravelPermission.TRAVEL_ASSIGN_DELEGATES.getPermissionString() %>">
          <li class="sub-topic orange"><a href="${ctxPath}/travel/delegation">Assign Delegates</a></li>
        </shiro:hasPermission>
      </ul>
    </shiro:hasPermission>

    <shiro:hasPermission name="<%= SimpleTravelPermission.TRAVEL_UI_RECONCILE_TRAVEL.getPermissionString() %>">
      <h3 class="main-topic">Reconcile Travel</h3>
      <ul>
        <li class="sub-topic orange">
          <a href="${ctxPath}/travel/reconcile">Reconcile Travel</a>
        </li>
      </ul>
    </shiro:hasPermission>

  </section>
</div>