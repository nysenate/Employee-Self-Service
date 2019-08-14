<%--
  Created by IntelliJ IDEA.
  User: senate
  Date: 8/7/19
  Time: 4:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section ng-controller="RequestApprovalCtrl">
  <div class="time-attendance-hero">
    <h2>Employee Time Off Requests</h2>
  </div>

  <div class="content-container content-controls">
    <div class="content-container"><h1>Time Off Requests Needing Approval</h1></div>

    <time-off-request-approval ng-if="!loadingRequests && !loadingEmployees" active="activeRequests" approve="approvalRequests">
    </time-off-request-approval>


  </div>
</section>
