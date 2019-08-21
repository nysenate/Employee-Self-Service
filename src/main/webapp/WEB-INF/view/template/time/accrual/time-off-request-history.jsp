<%--
  Created by IntelliJ IDEA.
  User: senate
  Date: 8/13/19
  Time: 5:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  /**
   * This template provides the functionality necessary for employees to view
   * their time off request history.
   */
%>

<div ng-controller="RequestHistoryCtrl">
  <div class="time-attendance-hero">
    <h2>Time Off Request History</h2>
  </div>

  <div class="timeoff-request-accrual-container content-container content-controls">
    <div class="content-container"><h1>Past Time Off Requests</h1></div>


    <time-off-request-list data="requests"></time-off-request-list>

  </div>
</div>
