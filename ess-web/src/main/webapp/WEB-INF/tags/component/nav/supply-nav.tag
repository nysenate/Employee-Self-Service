<%@tag description="Left navigation menu for Supply screens" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<div class="left-nav-div">
  <section class="left-nav-content no-print" ess-navigation>
    <ess-component-nav:nav-header topicTitle="Supply Menu" colorClass="blue-purple"/>
    <h3 class="main-topic">My Supply</h3>
    <ul class="sub-topic-list">
      <li class="sub-topic"><a href="${ctxPath}/supply/order">Requisition Form</a></li>
      <li class="sub-topic"><a href="${ctxPath}/supply/order/cart">Cart</a></li>
      <li class="sub-topic"><a href="${ctxPath}/supply/history/location-history">Location History</a></li>
    </ul>
    <shiro:hasPermission name="supply:shipment:manage">
    <h3 class="main-topic">Manage Supply</h3>
    <ul class="sub-topic-list">
      <li class="sub-topic"><a href="${ctxPath}/supply/manage/fulfillment">Fulfillment</a></li>
      <li class="sub-topic"><a href="${ctxPath}/supply/manage/reconciliation">Reconciliation</a></li>
      <li class="sub-topic"><a href="${ctxPath}/supply/history/history">Requisition History</a></li>
    </ul>
    </shiro:hasPermission>
  </section>
  <section class="left-nav-content margin-top-20 no-print" ng-controller="SupplyNavigationController" ng-show="shouldDisplayCategoryFilter">
    <ess-component-nav:nav-header topicTitle="Categories" colorClass="blue-purple"/>
    <div class="padding-10">
      <a style="padding-left: 10px;" ng-click="clearSelections()">
        Clear All
      </a>
    </div>
    <div style="height: 300px; overflow-y: auto">
      <ul class="sub-topic-list">
        <li ng-repeat="cat in categories">
          <input type="checkbox" ng-model="cat.selected" ng-change="onCategorySelected()">
          <label>{{cat.name}}</label>
        </li>
      </ul>
    </div>
  </section>
</div>
