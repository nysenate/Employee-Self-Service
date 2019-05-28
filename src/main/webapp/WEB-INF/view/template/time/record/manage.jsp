<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="RecordManageCtrl">

  <div class="time-attendance-hero">
    <h2>Review Time Records</h2>
  </div>

  <!-- Supervisor Select -->

  <div class="content-container content-controls">
    <p class="content-info">View Employees Under &nbsp;
      <select name="supSelect" ng-model="state.iSelSup"
              ng-options="state.supervisors.indexOf(sup)
                        as sup.dropDownLabel
                        group by sup.group
                        for sup in state.supervisors">
      </select>
    </p>
  </div>

  <!-- Loader -->

  <div ng-show="state.request.supEmpGroup || state.request.records">
    <div loader-indicator class="loader"></div>
  </div>

  <!-- Record display -->

  <div ng-hide="state.request.supEmpGroup || state.request.records">

    <div ng-if="!hasRecords('SUBMITTED')">
      <div ess-notification
           ng-show="getSelSupEntry().fullEmpGroup"
           level="info" title="No time records need action."
           message="There are currently no records that require approval.">
      </div>
      <div ess-notification
           ng-hide="getSelSupEntry().fullEmpGroup"
           level="info" title="None of {{getSelSupEntry().name.fullName | possessive}} time records need action."
           message="There are currently no records under {{getSelSupEntry().name.fullName}} that require approval.">
      </div>
    </div>

    <!-- Submitted Records -->

    <section class="content-container" ng-if="hasRecords('SUBMITTED')">
      <h1 class="teal">
        T&A Record(s) Needing Approval
        ({{getRecords('SUBMITTED').length}})
      </h1>
      <p class="content-info">
        Select pending records in the table below and click 'Review Selected Records'<br/>
        at the bottom to review the record details and either approve or reject them.
      </p>
      <div class="record-manage-controls">
        <ul class="horizontal">
          <li><a ng-click="selectAll('SUBMITTED')">Select All</a></li>
          <li><a ng-click="selectNone('SUBMITTED')">Select None</a></li>
        </ul>
        <div class="select-actions">
          <input type="button" class="submit-button" value="Approve Selected"
                 ng-disabled="hasSelections('SUBMITTED') == false"
                 ng-click="approveSelections('SUBMITTED')"/>
          <input type="button" class="time-neutral-button" value="Review Selected"
                 ng-click="review('SUBMITTED', true)"
                 ng-disabled="hasSelections('SUBMITTED') == false"/>
        </div>
      </div>
      <div supervisor-record-list
           user-emp-id="state.userEmpId"
           records="getRecords('SUBMITTED')"
           selected-indices="state.selectedIndices.SUBMITTED"></div>
      <div class="record-manage-controls">
        <ul class="horizontal">
          <li><a ng-click="selectAll('SUBMITTED')">Select All</a></li>
          <li><a ng-click="selectNone('SUBMITTED')">Select None</a></li>
        </ul>
        <div class="select-actions">
          <input type="button" class="submit-button" value="Approve Selected"
                 ng-disabled="hasSelections('SUBMITTED') == false"
                 ng-click="approveSelections('SUBMITTED')"/>
          <input type="button" class="time-neutral-button" value="Review Selected"
                 ng-click="review('SUBMITTED', true)"
                 ng-disabled="hasSelections('SUBMITTED') == false"/>
        </div>
      </div>
    </section>

    <!-- Disapproved Records -->

    <toggle-panel open="true" ng-if="hasRecords('DISAPPROVED')"
                  label="T&A Records Awaiting Correction By Employee
                         ({{getRecords('DISAPPROVED').length}})">
      <p class="content-info">
        The following records have been rejected and are pending correction by the employee.<br/>
        Once the employee resubmits the record it will appear in the 'Records Needing Approval' section.
      </p>
      <div class="record-manage-controls">
        <ul class="horizontal">
          <li><a ng-click="selectAll('DISAPPROVED')">Select All</a></li>
          <li><a ng-click="selectNone('DISAPPROVED')">Select None</a></li>
        </ul>
        <div class="select-actions">
          <input type="button" class="submit-button" value="View Selected"
                 ng-disabled="hasSelections('DISAPPROVED') == false"
                 ng-click="review('DISAPPROVED', false)"/>
          <input type="button" class="time-neutral-button" value="Email Selected"
                 ng-click="remindSelections('DISAPPROVED')"
                 ng-disabled="hasSelections('DISAPPROVED') == false"/>
        </div>
      </div>
      <div supervisor-record-list
           records="getRecords('DISAPPROVED')"
           selected-indices="state.selectedIndices.DISAPPROVED"></div>
      <div class="record-manage-controls">
        <ul class="horizontal">
          <li><a ng-click="selectAll('DISAPPROVED')">Select All</a></li>
          <li><a ng-click="selectNone('DISAPPROVED')">Select None</a></li>
        </ul>
        <div class="select-actions">
          <input type="button" class="submit-button" value="View Selected"
                 ng-disabled="hasSelections('DISAPPROVED') == false"
                 ng-click="review('DISAPPROVED', false)"/>
          <input type="button" class="time-neutral-button" value="Email Selected"
                 ng-click="remindSelections('DISAPPROVED')"
                 ng-disabled="hasSelections('DISAPPROVED') == false"/>
        </div>
      </div>
    </toggle-panel>

    <!-- Unsubmitted Records -->

    <toggle-panel open="true" ng-if="hasRecords('NOT_SUBMITTED')"
                  label="T&A Records Not Submitted
                         ({{getRecords('NOT_SUBMITTED').length}})">
      <p class="content-info">
        The records have not yet been submitted by the employee.<br/>
      </p>
      <div class="record-manage-controls">
        <ul class="horizontal">
          <li><a ng-click="selectAll('NOT_SUBMITTED')">Select All</a></li>
          <li><a ng-click="selectNone('NOT_SUBMITTED')">Select None</a></li>
        </ul>
        <div class="select-actions">
          <input type="button" class="submit-button" value="View Selected"
                 ng-disabled="hasSelections('NOT_SUBMITTED') == false"
                 ng-click="review('NOT_SUBMITTED', false)"/>
          <input type="button" class="time-neutral-button" value="Email Selected"
                 ng-click="remindSelections('NOT_SUBMITTED')"
                 ng-disabled="hasSelections('NOT_SUBMITTED') == false"/>
        </div>
      </div>
      <div supervisor-record-list records="getRecords('NOT_SUBMITTED')"
           selected-indices="state.selectedIndices.NOT_SUBMITTED"></div>
      <div class="record-manage-controls">
        <ul class="horizontal">
          <li><a ng-click="selectAll('NOT_SUBMITTED')">Select All</a></li>
          <li><a ng-click="selectNone('NOT_SUBMITTED')">Select None</a></li>
        </ul>
        <div class="select-actions">
          <input type="button" class="submit-button" value="View Selected"
                 ng-disabled="hasSelections('NOT_SUBMITTED') == false"
                 ng-click="review('NOT_SUBMITTED', false)"/>
          <input type="button" class="time-neutral-button" value="Email Selected"
                 ng-click="remindSelections('NOT_SUBMITTED')"
                 ng-disabled="hasSelections('NOT_SUBMITTED') == false"/>
        </div>
      </div>
    </toggle-panel>

    <br/>
    <hr/>

    <!-- Approved Records -->

    <toggle-panel open="false" ng-if="hasRecords('APPROVED')"
                  label="T&A Records Pending Approval By Personnel
                         ({{getRecords('APPROVED').length}})">
      <p class="content-info">
        The following records have been recently approved and are awaiting approval by Personnel.
      </p>
      <div supervisor-record-list records="getRecords('APPROVED')"></div>
    </toggle-panel>

    <!-- Personnel Disapproved Records -->

    <toggle-panel open="false" ng-if="hasRecords('DISAPPROVED_PERSONNEL')"
                  label="T&A Records Rejected By Personnel Awaiting Employee Correction
                         ({{getRecords('DISAPPROVED_PERSONNEL').length}})">
      <p class="content-info">
        The records have been rejected by Personnel and are awaiting re-submission by the employee.
      </p>
      <div supervisor-record-list records="getRecords('DISAPPROVED_PERSONNEL')"></div>
    </toggle-panel>

    <!-- Submitted to Personnel Records -->

    <toggle-panel open="false" ng-if="hasRecords('SUBMITTED_PERSONNEL')"
                  label="T&A Personnel Rejected Records Pending Approval
                         ({{getRecords('SUBMITTED_PERSONNEL').length}})">
      <p class="content-info">
        The following records have been recently submitted to Personnel by an employee
        to correct errors detected by Personnel
      </p>
      <div supervisor-record-list records="getRecords('SUBMITTED_PERSONNEL')"></div>
    </toggle-panel>

  </div>

  <!-- Modals -->

  <div modal-container>
    <modal modal-id="record-details">
      <div record-detail-modal></div>
    </modal>
    <modal modal-id="record-review">
      <div record-review-modal></div>
    </modal>
    <modal modal-id="record-review-reject">
      <div record-review-reject-modal></div>
    </modal>
    <modal modal-id="record-approval-submit">
      <div record-approve-submit-modal></div>
    </modal>
    <modal modal-id="record-reminder-prompt">
      <div record-reminder-prompt-modal></div>
    </modal>
    <modal modal-id="record-reminder-posting">
      <div progress-modal title="Sending Email Reminders"></div>
    </modal>
    <modal modal-id="record-reminder-posted">
      <div confirm-modal title="Email reminders were sent successfully."></div>
    </modal>
    <modal modal-id="record-review-close">
      <div confirm-modal rejectable="true" title="Unsubmitted Records"
           confirm-message="There are one or more reviewed records that have not been submitted. Discard changes?"
           resolve-button="Discard Changes" resolve-class="reject-button"
           reject-button="Resume Review" reject-class="submit-button">
      </div>
    </modal>
    <modal modal-id="inactive-employee-email">
      <div confirm-modal
           title="Inactive Employees"
           level="error">
        <p>
          The requested reminders could not be sent because the following employees are no longer active:
        </p>
        <ul class="inactive-employee-email-list">
          <li ng-repeat="employee in state.inactiveEmps" ng-bind="employee.fullName"></li>
        </ul>
        <p>
          Please contact Senate Personnel at (518) 455-3376.
        </p>
      </div>
    </modal>
  </div>
</div>
