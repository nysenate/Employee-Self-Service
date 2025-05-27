<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="EmpCheckHistoryCtrl" id="paycheck-history">
  <div class="my-info-hero">
    <h2>Paycheck History</h2>
  </div>

  <div class="content-container content-controls">
    <div class="content-info">
      <div ng-hide="checkHistory.useFiscalYears">
        <label>
          Filter By Year
          <select ng-model="checkHistory.year"
                  ng-options="year for year in checkHistory.recordYears" ng-change="getRecords()"></select>
        </label>
      </div>
      <div ng-show="checkHistory.useFiscalYears">
        <label>
          Filter By Fiscal Year
          <select ng-show="checkHistory.useFiscalYears" ng-model="checkHistory.year"
                  ng-options="((year - 1) + ' - ' + year) for year in checkHistory.recordFiscalYears"
                  ng-change="getRecords()"></select>
        </label>
      </div>
      <div class="fiscal-toggle">
        <label>
          Show Fiscal Year
          <input type="checkbox" ng-model="checkHistory.useFiscalYears" ng-change="onFiscalYearSwitch()">
        </label>
      </div>
    </div>
  </div>

  <div loader-indicator class="loader" ng-show="checkHistory.searching === true"></div>

  <div class="content-container" ng-show="summary.paychecks.length > 0">
    <h1 ng-hide="checkHistory.useFiscalYears">
      {{checkHistory.year}} Paycheck Records
    </h1>
    <h1 ng-show="checkHistory.useFiscalYears">
      {{checkHistory.year - 1}} - {{checkHistory.year}} Fiscal Year Paycheck Records
    </h1>
    <div class="padding-10 scroll-x">
      <table id="paycheck-history-table" class="ess-table">
        <thead>
        <tr>
          <th>Check Date</th>
          <th>Pay Period</th>
          <th class="money-col">Gross</th>
          <th ng-repeat="deduction in summary.deductions" class="money-col">{{deduction.description | formatDeductionHeader}}</th>
          <th ng-if="displayDirectDepositColumn()" class="money-col">Direct Deposit</th>
          <th ng-if="displayCheckColumn()" class="money-col">Check</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="paycheck in summary.paychecks">
          <td>{{paycheck.checkDate | moment:'l'}}</td>
          <td>{{paycheck.payPeriod}}</td>
          <td ng-class="{bold: isSignificantChange(paycheck.grossIncome, summary.paychecks[$index - 1].grossIncome)}"
              class="money-col">
            {{paycheck.grossIncome | currency}}
          </td>
          <td ng-repeat="deduction in paycheck.deductions" class="money-col"
              ng-class="{bold: isSignificantChange(deduction.amount, summary.paychecks[$parent.$index - 1].deductions[$index].amount)}">
            {{deduction.amount | currency}}
          </td>
          <td ng-class="{bold: isSignificantChange(paycheck.directDepositAmount, summary.paychecks[$index - 1].directDepositAmount)}"
              ng-if="displayDirectDepositColumn()" class="money-col">
            {{paycheck.directDepositAmount | currency}}
          </td>
          <td ng-class="{bold: isSignificantChange(paycheck.checkAmount, summary.paychecks[$index - 1].checkAmount)}"
              ng-if="displayCheckColumn()" class="money-col">
            {{paycheck.checkAmount | currency}}
          </td>
        </tr>
        <tr class="yearly-totals">
          <td>Annual Totals</td>
          <td colspan="1"></td>
          <td class="money-col">{{summary.grossIncomeTotal | currency}}</td>
          <td class="money-col" ng-repeat="deduction in summary.deductions">{{summary.deductionTotals[deduction.code] || 0 | currency}}</td>
          <td class="money-col" ng-if="displayDirectDepositColumn()">{{summary.directDepositTotal | currency}}</td>
          <td class="money-col" ng-if="displayCheckColumn()">{{summary.checkAmountTotal | currency}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>

  <%-- No results notification --%>
  <div class="content-container" ng-show="checkHistory.searching === false && summary.paychecks.length === 0">
    <h1>No pay checks found for {{checkHistory.year}}</h1>
  </div>
  <div modal-container></div>
</div>
