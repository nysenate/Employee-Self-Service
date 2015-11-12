<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>

<section class="left-nav-content margin-top-20" ng-controller="SupplyOrderController">
  <ess-component-nav:nav-header topicTitle="Categories" colorClass="blue-purple"/>
  <ul class="sub-topic-list">
    <li ng-repeat="cat in categories">
      <input type="checkbox" ng-model="cat.selected" ng-change="filterByCategories()">
      <label>{{cat.categoryName}}</label>
    </li>
  </ul>
</section>