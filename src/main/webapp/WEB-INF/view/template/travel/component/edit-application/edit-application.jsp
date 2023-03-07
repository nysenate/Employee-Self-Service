<div ng-controller="EditApplicationCtrl as vm">
  <div class="travel-hero">
    <h2>Edit Travel Application</h2>
  </div>
  <div class="content-container content-controls">
    <div class="padding-10 text-align-center">
      <span class="disapproved-text">Editing</span> Travel application for: <span class="bold"
                                                                                  ng-bind="::vm.dto.traveler.fullName"></span>
    </div>
  </div>

  <ess-new-app-breadcrumbs ng-if="vm.activeRole !== 'TRAVEL_ADMIN'"/>
  <ess-edit-app-breadcrumbs ng-if="vm.activeRole === 'TRAVEL_ADMIN'"/>

  <div ng-if="vm.dto">
    <div ng-if="vm.stateService.isPurposeState()">
      <ess-purpose-edit-form amendment="vm.dto.amendment"
                             event-types="vm.dto.validEventTypes"
                             positive-callback="vm.savePurpose(amendment)"
                             negative-callback="vm.cancelEdit(amendment)"
                             negative-label="Cancel">
      </ess-purpose-edit-form>
    </div>

    <div ng-if="vm.stateService.isOutboundState()">
      <ess-outbound-edit-form amendment="vm.dto.amendment"
                              traveler="vm.dto.traveler"
                              positive-callback="vm.saveOutbound(amendment)"
                              neutral-callback="vm.toPurposeState(amendment)"
                              negative-callback="vm.cancelEdit(amendment)"
                              negative-label="Cancel">
      </ess-outbound-edit-form>
    </div>

    <div ng-if="vm.stateService.isReturnState()">
      <ess-return-edit-form amendment="vm.dto.amendment"
                            positive-callback="vm.saveRoute(amendment)"
                            neutral-callback="vm.toOutboundState(amendment)"
                            negative-callback="vm.cancelEdit(amendment)"
                            negative-label="Cancel">
      </ess-return-edit-form>
    </div>

    <div ng-if="vm.stateService.isAllowancesState()">
      <ess-allowances-edit-form amendment="vm.dto.amendment"
                                positive-callback="vm.saveAllowances(amendment)"
                                neutral-callback="vm.toReturnState(amendment)"
                                negative-callback="vm.cancelEdit(amendment)"
                                negative-label="Cancel">
      </ess-allowances-edit-form>
    </div>

    <%--    <div ng-if="vm.stateService.isPurposeState()">--%>
    <div ng-if="vm.stateService.isOverridesState()">
      <ess-perdiem-overrides-edit-form amendment="vm.dto.amendment"
                                       positive-callback="vm.saveOverrides(amendment)"
                                       neutral-callback="vm.toAllowancesState(amendment)"
                                       negative-callback="vm.cancelEdit(amendment)"
                                       negative-label="Cancel">
      </ess-perdiem-overrides-edit-form>
    </div>

    <div ng-if="vm.stateService.isReviewState()">
      <ess-review-edit-form
          ng-if="vm.activeRole === 'TRAVEL_ADMIN'"
          amendment="vm.dto.amendment"
          traveler="vm.dto.traveler"
          positive-btn-label="Save Edits"
          positive-callback="vm.saveEdits(amendment)"
          neutral-callback="vm.toOverridesState(amendment)"
          negative-callback="vm.cancelEdit(amendment)"
          negative-label="Cancel">
      </ess-review-edit-form>
      <ess-review-edit-form
          ng-if="vm.activeRole === 'NONE'"
          amendment="vm.dto.amendment"
          positive-btn-label="Save and Resubmit"
          positive-callback="vm.saveEdits(amendment)"
          neutral-callback="vm.toOverridesState(amendment)"
          negative-callback="vm.cancelEdit(amendment)"
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