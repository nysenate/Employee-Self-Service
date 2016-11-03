<div class="save-progress-modal">
  <h3 class="content-info" style="margin-bottom:0;">Your Requisition Request has been submitted!</h3>
  <div class="content-info">
  <h4>Your requisition id number is: {{requisitionId}}</h4>
  <h4>What would you like to do next?</h4>
  <input ng-click="logout()" class="reject-button" type="button" value="Log out of ESS"/>
  <input ng-click="returnToSupply()" class="submit-button" type="button" value="Back to ESS"/>
  </div>
</div>