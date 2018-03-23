<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="EmpAckDocReportCtrl">

  <div class="my-info-hero">
    <h2>Employee Report</h2>
  </div>

  <div class="content-container">

    <employee-search selected-emp="selectedEmp"></employee-search>

    <div id="empReportStatus" ng-if="ackStatusesReady">

      <ul class="ack-doc-list">

        <li ng-repeat="status in displayAckStatuses">
          <p ng-bind="status"></p>
        </li>

      </ul>

    </div>
  </div>

</section>