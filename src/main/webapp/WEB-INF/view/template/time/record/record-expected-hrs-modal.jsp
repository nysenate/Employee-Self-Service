<div class="confirm-modal">
  <h3 class="content-info">
    Hours entered are less than pay period requirement
  </h3>
  <div class="confirmation-message">
    <p ng-show="serviceSurplus >= expectedDifference">
      Warning: You are attempting to use {{expectedDifference}}
      excess hours.
    </p>
    <div ng-show="serviceSurplus < expectedDifference">
      <p>Warning: You do not have enough hours to fulfill required pay period hours.</p>
      <div style="display: flex; justify-content: space-around">
        <span class="bold">Required: {{biWeekHrsExpected}} hrs.</span>
        <span class="bold">Entered: {{raSaTotal}} hrs.</span>
        <span class="bold">
          Year To Date
          {{ serviceSurplus < 0 ? "Deficit" : "Excess" }}:
          {{serviceSurplus}} hrs.
        </span>
      </div>
    </div>
    <hr/>
    <div class="input-container">
      <input ng-click="resolveModal()" class="submit-button" type="button" value="Proceed"/>
      <input ng-click="rejectModal()" class="reject-button" type="button" value="Cancel"/>
    </div>
  </div>
</div>
