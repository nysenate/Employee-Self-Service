<%@ tag import="gov.nysenate.ess.travel.authorization.permission.TravelPermission" %>
<%@tag description="Left navigation menu for Travel screens" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<div class="left-nav-div flex-column-box">
  <section class="left-nav-content flex-header" ess-navigation>
    <ess-component-nav:nav-header topicTitle="Travel Menu" colorClass="orange"/>
    <h3 class="main-topic">My Travel</h3>
    <ul class="sub-topic-list">
      <li class="sub-topic orange"><a href="${ctxPath}/travel/application/travel-application">Travel Application</a></li>
      <li class="sub-topic orange"><a href="${ctxPath}/travel/apps">View Applications</a></li>
      <%--<li class="sub-topic"><a href="${ctxPath}/travel/config">User Configuration</a></li>--%>
    </ul>
    <shiro:hasPermission name="<%= TravelPermission.TRAVEL_UI_APPROVAL.getPermissionString() %>">
    <h3 class="main-topic">Manage Travel</h3>
      <ul class="sub-topic-list">
        <li class="sub-topic orange"><a href="${ctxPath}/travel/manage/review">Review Applications</a></li>
      </ul>
    </shiro:hasPermission>
  </section>
</div>