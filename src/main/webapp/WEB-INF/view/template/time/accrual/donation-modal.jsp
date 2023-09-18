<div class="text-align-center confirm-modal" style="font-size: 110%">
  <h3 class="content-info">
    Donation Confirmation
  </h3>
  <div class="confirmation-message">
    <p>
      Once you donate your sick leave, it is irrevocable and forfeited permanently.<br>
      The donated sick leave will not be returned to you.
    </p>
    <label for="lastName" class="bold">Enter your last name to confirm:</label>
    <input autofocus type="text" id="lastName" name="lastName" ng-model="state.inputLastName">
    <hr/>
    <div class="input-container">
      <input ng-click="resolveDonation()" ng-disabled="state.inputLastName !== state.realLastName"
             class="submit-button" type="button" value="Confirm"/>
      <input ng-click="rejectDonation()" class="reject-button" type="button" value="Cancel"/>
    </div>
  </div>
</div>
