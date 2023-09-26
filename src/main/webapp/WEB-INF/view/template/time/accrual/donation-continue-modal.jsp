<div class="confirm-modal" style="font-size: 120%">
  <h3 class="content-info">
    Donation Confirmation
  </h3>
  <div class="confirmation-message">
    You will donate {{state.hoursToDonate}} out of {{state.accruedSickTime}} accrued sick hours.<br>
    <hr/>
    <div class="input-container">
      <input ng-click="resolveContinue()" class="submit-button" type="button" value="Continue">
      <input ng-click="rejectContinue()" class="reject-button" type="button" value="Go Back">
    </div>
  </div>
</div>
