<div class="content-container content-controls">
  <p class="content-info">Filter By Year &nbsp;
    <select ng-model="selectedYear" ng-change="getAccSummaries(selectedYear)"
            ng-options="year for year in activeYears">
    </select>
  </p>
</div>

<ess-notification ng-show="!isLoading() && error !== null" level="warn"
                  title="{{error.title}}" message="{{error.message}}">
</ess-notification>

<div ng-show="isTe">
  <jsp:include page="te-accruals.jsp"/>
</div>

<div loader-indicator class="loader" ng-show="isLoading()"></div>

<div class="content-container" ng-hide="isLoading()">
  <p class="content-info" ng-hide="accSummaries[selectedYear].length > 0">
    No historical accrual records exist for this year.
    If it is early in the year they may not have been created yet.
  </p>
  <div ng-show="accSummaries[selectedYear].length > 0">
    <p class="content-info">
      Summary of historical accrual records.
      Click a row to view or print a detailed summary of projected accrual hours.
    </p>
    <div class="padding-10">
      <table class="detail-acc-history-table" float-thead="floatTheadOpts"
             float-thead-enabled="true" ng-model="accSummaries">
        <thead>
        <tr>
          <th colspan="2">Pay Period</th>
          <th colspan="4" class="">Personal Hours</th>
          <th colspan="5" class="">Vacation Hours</th>
          <th colspan="5" class="">Sick Hours</th>
        </tr>
        <tr>
          <th>#</th>
          <th>End Date</th>
          <th class="personal">Accrued</th>
          <th class="personal">Used</th>
          <th class="personal">Used Ytd</th>
          <th class="personal">Avail</th>
          <th class="vacation">Rate</th>
          <th class="vacation">Accrued</th>
          <th class="vacation">Used</th>
          <th class="vacation">Used Ytd</th>
          <th class="vacation">Avail</th>
          <th class="sick">Rate</th>
          <th class="sick">Accrued</th>
          <th class="sick">Used</th>
          <th class="sick">Used Ytd</th>
          <th class="sick">Avail</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="record in accSummaries[selectedYear]"
            ng-class="{'highlighted': record.payPeriod.current}"
            title="Open a Printable View for this Record"
            ng-click="viewDetails(record)"
        >
          <td>{{record.payPeriod.payPeriodNum}}</td>
          <td>{{record.payPeriod.endDate | moment:'MM/DD/YYYY'}}</td>
          <td class="accrual-hours personal">{{record.personalAccruedYtd}}</td>
          <td class="accrual-hours personal">{{record.biweekPersonalUsed}}</td>
          <td class="accrual-hours personal">{{record.personalUsed}}</td>
          <td class="accrual-hours available-hours personal">{{record.personalAvailable}}</td>
          <td class="accrual-hours vacation">{{record.vacationRate}}</td>
          <td class="accrual-hours vacation">{{record.vacationAccruedYtd + record.vacationBanked}}</td>
          <td class="accrual-hours vacation">{{record.biweekVacationUsed}}</td>
          <td class="accrual-hours vacation">{{record.vacationUsed}}</td>
          <td class="accrual-hours available-hours vacation">{{record.vacationAvailable}}</td>
          <td class="accrual-hours sick">{{record.sickRate}}</td>
          <td class="accrual-hours sick">{{record.sickAccruedYtd}}</td>
          <td class="accrual-hours sick">{{record.biweekSickEmpUsed + record.biweekSickFamUsed}}</td>
          <td class="accrual-hours sick">{{record.sickEmpUsed + record.sickFamUsed}}</td>
          <td class="accrual-hours available-hours sick">{{record.sickAvailable}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>
