<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="EmpCheckHistoryCtrl">
  <div class="my-info-hero">
    <h2>Paycheck History</h2>
  </div>

  <div class="content-container content-controls">
    <p class="content-info">
      Filter By Year&nbsp;
      <select ng-model="checkHistory.year" ng-options="year for year in checkHistory.recordYears" ng-change="getRecords()"></select>
    </p>
  </div>

  <div loader-indicator class="loader" ng-show="checkHistory.searching === true"></div>

  <div class="content-container" ng-show="paychecks.length > 0">
    <h1>{{checkHistory.year}} Paycheck Records</h1>
    <div class="padding-10 scroll-x">
      <table id="paycheck-history-table" class="ess-table" ng-model="paychecks">
        <thead>
        <tr>
          <th>Check Date</th>
          <th>Pay Period</th>
          <th>Gross</th>
          <th ng-repeat="col in deductionCols">{{col | formatDeductionHeader}}</th>
          <th ng-if="dirDepositPresent">Direct Deposit</th>
          <th ng-if="checkPresent">Check</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="paycheck in paychecks">
          <td>{{paycheck.checkDate | moment:'l'}}</td>
          <td>{{paycheck.payPeriod}}</td>
          <td ng-class="{bold: isSignificantChange(paycheck.grossIncome, paychecks[$index - 1].grossIncome)}">
            {{paycheck.grossIncome | currency}}
          </td>
          <td ng-repeat="col in deductionCols"
              ng-class="{bold: isSignificantChange(paycheck.deductions[col].amount, paychecks[$parent.$index - 1].deductions[col].amount)}">
            {{paycheck.deductions[col].amount | currency}}
          </td>
          <td ng-class="{bold: isSignificantChange(paycheck.directDepositAmount, paychecks[$index - 1].directDepositAmount)}"
              ng-if="dirDepositPresent">
            {{paycheck.directDepositAmount | currency}}
          </td>
          <td ng-class="{bold: isSignificantChange(paycheck.checkAmount, paychecks[$index - 1].checkAmount)}"
              ng-if="checkPresent">
            {{paycheck.checkAmount | currency}}
          </td>
        </tr>
        <tr class="yearly-totals">
          <td>Annual Totals</td>
          <td colspan="1"></td>
          <td>{{ytd.gross | currency}}</td>
          <td ng-repeat="col in deductionCols">{{ytd[col] || 0 | currency}}</td>
          <td ng-if="dirDepositPresent">{{ytd.directDeposit | currency}}</td>
          <td ng-if="checkPresent">{{ytd.check | currency}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>

  <%-- No results notification --%>
  <div class="content-container" ng-show="checkHistory.searching === false && paychecks.length === 0">
    <h1>No pay checks found for {{checkHistory.year}}</h1>
  </div>
  <div modal-container></div>
</div>
