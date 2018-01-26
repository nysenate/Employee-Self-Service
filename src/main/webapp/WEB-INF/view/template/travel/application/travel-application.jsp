<div ng-controller="NewTravelApplicationCtrl">
  <div class="travel-hero">
    <h2>Travel Application</h2>
  </div>
  <div class="content-container content-controls">
    <div class="padding-10 text-align-center">
      Travel application for: <span class="bold">{{app.traveler.firstName}} {{app.traveler.lastName}}</span>
    </div>
  </div>

  <div ng-if="pageState === STATES.PURPOSE">
    <travel-application-purpose></travel-application-purpose>
  </div>

  <div ng-if="pageState === STATES.ORIGIN">
    <travel-application-origin></travel-application-origin>
  </div>

  <div ng-if="pageState === STATES.DESTINATION">
    <travel-application-destination></travel-application-destination>
  </div>

  <div ng-if="pageState === STATES.ALLOWANCES">
    <travel-application-allowances></travel-application-allowances>
  </div>

  <div ng-if="pageState === STATES.REVIEW">
    <travel-application-review></travel-application-review>
  </div>

  <div modal-container>
    <modal modal-id="destination-selection-modal">
      <div destination-selection-modal></div>
    </modal>
    <modal modal-id="calculating-allowances-progress">
      <div progress-modal title="Calculating allowances..."></div>
    </modal>
    <modal modal-id="submit-progress">
      <div progress-modal title="Submitting travel application..."></div>
    </modal>
    <modal modal-id="submit-results">
      <div confirm-modal rejectable="true"
           title="Your travel application has been submitted."
           confirm-message="What would you like to do next?"
           resolve-button="Go back to ESS"
           reject-button="Log out of ESS">
      </div>
    </modal>
  </div>

</div>
</div>
