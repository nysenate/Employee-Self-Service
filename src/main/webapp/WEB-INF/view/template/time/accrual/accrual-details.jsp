<div id="accrual-detail-window" class="grid">
  <h3 class="content-info">
    <span ng-show="accruals.computed && !accruals.submitted">Projected</span>
    Accrual Usage for {{accruals.payPeriod.startDate | moment:'YYYY'}}
    Pay Period {{accruals.payPeriod.payPeriodNum +
        (accruals.payPeriod.endYearSplit ? 'A' : accruals.payPeriod.startYearSplit ? 'B' : 0)
    }}
  </h3>
  <div class="accrual-detail-content col-12-12">
    <div class="col-10-12 accrual-detail-table-container">
      <div class="col-6-12">
        <h4 class="content-info">YTD Hours of Service</h4>
        <table class="accrual-detail-table">
          <tbody>
            <tr>
              <td>Expected</td>
              <td ng-bind="accruals.serviceYtdExpected | number:2">
            </tr>
            <tr>
              <td>Actual</td>
              <td ng-bind="accruals.serviceYtd | number:2">
            </tr>
            <tr class="total-row">
              <td>Difference</td>
              <td ng-bind-html="(accruals.serviceYtd - accruals.serviceYtdExpected) | number:2 | hoursDiffHighlighter">
            </tr>
          </tbody>
        </table>
        <h4 class="content-info sick">Sick Hours</h4>
        <table class="accrual-detail-table">
          <tbody>
            <tr>
              <td>Prev. Year Banked</td>
              <td ng-bind="accruals.sickBanked | number:2"></td>
            </tr>
            <tr>
              <td>Accrued YTD</td>
              <td ng-bind="accruals.sickAccruedYtd | number:2"></td>
            </tr>
            <tr>
              <td>Used YTD (Employee)</td>
              <td ng-bind="-accruals.sickEmpUsed | number:2"></td>
            </tr>
            <tr>
              <td>Used YTD (Family)</td>
              <td ng-bind="-accruals.sickFamUsed | number:2"></td>
            </tr>
            <tr class="total-row">
              <td>Available for Period</td>
              <td ng-bind="accruals.sickAvailable | number:2"></td>
            </tr>
            <tr>
              <td>Used in Period (Employee)</td>
              <td ng-bind="-accruals.biweekSickEmpUsed | number:2"></td>
            </tr>
            <tr>
              <td>Used in Period (Family)</td>
              <td ng-bind="-accruals.biweekSickFamUsed | number:2"></td>
            </tr>
            <tr class="total-row">
              <td>Available next Period</td>
              <td ng-bind="accruals.sickAvailable - accruals.biweekSickEmpUsed - accruals.biweekSickFamUsed | number:2">
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="col-6-12">
        <h4 class="content-info personal">Personal Hours</h4>
        <table class="accrual-detail-table">
          <tbody>
            <tr>
              <td>Accrued YTD</td>
              <td ng-bind="accruals.personalAccruedYtd | number:2"></td>
            </tr>
            <tr>
              <td>Used YTD</td>
              <td ng-bind="-accruals.personalUsed | number:2"></td>
            </tr>
            <tr class="total-row">
              <td>Available for Period</td>
              <td ng-bind="accruals.personalAvailable | number:2"></td>
            </tr>
            <tr>
              <td>Used in Period</td>
              <td ng-bind="accruals.biweekPersonalUsed | number:2"></td>
            </tr>
            <tr class="total-row">
              <td>Available next Period</td>
              <td ng-bind="accruals.personalAvailable - accruals.biweekPersonalUsed | number:2">
              </td>
            </tr>
          </tbody>
        </table>
        <h4 class="content-info vacation">Vacation Hours</h4>
        <table class="accrual-detail-table">
          <tbody>
            <tr>
              <td>Prev. Year Banked</td>
              <td ng-bind="accruals.vacationBanked | number:2"></td>
            </tr>
            <tr>
              <td>Accrued YTD</td>
              <td ng-bind="accruals.vacationAccruedYtd | number:2"></td>
            </tr>
            <tr>
              <td>Used YTD</td>
              <td ng-bind="-accruals.vacationUsed | number:2"></td>
            </tr>
            <tr class="total-row">
              <td>Available for Period</td>
              <td ng-bind="accruals.vacationAvailable | number:2"></td>
            </tr>
            <tr>
              <td>Used in Period</td>
              <td ng-bind="accruals.biweekVacationUsed | number:2"></td>
            </tr>
            <tr class="total-row">
              <td>Available next Period</td>
              <td ng-bind="accruals.vacationAvailable - accruals.biweekVacationUsed | number:2">
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div class="col-2-12 accrual-detail-info">
      <h4 class="content-info">Period Dates</h4>
      <table>
        <tr>
          <th>Begin</th>
          <td>{{accruals.payPeriod.startDate | moment:'MM/DD/YYYY'}}</td>
        </tr>
        <tr>
          <th>End</th>
          <td>{{accruals.payPeriod.endDate | moment:'MM/DD/YYYY'}}</td>
        </tr>
      </table>
      <h4 class="content-info">Acc. Rates</h4>
      <table class="accrual-rate-table">
        <tr>
          <th>Vacation</th>
          <td ng-bind="accruals.vacationRate"></td>
        </tr>
        <tr>
          <th>Sick</th>
          <td ng-bind="accruals.sickRate"></td>
        </tr>
      </table>
      <h4 class="content-info">Actions</h4>
      <p class="accrual-report-link">
        <a target="_blank" title="Open a Printable View for this Record"
           ng-href="{{reportUrl}}">Print Report</a>
        <br><br>
        <a href="" ng-click="close()" title="Close this Window">Exit</a>
      </p>
    </div>
  </div>
</div>