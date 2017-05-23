<div class="accrual-loader" ng-show="loading">
  <h3 class="text-align-center">Loading Allowance...</h3>
  <div loader-indicator class="sm-loader"></div>
</div>

<div class="allowance-container" ng-hide="loading">
  <div class="allowance-component">
    <div class="captioned-hour-square">
      <div class="hours-caption">
        {{allowance.year}} Allowance
      </div>
      <div class="hours-display">
        <div class="ytd-hours">
          <div class="hours-caption">Total Allowed Hours</div>
          {{ allowance.totalHours | number }}
        </div>
        <div class="ytd-hours">
          <div class="hours-caption">Reported Hours</div>
          {{allowance.hoursUsed | number}}
        </div>
        <div class="ytd-hours" ng-show="showRecordHours()">
          <div class="hours-caption">Current Record Hours</div>
          {{tempWorkHours | number}}
        </div>
        <div class="ytd-hours">
          <div class="hours-caption">Estimated Available Hours</div>
          <span ng-bind-html="getAvailableHours() | number | hoursDiffHighlighter"></span>
        </div>
      </div>
    </div>
  </div>
</div>
