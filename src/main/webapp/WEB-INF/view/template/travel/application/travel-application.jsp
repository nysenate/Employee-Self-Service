<div ng-controller="NewTravelApplicationCtrl">
  <div class="travel-hero">
    <h2>Travel Application</h2>
  </div>
  <div class="content-container content-controls">
    <div class="padding-10 text-align-center">
      Travel application for: <span class="bold">{{app.traveler.firstName}} {{app.traveler.lastName}}</span>
    </div>
  </div>

  <div class="step-indicator">
    <a class="step completed">Purpose</a>
    <a class="step" ng-class="{completed: highlightOriginStep()}">Origin</a>
    <a class="step" ng-class="{completed: highlightDestinationStep()}">Destination</a>
    <a class="step" ng-class="{completed: highlightExpensesStep()}">Expenses</a>
    <a class="step" ng-class="{completed: highlightReviewStep()}">Review</a>
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

    <modal modal-id="review-progress">
      <div progress-modal title="Loading..."></div>
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

    <modal modal-id="travel-lodging-details-modal">
      <div travel-lodging-details-modal></div>
    </modal>

    <modal modal-id="travel-meal-details-modal">
      <div travel-meal-details-modal></div>
    </modal>

    <modal modal-id="travel-mileage-details-modal">
      <div travel-mileage-details-modal></div>
    </modal>
  </div>

</div>
</div>
