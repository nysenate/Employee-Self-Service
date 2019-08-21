<%--
  Created by IntelliJ IDEA.
  User: senate
  Date: 8/20/19
  Time: 2:59 PM
  To change this template use File | Settings | File Templates.
--%>

<!-- Page to view a single time-off request
      (Request is given by the requestId path variable) -->

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="SingleRequestCtrl">
  <div class="time-attendance-hero">
    <h2>Time Off Requests</h2>
  </div>

  <div class="timeoff-request-accrual-container content-container content-controls" ng-if="!loadingRequests">
    <div class="content-container" ng-if="!loadingRequests"><h1>Time Off Request: {{startDate}} to {{endDate}}</h1></div>

      <time-off-request-view ng-if="!loadingRequests" mode="viewMode" data="request"></time-off-request-view>

  </div>
</div>