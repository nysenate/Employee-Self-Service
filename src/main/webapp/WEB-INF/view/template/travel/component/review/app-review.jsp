<div ng-controller="AppReviewCtrl">
  <div>
    <div class="travel-hero">
      <h2>Review Applications</h2>
    </div>
    <div class="content-container content-controls">
      <h4 class="travel-content-info travel-text-bold">The following travel applications require your review.</h4>
    </div>
  </div>

  <div ng-if="data.isLoading === false">
    <div ng-if="data.apps.length === 0">
      <div class="content-container">
        <div class="content-info">
          <h2 class="dark-gray">No Applications to Review.</h2>
        </div>
      </div>
    </div>

    <div ng-if="data.apps.length > 0">
      <ess-app-summary-table
          apps="data.apps"
          on-row-click="displayReviewFormModal(app)">
      </ess-app-summary-table>
    </div>
  </div>

  <div modal-container>
    <modal modal-id="app-review-form-modal">
      <div app-review-form-modal></div>
    </modal>
  </div>

</div>
