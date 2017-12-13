
<div class="content-container content-controls"
     ng-show="isUser()">
  <p class="content-info">Filter By Year &nbsp;
    <select ng-model="selectedYear"
            ng-options="year for year in activeYears">
    </select>
  </p>
</div>

<div loader-indicator class="loader" ng-show="isEmpLoading()"></div>

<div class="content-container" ng-hide="isEmpLoading()">
  <div ng-show="selectedYear">
    <div class="content-container content-controls" ng-hide="isUser()">
      <h1 ng-if="!(hideTitle || isUser())">
        {{empSupInfo.empFirstName}}
        {{empSupInfo.empLastName | possessive}}
        Allowance History
      </h1>
      <p class="content-info" style="margin-bottom: 0">
        Filter By Year &nbsp;
        <select ng-model="selectedYear" ng-options="year for year in activeYears"></select>
      </p>
    </div>

    <div loader-indicator class="sm-loader no-collapse" ng-show="isLoading()"></div>

    <p class="content-info" ng-hide="periodAllowanceUsages[selectedYear].length > 0 || isLoading()">
      No allowance usage records exist for this year.
      If it is early in the year they may not have been created yet.
    </p>

    <div ng-show="periodAllowanceUsages[selectedYear].length > 0">
      <p class="content-info">
        Summary of past allowance usage for each pay period.
        Click a row to view or print a detailed summary of allowance usage.
      </p>

      <table class="allowance-table"
             float-thead="floatTheadOpts"
             ng-model="periodAllowanceUsages[selectedYear]">
        <thead>
          <tr>
            <th class="period-no">Period #</th>
            <th class="end-date">End Date</th>
            <th class="used">Used</th>
            <th class="used-ytd">Used YTD</th>
            <th class="total-allowed">Total Allowed</th>
            <th class="est-available">Est Available</th>
          </tr>
        </thead>
        <tbody>
          <tr ng-repeat="perUsage in periodAllowanceUsages[selectedYear]"
              title="Print period allowance usage"
              ng-click="selectPeriodUsage(perUsage)"
              ns-popover
              ns-popover-template="print-allowance-usage"
              ns-popover-timeout="60"
              ns-popover-theme="ns-popover-tooltip-theme"
              ns-popover-placement="top">
            <td class="period-no" ng-bind="perUsage.payPeriod.payPeriodNum"></td>
            <td class="end-date" ng-bind="perUsage.payPeriod.endDate | moment:'MM/DD/YYYY'"></td>
            <td class="used" ng-bind="perUsage.periodHoursUsed"></td>
            <td class="used-ytd" ng-bind="perUsage.hoursUsed + perUsage.periodHoursUsed | number"></td>
            <td class="total-allowed" ng-bind="perUsage.totalHours | number"></td>
            <td class="est-available" ng-bind-html="getExpectedHours(perUsage) | number"></td>
          </tr>
          <script type="text/ng-template" id="print-allowance-usage">
            <div class="triangle"></div>
            <div class="ns-popover-tooltip print-allowance-usage">
              <h4 class="content-info">Open printable report for period</h4>
              <input type="button" class="time-neutral-button" value="Print"
                     ng-click="printSelectedPerUsage()">
            </div>
          </script>
        </tbody>
      </table>
      <hr>
    </div>
  </div>
</div>
