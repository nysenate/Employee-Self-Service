<div ng-controller="NewApplicationCtrl">
  <div class="travel-hero">
    <h2>Travel Application</h2>
  </div>
  <div class="content-container content-controls">
    <div class="padding-10 text-align-center">
      Travel application for: <span class="bold">{{data.app.traveler.firstName}} {{data.app.traveler.lastName}}</span>
    </div>
  </div>

  <ess-new-app-breadcrumbs></ess-new-app-breadcrumbs>

  <div loader-indicator class="loader" ng-show="!data.app"></div>

  <div ng-if="data.app">
    <div ng-if="stateService.isPurposeState()">
      <ess-purpose-edit-form app="data.app"
                             title="Enter your purpose of travel."
                             positive-callback="savePurpose(app)"
                             negative-callback="cancel(app)">
      </ess-purpose-edit-form>
    </div>

    <div ng-if="stateService.isOutboundState()">
      <ess-outbound-edit-form app="data.app"
                              title="Enter your outbound route starting from the origin and including all destinations."
                              positive-callback="saveOutbound(app)"
                              neutral-callback="previousStep(app)"
                              negative-callback="cancel(app)">
      </ess-outbound-edit-form>
    </div>

    <div ng-if="stateService.isReturnState()">
      <ess-return-edit-form app="data.app"
                            title="Enter your return route from the last destination to the origin."
                            positive-callback="saveRoute(app)"
                            neutral-callback="previousStep(app)"
                            negative-callback="cancel(app)">
      </ess-return-edit-form>
    </div>

    <div ng-if="stateService.isAllowancesState()">
      <ess-allowances-edit-form app="data.app"
                                title="Enter your estimated expenses for the following categories."
                                positive-callback="saveAllowances(app)"
                                neutral-callback="previousStep(app)"
                                negative-callback="cancel(app)">
      </ess-allowances-edit-form>
    </div>

    <div ng-if="stateService.isReviewState()">
      <ess-review-edit-form app="data.app"
                            title="Please review your application."
                            positive-callback="submitApplication(app)"
                            neutral-callback="previousStep(app)"
                            negative-callback="cancel(app)">
      </ess-review-edit-form>
    </div>
  </div>

  <div modal-container>

    <%--Continue application modal--%>
    <modal modal-id="ess-continue-saved-app-modal">
      <div ess-continue-saved-app-modal></div>
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
    <modal modal-id="ess-address-county-modal">
      <div ess-address-county-modal></div>
    </modal>

    <modal modal-id="long-trip-warning">
      <div confirm-modal rejectable="true"
           title="Scheduled trip is longer than 7 days"
           confirm-message="Are you sure your travel dates are correct?"
           resolve-button="Yes, my dates are correct"
           reject-button="Let me review">
      </div>
    </modal>

    <%-- Review Modals --%>
    <modal modal-id="submit-confirm">
      <div confirm-modal rejectable="true"
           title="Save Travel Application?"
           confirm-message="Are you sure you want to save this travel application?"
           resolve-button="Save"
           reject-button="Cancel">
      </div>
    </modal>

    <modal modal-id="submit-progress">
      <div progress-modal title="Saving travel application..."></div>
    </modal>

    <modal modal-id="submit-results">
      <div confirm-modal rejectable="true"
           title="Your travel application has been saved."
      <%--confirm-message="What would you like to do next?"--%>
           resolve-button="Go back to ESS"
           reject-button="Log out of ESS">
        <div style="padding-bottom: 20px;">
          <p>
            You should now <a class="bold" target="_blank"
                              ng-href="${ctxPath}/travel/application/print?id={{data.app.id}}&print=true">print</a>,
            sign and deliver your application to your department head.
          </p>
        </div>
      </div>
    </modal>

    <%-- Review detail modals --%>
    <modal modal-id="ess-lodging-details-modal">
      <div ess-lodging-details-modal></div>
    </modal>

    <modal modal-id="ess-meal-details-modal">
      <div ess-meal-details-modal></div>
    </modal>

    <modal modal-id="ess-mileage-details-modal">
      <div ess-mileage-details-modal></div>
    </modal>

    <modal modal-id="external-api-error">
      <div confirm-modal rejectable="true"
           title="Failed to Create Travel Request"
           resolve-button="Go back to ESS"
           reject-button="Log out of ESS">
        <div>
          <p style="text-align: left;">
            ESS is unable to communicate with some 3rd party services required to create the travel estimate.
            Please try submitting your travel application again later. If you continue to get this error please contact
            STS.
          </p>
          <h4>
            What would you like to do next?
          </h4>
        </div>
      </div>
    </modal>

  </div>

</div>
</div>
