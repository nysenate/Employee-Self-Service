<p class="content-info no-bottom-margin">Explain the reason for disapproving the time record.</p>
<textarea class="reject-modal-textarea" placeholder="Reason for disapproval" ng-model="remarks" tabindex="1"></textarea>
<div class="reject-modal-button-wrapper">
  <input type="button" style="float:left;" class="time-neutral-button" value="Cancel" ng-click="cancel()"/>
  <input type="button" style="float:right;" class="reject-button" value="Disapprove Record" ng-click="resolve()"/>
</div>
