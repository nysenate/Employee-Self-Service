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
  <section class="left-nav-content margin-top-20" ng-controller="SupplyNavigationController" ng-show="displayCategoryNavigation">
    <ess-component-nav:nav-header topicTitle="Categories" colorClass="blue-purple"/>
    <ul class="sub-topic-list">
      <li ng-repeat="cat in categories">
        <input type="checkbox" ng-model="cat.selected">
        <label>{{cat.name}}</label>
      </li>
    </ul>
  </section>
</div>
