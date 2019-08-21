<%--
  Created by IntelliJ IDEA.
  User: senate
  Date: 8/19/19
  Time: 10:29 AM
  To change this template use File | Settings | File Templates.
--%>

<!-- Page to create a new time off request -->

<div ng-controller="NewRequestCtrl">
  <div class="time-attendance-hero">
    <h2>New Time Off Request</h2>
  </div>
  <div class="timeoff-request-accrual-container content-container">

    <a class="time-off-request-back-button" ng-click="goBack()">
      Back to Time Off Requests
    </a>

    <time-off-request-view data="data" mode="viewMode"></time-off-request-view>

  </div>
</div>