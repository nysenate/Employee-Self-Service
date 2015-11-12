<%@tag description="Left navigation menu for Time & Attendance screens" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<div class="left-nav-div">
  <section class="left-nav-content" ess-navigation>
    <ess-component-nav:nav-header topicTitle="Supply Menu" colorClass="blue-purple"/>
    <h3 class="main-topic">My Supply</h3>
    <ul class="sub-topic-list">
      <li class="sub-topic"><a href="${ctxPath}/supply/requisition/order">Requisition Form</a></li>
      <li class="sub-topic"><a href="${ctxPath}/supply/cart/cart">Cart</a></li>
    </ul>
  </section>
  <%--TODO: if in order page. --%>
  <category-navigation></category-navigation>
</div>
