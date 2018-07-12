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
    <span class="step completed" ng-click="gotoStep(STATES.PURPOSE)"
          ng-class="{navigable: stepNavigable(STATES.PURPOSE)}">Purpose</span>
    <span class="step" ng-click="gotoStep(STATES.OUTBOUND)"
          ng-class="{completed: highlightStep(STATES.OUTBOUND), navigable: stepNavigable(STATES.OUTBOUND)}">Outbound</span>
    <span class="step" ng-click="gotoStep(STATES.RETURN)"
          ng-class="{completed: highlightStep(STATES.RETURN), navigable: stepNavigable(STATES.RETURN)}">Return</span>
    <span class="step" ng-click="gotoStep(STATES.ALLOWANCES)"
          ng-class="{completed: highlightStep(STATES.ALLOWANCES), navigable: stepNavigable(STATES.ALLOWANCES)}">Expenses</span>
    <span class="step"
          ng-class="{completed: highlightStep(STATES.REVIEW)}">Review</span>
  </div>

  <div loader-indicator class="loader" ng-show="!app"></div>

  <div ng-if="app">
    <div ng-if="pageState === STATES.PURPOSE">
      <travel-application-purpose></travel-application-purpose>
    </div>

    <div ng-if="pageState === STATES.OUTBOUND">
      <travel-application-outbound></travel-application-outbound>
    </div>

    <div ng-if="pageState === STATES.RETURN">
      <travel-application-return></travel-application-return>
    </div>

    <div ng-if="pageState === STATES.ALLOWANCES">
      <travel-application-allowances></travel-application-allowances>
    </div>

    <div ng-if="pageState === STATES.REVIEW">
      <travel-application-review></travel-application-review>
    </div>
  </div>

  <div modal-container>

    <%--Continue application modal--%>
    <modal modal-id="travel-continue-application-modal">
      <div travel-continue-application-modal></div>
    </modal>


    <%--Cancel Modal--%>
    <modal modal-id="cancel-application">
      <div confirm-modal rejectable="true"
           title="Cancel Travel Application"
           confirm-message="Are you sure you want to cancel your current application?"
           resolve-button="Yes"
           reject-button="No">
      </div>
    </modal>

    <%--Loading Modal--%>
    <modal modal-id="loading">
      <div progress-modal title="Loading..."></div>
    </modal>

    <%--County information modal--%>
    <modal modal-id="address-county-modal">
      <div address-county-modal></div>
    </modal>

    <%-- Review Modals --%>
    <modal modal-id="submit-confirm">
      <div confirm-modal rejectable="true"
           title="Submit Travel Application?"
           confirm-message="The application will be sent to your department head for review."
           resolve-button="Submit"
           reject-button="Cancel">
      </div>
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

    <%-- Review detail modals --%>
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
