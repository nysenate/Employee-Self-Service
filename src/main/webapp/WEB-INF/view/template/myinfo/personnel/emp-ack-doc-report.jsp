<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="EmpAckDocReportCtrl">

  <div class="my-info-hero">
    <h2>Employee Report</h2>
  </div>

  <employee-search selected-emp="selectedEmp"></employee-search>

  <div class="content-container">

    <div ng-show="selectedEmp">
      <div loader-indicator class="loader no-collapse" ng-show="state.loadingAcks"></div>

      <div ng-show="!state.loadingAcks">

        <div class="content-controls">
          <p class="content-info">
            View acknowledgments for year:
            <select ng-model="state.selectedYear"
                    ng-options="year for year in state.years">
            </select>
          </p>
        </div>

        <div class="ack-doc-display">

          <h2>Pending Acknowledgments</h2>
          <ul class="unacknowledged-doc-list">
            <li ng-show="state.unacknowledged.length == 0"><p>The employee has no pending acknowledgments</p></li>
            <li ng-repeat="status in state.unacknowledged | orderBy:'status.ackDoc.effectiveDateTime'">
              <p class="ack-doc-list-item">
                <span class="icon-text-document"></span>
                <span ng-bind="status.ackDoc.title"></span>
              </p>
            </li>
          </ul>

          <h2>Completed Acknowledgments</h2>
          <ul>
            <li ng-show="state.acknowledged.length == 0"><p>The employee has no completed acknowledgments</p></li>
            <li ng-repeat="status in state.acknowledged | orderBy:'status.ackDoc.effectiveDateTime'">
              <p class="ack-doc-list-item">
                <span class="icon-check"></span>
                <span ng-bind="status.ackDoc.title"></span>
                <span class="ack-doc-list-item-ack-date">
                  - acknowledged {{status.ack.timestamp | moment:'MMM D, YYYY'}}
                  <span ng-show="status.ack.personnelAcked">(Override)</span>
                </span>
              </p>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</section>