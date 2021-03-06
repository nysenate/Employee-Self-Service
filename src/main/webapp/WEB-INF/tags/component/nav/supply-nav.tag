<%@ tag import="gov.nysenate.ess.supply.authorization.permission.SupplyPermission" %>
<%@tag description="Left navigation menu for Supply screens" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<div class="left-nav-div flex-column-box">
  <section class="left-nav-content flex-header" ess-navigation>
    <ess-component-nav:nav-header topicTitle="Supply Menu" colorClass="blue-purple"/>
    <h3 class="main-topic">My Supply</h3>
    <ul class="sub-topic-list">
      <li class="sub-topic blue-purple"><a href="${ctxPath}/supply/shopping/order">Requisition Form</a></li>
      <li class="sub-topic blue-purple"><a href="${ctxPath}/supply/shopping/cart/cart">Shopping Cart</a></li>
      <li class="sub-topic blue-purple"><a href="${ctxPath}/supply/history/order-history">Order History</a></li>
    </ul>
    <shiro:hasPermission name="<%= SupplyPermission.SUPPLY_UI_NAV_MANAGE.getPermissionString() %>">
    <h3 class="main-topic">Manage Supply</h3>
    <ul class="sub-topic-list">
      <shiro:hasPermission name="<%= SupplyPermission.SUPPLY_UI_MANAGE_FULFILLMENT.getPermissionString() %>">
      <li class="sub-topic blue-purple"><a href="${ctxPath}/supply/manage/fulfillment">Fulfillment</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="<%= SupplyPermission.SUPPLY_UI_MANAGE_RECONCILIATION.getPermissionString() %>">
      <li class="sub-topic blue-purple"><a href="${ctxPath}/supply/manage/reconciliation">Reconciliation</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="<%= SupplyPermission.SUPPLY_UI_MANAGE_REQUISITION_HISTORY.getPermissionString() %>">
      <li class="sub-topic blue-purple"><a href="${ctxPath}/supply/history/requisition-history">Requisition History</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="<%= SupplyPermission.SUPPLY_UI_MANAGE_ITEM_HISTORY.getPermissionString() %>">
      <li class="sub-topic blue-purple"><a href="${ctxPath}/supply/history/item-history">Item History</a></li>
      </shiro:hasPermission>
    </ul>
    </shiro:hasPermission>
  </section>
  <section class="left-nav-content flex-content flex-column-box margin-top-20"
           style="margin-bottom: 100px; min-height: 0px; min-width: 0px;"
           ng-controller="SupplyNavigationController"
           ng-show="state.isShopping()">
    <ess-component-nav:nav-header topicTitle="Categories" colorClass="blue-purple"/>
    <div class="flex-header padding-10">
      <a style="padding-left: 10px;" ng-click="clearSelections()">
        Clear All
      </a>
    </div>
    <div class="flex-content" style="overflow-y: auto">
      <ul class="">
        <li ng-repeat="cat in categories">
          <input type="checkbox" ng-model="cat.selected" ng-change="onCategoryUpdated()">
          <label>{{cat.name}}</label>
        </li>
      </ul>
    </div>
  </section>
</div>
