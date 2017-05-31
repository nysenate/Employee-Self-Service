<div loader-indicator class="loader" ng-show="isLoading()"></div>
<section class="content-container" ng-hide="isLoading()">
  <div ng-show="state.recordYears.length > 0">
    <h1 ng-if="!(hideTitle || isUser())">
      {{state.selectedEmp.empFirstName}}
      {{state.selectedEmp.empLastName | possessive}}
      Attendance Records
    </h1>
    <div class="content-controls">
      <p class="content-info" style="margin-bottom:0;">
        View attendance records for year &nbsp;
        <select ng-model="state.selectedRecYear"
                ng-options="year for year in state.recordYears">
        </select>
      </p>
    </div>
    <ess-notification level="warn" title="No Employee Records For {{state.selectedRecYear}}"
                      ng-hide="state.records.length > 0 || isLoading()">
      <p>
        It appears as if the employee has no records for the selected year.<br>
        Please contact Senate Personnel at (518) 455-3376 if you require any assistance.
      </p>
    </ess-notification>
    <div ng-show="state.records.length > 0">
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
          <tr ng-repeat="record in state.records" ng-click="showDetails(record)"
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
          </tbody>
        </table>
      </div>
    </div>

  </div>
  <div ng-hide="state.recordYears.length > 0">
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
</section>
