<div class="confirm-modal" style="font-size: 120%">
  <h3 class="content-info">
    Donation Confirmation
  </h3>
  <div class="confirmation-message">
    <p>
      Once you donate your sick leave, it is irrevocable and forfeited permanently.<br>
      The donated sick leave will not be returned to you.
    </p>
    <form class="last-donation-confirm-form" name="lastNameDonationForm">
      <div>
        <label>Last Name</label>
        <span ng-bind="state.realLastName"></span>
      </div>
      <div style="margin-bottom: 10px">
        <label for="inputLastName">Confirm Last Name</label>
        <input type="text" id="inputLastName" ng-model="state.inputLastName">
      </div>
    </form>
    <hr/>
    <div class="input-container">
      <input ng-click="resolveDonation()" ng-disabled="state.inputLastName !== state.realLastName"
             class="submit-button" type="button" value="Submit"/>
      <input ng-click="rejectDonation()" class="reject-button" type="button" value="Cancel"/>
    </div>
  </div>
</div>
