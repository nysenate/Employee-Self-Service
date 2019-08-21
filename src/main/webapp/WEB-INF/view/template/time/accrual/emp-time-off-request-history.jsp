<%--
  Created by IntelliJ IDEA.
  User: senate
  Date: 8/7/19
  Time: 4:07 PM
  To change this template use File | Settings | File Templates.
--%>

<%
  /**
   * This template provides the functionality necessary for supervisors to view
   * their employees' time off request history.
   */
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div div="employee-time-off-request-history">
  <div class="time-attendance-hero">
    <h2>Employee Time Off Request History</h2>
  </div>

  <div class="timeoff-request-accrual-container content-container content-controls">
    <div class="content-container">

      <employee-select selected-emp="selectedEmp" select-subject="Time Off Requests">
      </employee-select>

      <time-off-request-history ng-if="selectedEmp.empId" emp-sup-info="selectedEmp" >
      </time-off-request-history>

    </div>
  </div>
</div>

