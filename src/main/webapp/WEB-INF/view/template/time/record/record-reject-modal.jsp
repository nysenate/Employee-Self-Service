<p class="content-info no-bottom-margin">Explain the reason for disapproving the time record.</p>
<p class="reject-modal-error" ng-show="noRemarks">You must provide a reason for disapproval</p>
<textarea class="reject-modal-textarea" placeholder="Reason for disapproval" ng-model="remarks" tabindex="1"></textarea>
<div class="reject-modal-button-wrapper">
  <input type="button" style="float:left;" class="time-neutral-button" value="Cancel" ng-click="cancel()"/>
  <input type="button" style="float:right;" class="reject-button" value="Disapprove Record" ng-click="submit()"/>
</div>
