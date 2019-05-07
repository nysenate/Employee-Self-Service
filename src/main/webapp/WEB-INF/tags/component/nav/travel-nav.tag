<%@ tag import="gov.nysenate.ess.travel.authorization.permission.TravelPermission" %>
<%@tag description="Left navigation menu for Travel screens" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<div class="left-nav-div flex-column-box">
  <section class="left-nav-content flex-header" ess-navigation>
    <ess-component-nav:nav-header topicTitle="Travel Menu" colorClass="orange"/>
    <h3 class="main-topic">My Travel</h3>
    <ul class="sub-topic-list">
      <li class="sub-topic orange"><a href="${ctxPath}/travel/application/new">New Travel Application</a></li>
      <li class="sub-topic orange"><a href="${ctxPath}/travel/applications">View Travel Applications</a></li>
      <%--<li class="sub-topic"><a href="${ctxPath}/travel/config">User Configuration</a></li>--%>
    </ul>
    <shiro:hasPermission name="<%= TravelPermission.TRAVEL_UI_MANAGE.getPermissionString() %>">
      <h3 class="main-topic">Manage Travel</h3>
      <ul class="sub-topic-list">
        <shiro:hasPermission name="<%= TravelPermission.TRAVEL_UI_REVIEW.getPermissionString() %>">
          <li class="sub-topic orange">
            <a href="${ctxPath}/travel/review">Review Applications</a>
            <badge title="Applications pending review"
                   badge-id="travelPendingAppReviewCount" hide-empty="false" color="orange"></badge>
          </li>
        </shiro:hasPermission>
        <shiro:hasPermission name="<%= TravelPermission.TRAVEL_UI_REVIEW_HISTORY.getPermissionString() %>">
          <li class="sub-topic orange"><a href="${ctxPath}/travel/review/history">Review History</a></li>
        </shiro:hasPermission>
      </ul>
    </shiro:hasPermission>
  </section>
</div>