<div ng-controller="NewApplicationCtrl">
  <div class="travel-hero">
    <h2>Travel Application</h2>
  </div>

  <ess-new-app-breadcrumbs></ess-new-app-breadcrumbs>

  <div loader-indicator class="loader" ng-show="!data.amendment"></div>

  <div ng-if="data.amendment">
    <div ng-if="stateService.isPurposeState()">
      <ess-purpose-edit-form dto="data"
                             event-types="data.eventTypes"
                             positive-callback="savePurpose(dto)"
                             negative-callback="cancel(amendment)">
      </ess-purpose-edit-form>
    </div>

    <div ng-if="stateService.isOutboundState()">
      <ess-outbound-edit-form amendment="data.amendment"
                              traveler="data.traveler"
                              positive-callback="saveOutbound(amendment)"
                              neutral-callback="toPurposeState(amendment)"
                              negative-callback="cancel(amendment)">
      </ess-outbound-edit-form>
    </div>

    <div ng-if="stateService.isReturnState()">
      <ess-return-edit-form amendment="data.amendment"
                            positive-callback="saveRoute(amendment)"
                            neutral-callback="toOutboundState(amendment)"
                            negative-callback="cancel(amendment)">
      </ess-return-edit-form>
    </div>

    <div ng-if="stateService.isAllowancesState()">
      <ess-allowances-edit-form amendment="data.amendment"
                                positive-callback="saveAllowances(amendment)"
                                neutral-callback="toReturnState(amendment)"
                                negative-callback="cancel(amendment)">
      </ess-allowances-edit-form>
    </div>

    <div ng-if="stateService.isReviewState()">
      <ess-review-edit-form amendment="data.amendment"
                            traveler="data.traveler"
                            positive-btn-label="Submit Application"
                            positive-callback="submitApplication(amendment)"
                            neutral-callback="toAllowancesState(amendment)"
                            negative-callback="cancel(amendment)">
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
           confirm-message="Are you sure you want to cancel your current application? This will delete any data you have entered."
           resolve-button="Cancel Application"
           resolve-class="travel-reject-btn"
           reject-button="Keep Working"
           reject-class="travel-neutral-btn">
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

    <%--Long trip warning--%>
    <modal modal-id="long-trip-warning">
      <div confirm-modal rejectable="true"
           title="Scheduled trip is longer than 7 days"
           confirm-message="Are you sure your travel dates are correct?"
           resolve-button="Yes, my dates are correct"
           resolve-class="travel-neutral-btn"
           reject-button="Let me review"
           reject-class="travel-primary-btn">
      </div>
    </modal>

    <%--Error with one or more travel dates--%>
    <modal modal-id="travel-date-error-modal">
      <travel-date-error-modal></travel-date-error-modal>
    </modal>

    <%-- Review Modals --%>
    <modal modal-id="submit-confirm">
      <div confirm-modal rejectable="true"
           title="Submit Travel Application"
           confirm-message="Are you sure you want to submit this travel application?"
           resolve-button="Submit Application"
           resolve-class="travel-submit-btn"
           reject-button="Cancel"
           reject-class="travel-neutral-btn">
      </div>
    </modal>

    <modal modal-id="submit-progress">
      <div progress-modal title="Saving travel application..."></div>
    </modal>

    <modal modal-id="submit-results">
      <div confirm-modal rejectable="true"
           title="Your travel application has been submitted."
           confirm-message="What would you like to do next?"
           resolve-button="Go back to ESS"
           reject-button="Log out of ESS">
      </div>
    </modal>

    <modal modal-id="external-api-error">
      <div error-modal
           title="Communication Error"
           buttonValue="Ok"
           buttonClass="reject-button">
        <p>
          ESS is unable to communicate with some 3rd party services used to create the travel estimate.
          Please try submitting your travel application again later. If you continue to get this error, please contact
          STS at {{helplinePhoneNumber}}.
        </p>

      </div>
    </modal>

    <modal modal-id="document-upload-error">
      <div error-modal
           title="Unable to Upload Documents">
        <p>
          We were unable to save your selected supporting documents.
          Please make sure all your documents are below the maximum size of 10MB and try again.
          If you still experience this error contact the helpline at {{helplinePhoneNumber}}.
        </p>
      </div>
    </modal>

  </div>

</div>
</div>
