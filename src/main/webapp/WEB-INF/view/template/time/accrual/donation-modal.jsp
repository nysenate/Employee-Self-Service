<div class="confirm-modal">
  <h3 class="content-info">
    Donation Confirmation
  </h3>
  <div class="confirmation-message">
    Once you donate your sick leave,  it is irrevocable and forfeited permanently.
    The donated sick leave will not be returned to you.
  </div>
  <label for="lastName">Enter your last name to confirm:</label>
  <input type="text" id="lastName" name="lastName" ng-model="state.inputLastName"><br><br>
  <input ng-click="resolveDonation()" ng-disabled="state.inputLastName !== state.realLastName"
         class="submit-button" type="button" value="Confirm"/>
  &nbsp;&nbsp;
  <input ng-click="rejectDonation()" class="reject-button" type="button" value="Cancel"/>
</div>
