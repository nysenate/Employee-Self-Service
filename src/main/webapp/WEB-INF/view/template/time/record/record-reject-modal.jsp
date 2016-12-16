<div class="confirm-modal">
  <h3 class="content-info">Explain the reason for disapproving the time record.</h3>
  <div class="confirmation-message">
    <p class="reject-modal-error" ng-show="noRemarks">You must provide a reason for disapproval</p>
    <textarea class="reject-modal-textarea" placeholder="Reason for disapproval"
              ng-model="remarks" tabindex="1" maxlength="150"></textarea>
    <div class="input-container">
      <input type="button" class="time-neutral-button"
             value="Cancel" title="Cancel Disapproval"
             ng-click="cancel()"/>
      <input type="button" class="reject-button"
             value="Disapprove Record" title="Disapprove Record"
             ng-click="submit()"/>
    </div>
  </div>
</div>
