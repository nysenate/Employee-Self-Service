<div loader-indicator class="loader" ng-show="isLoading()"></div>

<div class="content-container"
     ng-show="!isLoading() && state.recordYears.length > 0"
     ng-class="{'content-controls': hideTitle || isUser()}">
  <h1 class="content-info" ng-if="!(hideTitle || isUser())">
    {{state.selectedEmp.empFirstName}}
    {{state.selectedEmp.empLastName | possessive}}
    Attendance Records
  </h1>
  <p class="content-info" style="margin-bottom:0;">
    View attendance records for year &nbsp;
    <select ng-model="state.selectedRecYear"
            ng-options="year for year in state.recordYears">
    </select>
  </p>
</div>

<ess-notification level="warn" title="No Employee Records For {{state.selectedRecYear}}"
                  ng-hide="state.records.employee.length > 0 || state.records.submitted.length > 0 || isLoading()">
  <p>
    It appears as if the employee has no records for the selected year.<br>
    Please contact Senate Personnel at (518) 455-3376 if you require any assistance.
  </p>
</ess-notification>

<div class="content-container" ng-show="!isLoading() && state.records.employee.length > 0">
  <h1>Active Attendance Records</h1>
  <p class="content-info">The following time records are in progress or awaiting submission.
    <br/>
    <span ng-show="linkToEntryPage">You can edit a record by clicking the row.</span>
    <span ng-hide="linkToEntryPage">Click a row to view the in-progress record.</span>
  </p>
  <div class="padding-10">
    <table id="attendance-active-table" class="ess-table attendance-listing-table"
           ng-model="state.records.employee">
      <thead>
      <tr>
        <th>Date Range</th>
        <th>Pay Period</th>
        <th>Status</th>
        <th>Work</th>
        <th>Holiday</th>
        <th>Vacation</th>
        <th>Personal</th>
        <th>Sick Emp</th>
        <th>Sick Fam</th>
        <th>Misc</th>
        <th>Total</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="record in state.records.employee"
          ng-click="showDetails(record)"
          ng-href="{{linkToEntryPage ? entryPageUrl + '?record=' + record.beginDate : '' }}"
          title="Click to view record details" class="e-timesheet">
        <td>{{record.beginDate | moment:'l'}} - {{record.endDate | moment:'l'}}</td>
        <td>{{record.payPeriod.payPeriodNum}}</td>
        <td ng-bind-html="record.recordStatus | timeRecordStatus:true"></td>
        <td>{{record.totals.workHours}}</td>
        <td>{{record.totals.holidayHours}}</td>
        <td>{{record.totals.vacationHours}}</td>
        <td>{{record.totals.personalHours}}</td>
        <td>{{record.totals.sickEmpHours}}</td>
        <td>{{record.totals.sickFamHours}}</td>
        <td>{{record.totals.miscHours}}</td>
        <td>{{record.totals.total}}</td>
      </tr>
      </tbody>
    </table>
  </div>
</div>
<div class="content-container" ng-show="!isLoading() && state.records.submitted.length > 0">
  <h1>Submitted Attendance Records</h1>
  <p class="content-info">
    Time records that have been submitted for pay periods during
    {{state.selectedRecYear}} are listed in the table below.
    <br/>
    You can view details about each pay period by clicking the row.
    <span ng-show="state.paperTimesheetsDisplayed">
            <br>
            <span class="bold">Note:</span>
            Details are unavailable for attendance records entered via paper timesheet (designated by "(paper)" under Status)
          </span>
  </p>
  <div class="padding-10">
    <table id="attendance-history-table" ng-hide="isLoading()" class="ess-table attendance-listing-table">
      <thead>
      <tr>
        <th>Date Range</th>
        <th>Pay Period</th>
        <th>Status</th>
        <th>Work</th>
        <th>Holiday</th>
        <th>Vacation</th>
        <th>Personal</th>
        <th>Sick Emp</th>
        <th>Sick Fam</th>
        <th>Misc</th>
        <th>Total</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="record in state.records.submitted" ng-click="showDetails(record)"
          ng-class="{'e-timesheet': !record.paperTimesheet}"
          title="{{record.paperTimesheet ? '' : 'Click to view record details'}}">
        <td>{{record.beginDate | moment:'l'}} - {{record.endDate | moment:'l'}}</td>
        <td>{{record.payPeriod.payPeriodNum}}</td>
        <td>
          <span ng-bind-html="record.recordStatus | timeRecordStatus:true"></span>
          <span ng-show="record.paperTimesheet">(paper)</span>
        </td>
        <td>{{record.totals.workHours}}</td>
        <td>{{record.totals.holidayHours}}</td>
        <td>{{record.totals.vacationHours}}</td>
        <td>{{record.totals.personalHours}}</td>
        <td>{{record.totals.sickEmpHours}}</td>
        <td>{{record.totals.sickFamHours}}</td>
        <td>{{record.totals.miscHours}}</td>
        <td>{{record.totals.total}}</td>
      </tr>
      <tr style="border-top:2px solid teal;">
        <td colspan="2"></td>
        <td><strong>Annual Totals</strong></td>
        <td><strong>{{state.annualTotals.workHours}}</strong></td>
        <td><strong>{{state.annualTotals.holidayHours}}</strong></td>
        <td><strong>{{state.annualTotals.vacationHours}}</strong></td>
        <td><strong>{{state.annualTotals.personalHours}}</strong></td>
        <td><strong>{{state.annualTotals.sickEmpHours}}</strong></td>
        <td><strong>{{state.annualTotals.sickFamHours}}</strong></td>
        <td><strong>{{state.annualTotals.miscHours}}</strong></td>
        <td><strong>{{state.annualTotals.total}}</strong></td>
        <td></td>
      </tr>
      </tbody>
    </table>
  </div>
</div>

<div ng-hide="isLoading() || state.recordYears.length > 0">
  <ess-notification level="info" title="No Time Record History">
    <p>
      <span ng-show="isUser()">You have</span>
      <span ng-hide="isUser()">{{empSupInfo.fullName}} has</span>
      no time records.
    </p>
    <p ng-show="empSupInfo.senator">
      {{empSupInfo.fullName}} is a Senator and does not currently enter time.
    </p>
  </ess-notification>
</div>
