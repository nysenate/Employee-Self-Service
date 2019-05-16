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
        <p class="content-info">
          Click on a pending acknowledgment to initiate an acknowledgment on behalf of the employee.
        </p>

        <div class="personnel-task-display">

          <h2>Pending Acknowledgments</h2>
          <ul class="personnel-task-incomplete-list">
            <li ng-show="state.unacknowledged.length == 0"><p>The employee has no pending acknowledgments</p></li>
            <li ng-repeat="status in state.unacknowledged | orderBy:'status.ackDoc.effectiveDateTime'">
              <a ng-click="initiatePersonnelAck(status)"
                 title="Click to record an acknowledgment on behalf of the employee.">
                <p class="personnel-task-list-item">
                  <span class="icon-text-document"></span>
                  <span ng-bind="status.ackDoc.title"></span>
                </p>
              </a>
            </li>
          </ul>

          <h2>Completed Acknowledgments</h2>
          <ul>
            <li ng-show="state.acknowledged.length == 0"><p>The employee has no completed acknowledgments</p></li>
            <li ng-repeat="status in state.acknowledged | orderBy:'status.ackDoc.effectiveDateTime'">
              <p class="personnel-task-list-item">
                <span class="icon-check"></span>
                <span ng-bind="status.ackDoc.title"></span>
                <span class="personnel-task-list-item-action-date">
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
  <div modal-container>
    <modal modal-id="personnel-acknowledge-prompt">
      <div confirm-modal rejectable="true" title="Submit Acknowledgment for Employee"
           resolve-button="Submit"
           reject-button="Cancel" reject-class="time-neutral-button">
        <div class="content-info acknowledgment-text">
          You are attempting to submit the following acknowledgment:
          <ul class="bold-text">
            <li>Employee: {{selectedEmp.fullName}}</li>
            <li>Year: {{state.selectedDoc.effectiveDateTime | moment:'YYYY'}}</li>
            <li>Document: {{state.selectedDoc.title}}</li>
          </ul>
          By pressing "Submit" you are verifying that this employee has submitted
          a signed paper form acknowledging receipt of the policy/document.
        </div>
      </div>
    </modal>
    <modal modal-id="personnel-acknowledge-success">
      <div confirm-modal title="Acknowledgment Complete"
           resolve-button="Ok" resolve-class="time-neutral-button">
        <p>
          You have successfully acknowledged
          <span class="ack-doc-title">{{state.selectedDoc.title}}</span>
          on behalf of {{selectedEmp.fullName}}.
        </p>
      </div>
    </modal>
  </div>
</section>