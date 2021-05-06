<div ng-controller="NewApplicationCtrl">
  <div class="travel-hero">
    <h2>Travel Application</h2>
  </div>
  <div class="content-container content-controls">
    <div class="padding-10 text-align-center">
      Travel application on behalf of:
      <span ng-if="!stateService.isReviewState()">
      <ui-select ng-model="data.traveler" style="min-width: 200px;">
        <ui-select-match>
          <span ng-bind="$select.selected.fullName"></span>
        </ui-select-match>
        <ui-select-choices repeat="emp in data.allowedTravelers | filter: $select.search track by emp.employeeId">
          <div ng-bind-html="emp.fullName"></div>
        </ui-select-choices>
      </ui-select>
      </span>
      <span ng-if="stateService.isReviewState()">
        {{data.app.traveler.fullName}}
      </span>
    </div>
  </div>

  <ess-new-app-breadcrumbs></ess-new-app-breadcrumbs>

  <div loader-indicator class="loader" ng-show="!data.amendment"></div>

  <div ng-if="data.amendment">
    <div ng-if="stateService.isPurposeState()">
      <ess-purpose-edit-form amendment="data.amendment"
                             event-types="data.eventTypes"
                             positive-callback="savePurpose(amendment)"
                             negative-callback="cancel(amendment)">
      </ess-purpose-edit-form>
    </div>

    <div ng-if="stateService.isOutboundState()">
      <ess-outbound-edit-form amendment="data.amendment"
                              traveler="data.traveler"
                              title="Enter your outbound route starting from the origin and including all destinations."
                              positive-callback="saveOutbound(amendment)"
                              neutral-callback="toPurposeState(amendment)"
                              negative-callback="cancel(amendment)">
      </ess-outbound-edit-form>
    </div>

    <div ng-if="stateService.isReturnState()">
      <ess-return-edit-form amendment="data.amendment"
                            title="Enter your return route from the last destination to the origin."
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
                            title="Please review your application."
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

    <modal modal-id="missing-department-data">
      <div confirm-modal rejectable="false"
           title="Unable to create Travel application"
      <%--           confirm-message="Unable to create a Travel Application due to incomplete department data in your employee records.--%>
      <%--                            Please contact the STS Helpline at {{helplinePhoneNumber}} with any questions or concerns."--%>
           resolve-button="Okay">
        <p>
          Unable to create a Travel Application due to missing department data in your employee records.
          <br/>
          Please contact the STS Helpline at {{helplinePhoneNumber}} with any questions or concerns.
        </p>
      </div>
    </modal>

    <%--Cancel Modal--%>
    <modal modal-id="cancel-application">
      <div confirm-modal rejectable="true"
           title="Cancel Travel Application"
           confirm-message="Are you sure you want to cancel your current application? This will delete any data you have entered."
           resolve-button="Cancel Application"
           resolve-class="reject-button"
           reject-button="Keep Application"
           reject-class="neutral-button">
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
           title="Submit Travel Application?"
           confirm-message="Are you sure you want to submit this travel application?"
           resolve-button="Submit Application"
           reject-button="Cancel"
           reject-class="neutral-button">
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

  </div>

</div>
</div>
