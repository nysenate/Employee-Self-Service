<div ng-controller="EditApplicationCtrl as vm">
  <div class="travel-hero">
    <h2>Edit Travel Application</h2>
  </div>
  <div class="content-container content-controls">
    <div class="padding-10 text-align-center">
      <span class="disapproved-text">Editing</span> Travel application for: <span class="bold"
                                                                                  ng-bind="::vm.draft.traveler.fullName"></span>
    </div>
  </div>

  <ess-new-app-breadcrumbs ng-if="vm.activeRole !== 'TRAVEL_ADMIN' && vm.activeRole !== 'SECRETARY_OF_THE_SENATE'"/>
  <ess-edit-app-breadcrumbs ng-if="vm.activeRole === 'TRAVEL_ADMIN' || vm.activeRole === 'SECRETARY_OF_THE_SENATE'"/>

  <div ng-if="vm.draft">
    <div ng-if="vm.stateService.isPurposeState()">
      <ess-purpose-edit-form data="vm"
                             positive-callback="vm.savePurpose(draft)"
                             negative-callback="vm.cancelEdit(draft)"
                             negative-label="Cancel">
      </ess-purpose-edit-form>
    </div>

    <div ng-if="vm.stateService.isOutboundState()">
      <ess-outbound-edit-form data="vm"
                              positive-callback="vm.saveOutbound(draft)"
                              neutral-callback="vm.toPurposeState(draft)"
                              negative-callback="vm.cancelEdit(draft)"
                              negative-label="Cancel">
      </ess-outbound-edit-form>
    </div>

    <div ng-if="vm.stateService.isReturnState()">
      <ess-return-edit-form data="vm"
                            positive-callback="vm.saveRoute(draft)"
                            neutral-callback="vm.toOutboundState(draft)"
                            negative-callback="vm.cancelEdit(draft)"
                            negative-label="Cancel">
      </ess-return-edit-form>
    </div>

    <div ng-if="vm.stateService.isAllowancesState()">
      <ess-allowances-edit-form data="vm"
                                positive-callback="vm.saveAllowances(draft)"
                                neutral-callback="vm.toReturnState(draft)"
                                negative-callback="vm.cancelEdit(draft)"
                                negative-label="Cancel">
      </ess-allowances-edit-form>
    </div>

    <div ng-if="vm.stateService.isOverridesState()">
      <ess-perdiem-overrides-edit-form data="vm"
                                       positive-callback="vm.saveOverrides(draft)"
                                       neutral-callback="vm.toAllowancesState(draft)"
                                       negative-callback="vm.cancelEdit(draft)"
                                       negative-label="Cancel">
      </ess-perdiem-overrides-edit-form>
    </div>

    <div ng-if="vm.stateService.isReviewState()">
      <ess-review-edit-form
          ng-if="vm.activeRole === 'TRAVEL_ADMIN' || vm.activeRole === 'SECRETARY_OF_THE_SENATE'"
          data="vm"
          positive-btn-label="Save Edits"
          positive-callback="vm.saveEdits(draft)"
          neutral-callback="vm.toOverridesState(draft)"
          negative-callback="vm.cancelEdit(draft)"
          negative-label="Cancel">
      </ess-review-edit-form>
      <ess-review-edit-form
          ng-if="vm.activeRole === 'NONE'"
          data="vm"
          positive-btn-label="Save and Resubmit"
          positive-callback="vm.saveEdits(draft)"
          neutral-callback="vm.toOverridesState(draft)"
          negative-callback="vm.cancelEdit(draft)"
          negative-label="Cancel">
      </ess-review-edit-form>
    </div>
  </div>


  <div modal-container>

    <%--Cancel Modal--%>
    <modal modal-id="cancel-edits">
      <div confirm-modal rejectable="true"
           title="Cancel Travel Application Edit"
           confirm-message="Are you sure you want to cancel the editing of this travel application? Any changes you have made will be lost."
           resolve-button="Do not Cancel"
           resolve-class="travel-neutral-btn"
           reject-class="travel-reject-btn"
           reject-button="Cancel Edit">
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
           resolve-class="travel-neutral-btn"
           reject-button="Let me review"
           reject-class="travel-primary-btn">
      </div>
    </modal>

    <%-- Review Modals --%>
    <modal modal-id="submit-confirm">
      <div confirm-modal rejectable="true"
           title="Save Travel Application?"
           confirm-message="Are you sure you want to save this travel application?"
           resolve-button="Save"
           resolve-class="travel-submit-btn"
           reject-button="Cancel"
           reject-class="travel-neutral-btn">
      </div>
    </modal>

    <modal modal-id="submit-progress">
      <div progress-modal title="Saving travel application..."></div>
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