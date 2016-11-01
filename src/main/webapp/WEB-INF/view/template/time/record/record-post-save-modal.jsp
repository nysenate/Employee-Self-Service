<div class="record-confirmation-modal record-post-save-modal">
  <h3 class="content-info">
    Your time record has been {{submit ? 'submitted' : 'saved'}}.
  </h3>
  <div class="confirmation-message">
    <h4>What would you like to do next?</h4>
    <div class="input-container">
      <input ng-click="resolve()" class="reject-button" type="button" value="Log out of ESS"/>
      <input ng-click="reject()" class="submit-button" type="button" value="Go back to ESS"/>
    </div>
  </div>
</div>
