<%@tag description="Left navigation menu for Travel screens" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<div class="left-nav-div flex-column-box">
  <section class="left-nav-content flex-header" ess-navigation>
    <ess-component-nav:nav-header topicTitle="Travel Menu" colorClass="orange"/>
    <h3 class="main-topic">My Travel</h3>
    <ul class="sub-topic-list">
      <li class="sub-topic"><a href="${ctxPath}/travel/application/travel-application">Travel Application</a></li>
      <li class="sub-topic"><a href="${ctxPath}/travel/view-applications">View Applications</a></li>
      <%--<li class="sub-topic"><a href="${ctxPath}/travel/travel-user-config">User Configuration</a></li>--%>
    </ul>
    <%--<h3 class="main-topic">Manage Requests</h3>--%>
    <%--<ul class="sub-topic-list">--%>
      <%--<li class="sub-topic"><a href="${ctxPath}/travel/manage/review-travel-requests">Review Travel Requests</a></li>--%>
      <%--<li class="sub-topic"><a href="${ctxPath}/travel/manage/travel-manage-history">Travel Request History</a></li>--%>
    <%--</ul>--%>
  </section>
</div>