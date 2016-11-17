<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="RecordManageCtrl">
  <div class="time-attendance-hero">
    <h2>Review Time Records</h2>
  </div>
  <div ng-show="state.loading">
    <div loader-indicator class="loader"></div>
  </div>
  <div ng-if="!state.loading">
    <div class="content-container content-controls">
      <p class="content-info">View Employees Under &nbsp;
        <select name="supSelect" ng-model="$parent.state.selSupId" ng-change="selectNone()"
            ng-options="getOptionLabel(supId) for supId in state.supIds">
        </select>
      </p>
    </div>

    <section class="content-container" ng-if="state.supRecords[state.selSupId].SUBMITTED">
      <h1 class="teal">T&A Record(s) Needing Approval ({{state.supRecords[state.selSupId]['SUBMITTED'].length}})</h1>
      <p class="content-info">
        Select pending records in the table below and click 'Review Selected Records'<br/>
        at the bottom to review the record details and either approve or reject them.
      </p>
      <ul class="horizontal" style="padding:0;margin:10px">
        <li style="margin-right:10px;"><a ng-click="selectAll('SUBMITTED')">Select All</a></li>
        <li style="margin-right:10px;"><a ng-click="selectNone('SUBMITTED')">Select None</a></li>
      </ul>
      <div supervisor-record-list records="state.supRecords[state.selSupId]['SUBMITTED']"
           sup-id="state.selSupId" selected-indices="state.selectedIndices['SUBMITTED']"></div>
      <div style="padding:.5em;text-align:right;">
        <input type="button" class="submit-button" value="Approve Selected" ng-disabled="hasSelections('SUBMITTED') == false"
               ng-click="approveSelections('SUBMITTED')"/>
        <input type="button" class="time-neutral-button" value="Review Selected"
               ng-click="review('SUBMITTED', true)" ng-disabled="hasSelections('SUBMITTED') == false"/>
      </div>
    </section>

    <div ess-notification ng-if="!state.supRecords[state.selSupId]['SUBMITTED']"
         level="info" title="No time records need action."
         message="There are currently no records that require approval."></div>

    <toggle-panel open="true" ng-if="state.supRecords[state.selSupId]['DISAPPROVED']"
                  label="T&A Records Awaiting Correction By Employee ({{state.supRecords[state.selSupId]['DISAPPROVED'].length}})">
      <p class="content-info">The following records have been rejected and are pending correction by the employee.<br/>
        Once the employee resubmits the record it will appear in the 'Records Needing Approval' section.</p>
      <div class="emp-manage-actions-container" style="float:right;">
        <input type="button" class="submit-button" value="View Selected" ng-disabled="hasSelections('DISAPPROVED') == false"
               ng-click="review('DISAPPROVED', false)"/>
        <input type="button" class="time-neutral-button" value="Email Selected"
               ng-click="remindSelections('DISAPPROVED')" ng-disabled="hasSelections('DISAPPROVED') == false"/>
      </div>
      <ul class="horizontal" style="padding:0;margin:10px;float:left">
        <li style="margin-right:10px;"><a ng-click="selectAll('DISAPPROVED')">Select All</a></li>
        <li style="margin-right:10px;"><a ng-click="selectNone('DISAPPROVED')">Select None</a></li>
      </ul>
      <div supervisor-record-list records="state.supRecords[state.selSupId]['DISAPPROVED']"
           selected-indices="state.selectedIndices['DISAPPROVED']"></div>
      <div class="emp-manage-actions-container">
        <input type="button" class="submit-button" value="View Selected"
               ng-disabled="hasSelections('DISAPPROVED') == false" ng-click="review('DISAPPROVED', false)"/>
        <input type="button" class="time-neutral-button" value="Email Selected"
               ng-click="remindSelections('DISAPPROVED')" ng-disabled="hasSelections('DISAPPROVED') == false"/>
      </div>
    </toggle-panel>

    <toggle-panel open="true" ng-if="state.supRecords[state.selSupId]['NOT_SUBMITTED']"
                  label="T&A Records Not Submitted ({{state.supRecords[state.selSupId]['NOT_SUBMITTED'].length}})">
      <p class="content-info">The records have not yet been submitted by the employee.<br/></p>
      <div class="emp-manage-actions-container" style="float:right;">
        <input type="button" class="submit-button" value="View Selected" ng-disabled="hasSelections('NOT_SUBMITTED') == false"
               ng-click="review('NOT_SUBMITTED', false)"/>
        <input type="button" class="time-neutral-button" value="Email Selected"
               ng-click="remindSelections('NOT_SUBMITTED')" ng-disabled="hasSelections('NOT_SUBMITTED') == false"/>
      </div>
      <ul class="horizontal" style="padding:0;margin:10px;float:left">
        <li style="margin-right:10px;"><a ng-click="selectAll('NOT_SUBMITTED')">Select All</a></li>
        <li style="margin-right:10px;"><a ng-click="selectNone('NOT_SUBMITTED')">Select None</a></li>
      </ul>
      <div supervisor-record-list records="state.supRecords[state.selSupId]['NOT_SUBMITTED']"
           selected-indices="state.selectedIndices['NOT_SUBMITTED']"></div>
      <div class="emp-manage-actions-container">
        <input type="button" class="submit-button" value="View Selected"
               ng-disabled="hasSelections('NOT_SUBMITTED') == false" ng-click="review('NOT_SUBMITTED', false)"/>
        <input type="button" class="time-neutral-button" value="Email Selected"
               ng-click="remindSelections('NOT_SUBMITTED')" ng-disabled="hasSelections('NOT_SUBMITTED') == false"/>
      </div>
    </toggle-panel>

    <br/>
    <hr/>

    <toggle-panel open="false" ng-if="state.supRecords[state.selSupId]['APPROVED']"
          label="T&A Records Pending Approval By Personnel ({{state.supRecords[state.selSupId]['APPROVED'].length}})">
      <p class="content-info">The following records have been recently approved and are awaiting approval by Personnel.</p>
      <div supervisor-record-list records="state.supRecords[state.selSupId]['APPROVED']"></div>
    </toggle-panel>

    <toggle-panel open="false" ng-if="state.supRecords[state.selSupId]['DISAPPROVED_PERSONNEL']"
                  label="T&A Records Rejected By Personnel Awaiting Employee Correction ({{state.supRecords[state.selSupId]['DISAPPROVED_PERSONNEL'].length}})">
      <p class="content-info">The records have been rejected by Personnel and are awaiting re-submission by the employee.</p>
      <div supervisor-record-list records="state.supRecords[state.selSupId]['DISAPPROVED_PERSONNEL']"></div>
    </toggle-panel>

    <toggle-panel open="false" ng-if="state.supRecords[state.selSupId]['SUBMITTED_PERSONNEL']"
           label="T&A Personnel Rejected Records Pending Approval ({{state.supRecords[state.selSupId]['SUBMITTED_PERSONNEL'].length}})">
      <p class="content-info">The following records have been recently submitted to Personnel by employee to correct errors detected by Personnel</p>
      <div supervisor-record-list records="state.supRecords[state.selSupId]['SUBMITTED_PERSONNEL']"></div>
    </toggle-panel>

  </div>
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
  </div>
</div>
