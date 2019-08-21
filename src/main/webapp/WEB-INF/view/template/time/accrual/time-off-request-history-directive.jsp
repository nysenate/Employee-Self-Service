<%--
  Created by IntelliJ IDEA.
  User: senate
  Date: 8/14/19
  Time: 10:35 AM
  To change this template use File | Settings | File Templates.
--%>

<%
  /**
   * This template provides the functionality necessary for employees to view
   * their time off request history.
   */
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="content-container"
     ng-show="state.recordYears.length > 0">
  <h1 class="content-info">
    {{state.selectedEmp.empFirstName}}
    {{state.selectedEmp.empLastName | possessive}}
    Time Off Requests
  </h1>
  <p class="content-info" style="margin-bottom:0;">
    View time off requests for year &nbsp;
    <select ng-model="state.selectedRecYear"
            ng-options="year for year in state.recordYears">
    </select>
  </p>
</div>
<time-off-request-list data="requests">
</time-off-request-list>