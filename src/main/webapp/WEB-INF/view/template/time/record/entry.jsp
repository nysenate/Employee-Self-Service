<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
/**
 * This template provides all time record entry functionality for both regular/special annual time records
 * as well as temporary time records.
*/
%>
<div ng-controller="RecordEntryController">
  <div class="time-attendance-hero">
    <h2>Attendance Record Entry</h2>
  </div>

  <div ng-show="!state.request.records && state.records.length == 0">
    <% /** If there are no active records for the user, display a warning message indicating such. */ %>
    <div class="margin-10-0" ess-notification level="info" title="No time records available to enter."
         ng-hide="canCreateNextRecord()"
         message="Please contact Senate Personnel at (518) 455-3376 if you require any assistance."></div>
    <div class="next-record-form" ng-show="canCreateNextRecord()">
      <p class="content-info">
        All time records have been submitted up to the current pay period.<br>
        You may enter time for the next pay period by pressing the button below.
      </p>
      <input type="button" class="time-neutral-button"
             title="Create Next Time Record" value="Create Next Time Record"
             ng-click="createNextRecord()">
    </div>
  </div>

  <div id="record-selection-container" class="record-selection-container content-container content-controls"
       ng-show="state.records.length > 0">
    <p class="content-info">Enter a time and attendance record by selecting from the list of active pay periods.</p>
    <% /** Record selection table for cases when there are a few active records to display. */ %>
    <table class="simple-table" ng-if="state.records.length <= 5">
      <thead>
      <tr>
        <th>Select</th>
        <th>Pay Period</th>
        <th>Supervisor</th>
        <th>Period End</th>
        <th>Status</th>
        <th>Last Updated</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="record in state.records" ng-click="$parent.state.iSelectedRecord = $index">
        <td>
          <input type="radio" name="recordSelect" ng-value="$index"
                 ng-model="$parent.state.iSelectedRecord"/>
        </td>
        <td>{{record.payPeriod.startDate | moment:'l'}} - {{record.payPeriod.endDate | moment:'l'}}</td>
        <td>{{record.supervisor.fullName}}</td>
        <td ng-class="{'dark-red': record.isDue === true}">{{record.dueFromNowStr}}</td>
        <td ng-bind-html="record.recordStatus | timeRecordStatus:true"></td>
        <td>
            <span ng-show="record.overallUpdateDate | momentCmp:'=':record.originalDate:'second' | not">
              {{record.overallUpdateDate | moment: 'lll'}}
            </span>
          <span ng-show="record.overallUpdateDate | momentCmp:'=':record.originalDate:'second'">New</span>
        </td>
      </tr>
      </tbody>
    </table>
    <% /** Record selection menu for cases when there are many active records (i.e. temporary employees). */ %>
    <div ng-if="state.records.length > 5">
      <div class="record-selection-menu-details">
        <label class="bold">Record Dates:
          <select class="record-selection-menu" ng-model="state.iSelectedRecord"
                  ng-options="state.records.indexOf(record) as getRecordRangeDisplay(record) for record in state.records"></select>
        </label>
        <span><span class="bold">Supervisor: </span>{{state.records[state.iSelectedRecord].supervisor.fullName}}</span>
        <span><span class="margin-left-10 bold">Status: </span>{{state.records[state.iSelectedRecord].recordStatus | timeRecordStatus}}</span>
        <span class="margin-left-10 bold">Last Updated: </span>
        <span
            ng-show="state.records[state.iSelectedRecord].overallUpdateDate | momentCmp:'=':state.records[state.iSelectedRecord].originalDate:'second' | not">
            {{state.records[state.iSelectedRecord].overallUpdateDate | moment: 'lll'}}
      </span>
        <span
            ng-show="state.records[state.iSelectedRecord].overallUpdateDate | momentCmp:'=':state.records[state.iSelectedRecord].originalDate:'second'">New</span>
      </div>
    </div>
  </div>

  <div loader-indicator class="loader" ng-show="state.request.records"></div>

  <% /** Display a warning for previously unsubmitted records */ %>
  <div ess-notification level="warn" title="Earlier Unsubmitted Records"
       class="margin-top-20 margin-bottom-20" ng-show="errorTypes.record.prevUnsubmittedRecord"
       message="This record cannot be submitted until all previous annual salary records are submitted.">
  </div>

  <% /** Display an error message if there are notes for a disapproved time record. */ %>
  <div ess-notification level="error" title="Time record requires correction"
       message="{{state.records[state.iSelectedRecord].initialRemarks}}" class="margin-top-20"
       ng-show="state.records[state.iSelectedRecord].recordStatus === 'DISAPPROVED' ||
                state.records[state.iSelectedRecord].recordStatus === 'DISAPPROVED_PERSONNEL'">
  </div>

  <% /** Accruals and Time entry for regular/special annual time record entries. */ %>
  <div class="content-container" ng-show="!state.request.records && state.records[state.iSelectedRecord].timeEntries">
    <p class="content-info margin-0">All hours available need approval from appointing authority.</p>
    <div ess-notification level="warn" title="Record with multiple pay types" class="margin-10"
         ng-if="state.annualEntries && state.tempEntries">
      <p>
        There was a change in pay type during the time covered by this record.<br>
        Record days have been split into two separate entry tables, one for Regular/Special Annual pay, another for
        Temporary pay
      </p>
    </div>
    <form id="timeRecordForm" method="post" action="">

      <!-- Annual Entry Form -->
      <div class="ra-sa-entry" ng-if="state.annualEntries">
        <h1 class="time-entry-table-title" ng-if="state.tempEntries">Regular/Special Annual Pay Entries</h1>
        <accrual-bar accruals="state.accrual" loading="state.request.accruals"></accrual-bar>
        <hr class="margin-top-10" />
        <% /** Display an error message if part of the time record is invalid. */ %>
        <div ess-notification level="error" title="Time record has errors"
             message="" class="time-entry-error-box margin-top-20"
             ng-show="selRecordHasRaSaErrors()">
          <ul>
            <li ng-show="errorTypes.raSa.workHoursInvalidRange">Work hours must be between 0 and 24.</li>
            <li ng-show="errorTypes.raSa.holidayHoursInvalidRange">
              Holiday hours must be at least 0 and may not exceed hours granted for the holiday
            </li>
            <li ng-show="errorTypes.raSa.vacationHoursInvalidRange">Vacation hours must be between 0 and 12.</li>
            <li ng-show="errorTypes.raSa.personalHoursInvalidRange">Personal hours must be between 0 and 12.</li>
            <li ng-show="errorTypes.raSa.sickEmpHoursInvalidRange">Employee sick hours must be between 0 and 12.</li>
            <li ng-show="errorTypes.raSa.sickFamHoursInvalidRange">Family sick hours must be between 0 and 12.</li>
            <li ng-show="errorTypes.raSa.miscHoursInvalidRange">Misc hours must be between 0 and 12.</li>
            <li ng-show="errorTypes.raSa.totalHoursInvalidRange">Total hours must be between 0 and 24.</li>
            <li ng-show="errorTypes.raSa.notEnoughVacationTime">Vacation hours recorded exceeds hours available.</li>
            <li ng-show="errorTypes.raSa.notEnoughPersonalTime">Personal hours recorded exceeds hours available.</li>
            <li ng-show="errorTypes.raSa.notEnoughSickTime">Sick hours recorded exceeds hours available.</li>
            <li ng-show="errorTypes.raSa.noMiscTypeGiven">A Misc type must be given when using Miscellaneous hours.</li>
            <li ng-show="errorTypes.raSa.noMiscHoursGiven">
              Miscellaneous hours must be present when a Misc type is selected.
            </li>
            <li ng-show="errorTypes.raSa.noMiscType2Given">A Misc type must be given when using Miscellaneous hours.</li>
            <li ng-show="errorTypes.raSa.noMisc2HoursGiven">
              Miscellaneous hours must be present when a Misc type is selected.
            </li>
            <li ng-show="errorTypes.raSa.halfHourIncrements">Hours must be in increments of 0.5</li>
            <li ng-show="errorTypes.raSa.notEnoughMiscTime" ng-repeat="data in state.miscLeaveUsageErrors">
              Your total of {{data.hoursUsed}} {{data.shortname}} hours
              exceeds the limit of {{data.hoursRemaining}} for the period {{data.range}}
            </li>
            <li ng-show="errorTypes.raSa.notEnoughMisc2Time" ng-repeat="data in state.miscLeaveUsageErrors">
              Your total of {{data.hoursUsed}} {{data.shortname}} hours
              exceeds the limit of {{data.hoursRemaining}} for the period {{data.range}}
            </li>
          </ul>
        </div>
        <table class="ess-table time-record-entry-table" id="ra-sa-time-record-table"
               ng-model="state.records[state.iSelectedRecord].timeEntries">
          <thead>
          <tr>
            <th>Date</th>
            <th>Work</th>
            <th>Holiday</th>
            <th>Vacation</th>
            <th>Personal</th>
            <th>Sick Emp</th>
            <th>Sick Fam</th>
            <th>Misc</th>
            <th>Misc Type</th>
            <th ng-show="state.miscEntered">Misc 2</th>
            <th ng-show="state.miscEntered">Misc 2 Type</th>
            <th>Total</th>
          </tr>
          </thead>
          <tbody record-validator validate="preValidation()" record="state.records[state.iSelectedRecord]">
          <tr class="time-record-row highlight-first"
              ng-repeat="(i,entry) in regRecords = (state.records[state.iSelectedRecord].timeEntries | filter:{payType: '!TE'})"
              ng-class="{'weekend': isWeekend(entry.date), 'dummy-entry': entry.dummyEntry}"
              ng-init="numRecs = regRecords.length"
              >
            <td class="date-column">{{entry.date | moment:'ddd M/D/YYYY'}}</td>
            <td entry-validator validate="entryValidators.raSa.workHours(entry)">
              <input type="number" ng-change="setDirty(entry)" time-record-input class="hours-input"
                     placeholder="--" step=".5" min="0" max="24" ng-disabled="entry.date | momentCmp:'>':'now':'day'"
                     ng-model="entry.workHours" tabindex="{{(entry.total < 7 || getSelectedRecord().focused) ? 1 : -1}}"
                     name="numWorkHours"/>
            </td>
            <td entry-validator validate="entryValidators.raSa.holidayHours(entry)">
              <input id="{{entry.date + '-holidayHours'}}"
                  type="number" ng-change="setDirty(entry)" time-record-input class="hours-input"
                  ng-readonly="!isHoliday(entry)" placeholder="{{isHoliday(entry) ? '--' : ''}}"
                  step=".5" min="0" max="{{getHolidayHours(entry)}}" ng-model="entry.holidayHours"
                  name="numHolidayHours"
                  tabindex="{{isHoliday(entry) ? accrualTabIndex.holiday(entry) : -1}}"/>
            </td>
            <td entry-validator validate="entryValidators.raSa.vacationHours(entry)">
              <input id="{{entry.date + '-vacationHours'}}"
                     type="number" ng-change="setDirty(entry)" time-record-input class="hours-input"
                     placeholder="--" step=".5" min="0" max="12"
                     ng-model="entry.vacationHours" name="numVacationHours"
                     tabindex="{{accrualTabIndex.vacation(entry)}}"/>
            </td>
            <td entry-validator validate="entryValidators.raSa.personalHours(entry)">
              <input id="{{entry.date + '-personalHours'}}"
                     type="number" ng-change="setDirty(entry)" time-record-input class="hours-input"
                     placeholder="--" step=".5" min="0" max="12"
                     tabindex="{{accrualTabIndex.personal(entry)}}"
                     ng-model="entry.personalHours" name="numPersonalHours"/>
            </td>
            <td entry-validator validate="entryValidators.raSa.sickEmpHours(entry)">
              <input id="{{entry.date + '-sickEmpHours'}}"
                     type="number" ng-change="setDirty(entry)" time-record-input class="hours-input"
                     placeholder="--" step=".5" min="0" max="12"
                     tabindex="{{accrualTabIndex.sickEmp(entry)}}"
                     ng-model="entry.sickEmpHours" name="numSickEmpHours"/>
            </td>
            <td entry-validator validate="entryValidators.raSa.sickFamHours(entry)">
              <input id="{{entry.date + '-sickFamHours'}}"
                     type="number" ng-change="setDirty(entry)" time-record-input class="hours-input"
                     placeholder="--" step=".5" min="0" max="12"
                     tabindex="{{accrualTabIndex.sickFam(entry)}}"
                     ng-model="entry.sickFamHours" name="numSickFamHours"/>
            </td>
            <td entry-validator validate="entryValidators.raSa.miscHours(entry)">
              <input id="{{entry.date + '-miscHours'}}"
                     type="number" ng-change="setDirty(entry)" time-record-input class="hours-input"
                     placeholder="--" step=".5" min="0" max="12"
                     tabindex="{{accrualTabIndex.misc(entry)}}"
                     ng-model="entry.miscHours" name="numMiscHours"/>
            </td>
            <td entry-validator validate="entryValidators.raSa.miscType(entry)">
              <select id="{{entry.date + '-miscType'}}" style="font-size:.9em;" name="miscHourType"
                      ng-model="entry.miscType" ng-change="setDirty(entry)"
                      tabindex="{{isFieldSelected(entry, 'miscType') || entry.miscHours ? 1 : -1}}"
                      ng-options="miscLeave.type as miscLeave.shortName for miscLeave in state.miscLeaves | filter:getMiscLeavePredicate(entry.date)">
                <option value="">No Misc Hours</option>
              </select>
            </td>
            <td ng-show="state.miscEntered" entry-validator validate="entryValidators.raSa.misc2Hours(entry)">
              <input id="{{entry.date + '-misc2Hours'}}"
                     type="number" ng-change="setDirty(entry)" time-record-input class="hours-input"
                     placeholder="--" step=".5" min="0" max="12"
                     tabindex="{{accrualTabIndex.misc(entry)}}"
                     ng-model="entry.misc2Hours" name="numMisc2Hours"/>
            </td>
            <td ng-show="state.miscEntered" entry-validator validate="entryValidators.raSa.miscType2(entry)">
              <select id="{{entry.date + '-miscType2'}}" style="font-size:.9em;" name="miscHourType2"
                      ng-model="entry.miscType2" ng-change="setDirty(entry)"
                      tabindex="{{isFieldSelected(entry, 'miscType2') || entry.misc2Hours ? 1 : -1}}"
                      ng-options="miscLeave.type as miscLeave.shortName for miscLeave in state.miscLeaves | filter:getMiscLeavePredicate(entry.date)">
                <option value="">No Misc Hours</option>
              </select>
            </td>
            <td class="text-align-center" entry-validator validate="entryValidators.raSa.totalHours(entry)">
              <span>{{entry.total | number}}</span>
            </td>
          </tr>
          <tr class="time-totals-row">
            <td><span ng-if="state.tempEntries">RA/SA</span> Record Totals</td>
            <td>{{state.totals.raSaWorkHours}}</td>
            <td>{{state.totals.holidayHours}}</td>
            <td>{{state.totals.vacationHours}}</td>
            <td>{{state.totals.personalHours}}</td>
            <td>{{state.totals.sickEmpHours}}</td>
            <td>{{state.totals.sickFamHours}}</td>
            <td>{{state.totals.miscHours}}</td>
            <td></td>
            <td ng-show="state.miscEntered">{{state.totals.misc2Hours}}</td>
            <td ng-show="state.miscEntered"></td>
            <td>{{state.totals.raSaTotal}}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- Temporary Entry Form -->
      <div class="te-entry" ng-if="state.tempEntries">
        <h1 class="time-entry-table-title" ng-if="state.annualEntries">Temporary Pay Entries</h1>
        <allowance-bar allowance="state.allowances[state.selectedYear]"
                       temp-work-hours="state.totals.tempWorkHours"
                       loading="state.request.allowances">
        </allowance-bar>
        <hr/>
        <% /** Display an error message if part of the time record is invalid. */ %>
        <div ess-notification level="error" title="Time record has errors"
             message="" class="time-entry-error-box margin-top-20"
             ng-show="selRecordHasTeErrors()">
          <ul>
            <li ng-show="errorTypes.te.workHoursInvalidRange">Work hours must be between 0 and 24</li>
            <li ng-show="errorTypes.te.notEnoughWorkHours">Work hours recorded exceeds available work hours</li>
            <li ng-show="errorTypes.te.fifteenMinIncrements">Work hours must be in increments of 0.25</li>
            <li ng-show="errorTypes.te.noComment">
              Must enter start and end work times for all work blocks during the entered work hours.
            </li>
            <li ng-show="errorTypes.te.noWorkHoursForComment">Commented entries must accompany 0 or more work hours
              entered
            </li>
          </ul>
        </div>
        <table class="ess-table time-record-entry-table" id="te-time-record-table">
          <thead>
          <tr>
            <th>Date</th>
            <th>Work</th>
            <th>Work Time Description / Comments</th>
          </tr>
          </thead>
          <tbody record-validator validate="preValidation()" record="state.records[state.iSelectedRecord]">
          <tr class="time-record-row highlight-first"
              ng-repeat="(i,entry) in state.records[state.iSelectedRecord].timeEntries | filter:{payType: 'TE'}"
              ng-class="{'weekend': isWeekend(entry.date), 'dummy-entry': entry.dummyEntry}">
            <td class="date-column">{{entry.date | moment:'ddd M/D/YYYY'}}</td>
            <td entry-validator validate="entryValidators.te.workHours(entry)">
              <input type="number" ng-change="setDirty(entry)" time-record-input class="hours-input"
                     placeholder="--" step="0.25" min="0" max="24" ng-disabled="entry.date | momentCmp:'>':'now':'day'"
                     ng-model="entry.workHours" name="numWorkHours" tabindex="1"/>
            </td>
            <td entry-validator validate="entryValidators.te.comment(entry)" class="entry-comment-col">
                <textarea maxlength="150" ng-change="setDirty(entry)" class="entry-comment" text-auto-height
                          text="entry.empComment"
                          ng-model="entry.empComment" name="entryComment"
                          tabindex="{{entry.workHours ? 1 : -1}}"></textarea>
            </td>
          </tr>
          <tr class="time-totals-row">
            <td><span ng-if="state.annualEntries">TE</span> Record Totals</td>
            <td>{{state.totals.tempWorkHours}}</td>
            <td></td>
          </tr>
          </tbody>
        </table>
      </div>
      <div class="save-record-container">
        <div class="record-remarks-container">
          <label for="remarks-text-area">Notes / Remarks</label>
          <textarea id="remarks-text-area" class="record-remarks-text-area" tabindex="1"
                    ng-model="state.records[state.iSelectedRecord].remarks" ng-change="setDirty()">
        </textarea>
        </div>
        <div class="float-right">
          <input ng-click="saveRecord(false)" class="submit-button" type="button" value="Save Record"
                 ng-disabled="!state.records[state.iSelectedRecord].dirty || !recordValid()"
                 tabindex="{{(state.records[state.iSelectedRecord].dirty && recordValid()) ? 1 : -1}}"/>
          <input ng-click="saveRecord(true)" class="submit-button" type="button" value="Submit Record"
                 ng-disabled="!recordSubmittable()" tabindex="{{recordSubmittable() ? 1 : -1}}"/>
        </div>
        <div class="clearfix"></div>
      </div>
    </form>
  </div>

  <div modal-container>
    <modal modal-id="save-progress">
      <div progress-modal title="Saving time record..."></div>
    </modal>
    <modal modal-id="submit-progress">
      <div progress-modal title="Saving and submitting time record..."></div>
    </modal>
    <modal modal-id="post-save">
      <div confirm-modal rejectable="true"
           title="Your time record has been saved."
           confirm-message="What would you like to do next?"
           resolve-button="Go back to ESS" reject-button="Log out of ESS">
      </div>
    </modal>
    <modal modal-id="post-submit">
      <div confirm-modal rejectable="true"
           title="Your time record has been submitted."
           confirm-message="What would you like to do next?"
           resolve-button="Go back to ESS" reject-button="Log out of ESS">
      </div>
    </modal>
    <modal modal-id="submit-ack">
      <div record-submit-ack-modal></div>
    </modal>
    <modal modal-id="expectedhrs-dialog">
      <div record-expected-hours-modal></div>
    </modal>
    <modal modal-id="futureenddt-dialog">
      <div confirm-modal rejectable="true"
           title="Timesheet with a future end date"
           confirm-message="Warning: You are attempting to submit a timesheet with a future end date"
           resolve-button="Proceed" reject-button="Cancel">
      </div>
    </modal>
    <modal modal-id="record-modified-dialog">
      <div confirm-modal
           title="An external modification was detected for the submitted record"
           resolve-button="Refresh Records">
        <p>
          Please hit the 'Refresh Records' button to get the latest version of the record<br>
          and resubmit if applicable.<br>
          If this continues to occur, please
          <a href="" ng-click="rejectModal()">
            send an error report.
          </a>
        </p>
      </div>
    </modal>
    <modal modal-id="unsubmitted-te-warning">
      <div record-unsubmitted-te-modal=></div>
    </modal>
  </div>
</div>