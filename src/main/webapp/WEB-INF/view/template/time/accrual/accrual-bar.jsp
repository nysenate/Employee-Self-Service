
<div class="accrual-loader" ng-show="loading">
  <h3 class="loading-text">Loading Accruals...</h3>
  <div loader-indicator class="sm-loader" style="margin: 15.5px auto;"></div>
</div>

<div class="accrual-hours-container" ng-hide="loading">
  <div class="accrual-component">
    <div class="captioned-hour-square" style="float:left;">
      <div class="hours-caption personal">Personal Hours</div>
      <div class="hours-display">{{accruals.personalAvailable}}</div>
    </div>
  </div>
  <div class="accrual-component">
    <div class="captioned-hour-square" style="float:left;">
      <div class="hours-caption vacation">Vacation Hours</div>
      <div class="hours-display">{{accruals.vacationAvailable}}</div>
    </div>
  </div>
  <div class="accrual-component">
    <div class="captioned-hour-square" style="float:left;">
      <div class="hours-caption sick">Sick Hours</div>
      <div class="odometer hours-display">{{accruals.sickAvailable}}</div>
    </div>
  </div>
  <div class="accrual-component">
    <div class="captioned-hour-square" style="width:390px;">
      <div style="background:rgb(92, 116, 116);color:white"
           class="hours-caption">Year To Date Hours Of Service
      </div>
      <div class="hours-display" style="font-size:1em">
        <div class="ytd-hours">
          Expected: {{accruals.serviceYtdExpected}}
        </div>
        <div class="ytd-hours">Actual: {{accruals.serviceYtd}}</div>
        <div class="ytd-hours" style="border-right:none;">
          Difference:
          <span
              ng-bind-html="(accruals.serviceYtd - accruals.serviceYtdExpected) | hoursDiffHighlighter"></span>
        </div>
      </div>
    </div>
  </div>
  <div style="clear:both;"></div>
</div>
