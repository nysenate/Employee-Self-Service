<%--
  Created by IntelliJ IDEA.
  User: senate
  Date: 8/7/19
  Time: 4:02 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  /**
   * This template provides the functionality necessary for employees to
   * view their active time off requests
   */
%>

<div ng-controller="RequestCtrl">
  <div class="time-attendance-hero">
    <h2>Time Off Requests</h2>
  </div>

  <div class="timeoff-request-accrual-container content-container content-controls">
    <div class="content-container"><h1>Active Time Off Requests</h1></div>


    <time-off-request-list data="requests"></time-off-request-list>


    <!--Add button to submit a new request-->
    <button class="add-button" ng-click="newRequest()">
      &nbsp;New Time Off Request&nbsp;
    </button>
  </div>
</div>
