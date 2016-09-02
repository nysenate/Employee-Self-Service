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

  <div class="content-container" ng-show="paychecks.length > 0">
    <h1 ng-hide="checkHistory.useFiscalYears">
      {{checkHistory.year}} Paycheck Records
    </h1>
    <h1 ng-show="checkHistory.useFiscalYears">
      {{checkHistory.year - 1}} - {{checkHistory.year}} Fiscal Year Paycheck Records
    </h1>
    <div class="padding-10 scroll-x">
      <table id="paycheck-history-table" class="ess-table" ng-model="paychecks">
        <thead>
        <tr>
          <th>Check Date</th>
          <th>Pay Period</th>
          <th class="money-col">Gross</th>
          <th ng-repeat="col in deductionCols" class="money-col">{{col | formatDeductionHeader}}</th>
          <th ng-if="dirDepositPresent" class="money-col">Direct Deposit</th>
          <th ng-if="checkPresent" class="money-col">Check</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="paycheck in paychecks">
          <td>{{paycheck.checkDate | moment:'l'}}</td>
          <td>{{paycheck.payPeriod}}</td>
          <td ng-class="{bold: isSignificantChange(paycheck.grossIncome, paychecks[$index - 1].grossIncome)}"
              class="money-col">
            {{paycheck.grossIncome | currency}}
          </td>
          <td ng-repeat="col in deductionCols" class="money-col"
              ng-class="{bold: isSignificantChange(paycheck.deductions[col].amount, paychecks[$parent.$index - 1].deductions[col].amount)}">
            {{paycheck.deductions[col].amount | currency}}
          </td>
          <td ng-class="{bold: isSignificantChange(paycheck.directDepositAmount, paychecks[$index - 1].directDepositAmount)}"
              ng-if="dirDepositPresent" class="money-col">
            {{paycheck.directDepositAmount | currency}}
          </td>
          <td ng-class="{bold: isSignificantChange(paycheck.checkAmount, paychecks[$index - 1].checkAmount)}"
              ng-if="checkPresent" class="money-col">
            {{paycheck.checkAmount | currency}}
          </td>
        </tr>
        <tr class="yearly-totals">
          <td>Annual Totals</td>
          <td colspan="1"></td>
          <td class="money-col">{{ytd.gross | currency}}</td>
          <td class="money-col" ng-repeat="col in deductionCols">{{ytd[col] || 0 | currency}}</td>
          <td class="money-col" ng-if="dirDepositPresent">{{ytd.directDeposit | currency}}</td>
          <td class="money-col" ng-if="checkPresent">{{ytd.check | currency}}</td>
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
