<div ng-controller="ReviewHistoryCtrl as vm">
  <div>
    <div class="travel-hero">
      <h2>Review History</h2>
    </div>
    <div class="content-container content-controls">
      <h4 class="travel-content-info travel-text-bold">Applications you have reviewed.</h4>
    </div>

    <div ng-if="vm.data.isLoading === false">
      <div ng-if="vm.data.apps.length === 0">
        <div class="content-container">
          <div class="content-info">
            <h2 class="dark-gray">No Review History.</h2>
          </div>
        </div>
      </div>

      <div ng-if="vm.data.apps.length > 0">
        <ess-app-summary-table
            apps="vm.data.apps"
            on-row-click="vm.displayAppReviewViewModal(app)"
            show-status>
        </ess-app-summary-table>
      </div>
    </div>

    <div modal-container>
      <modal modal-id="app-review-view-modal">
        <div app-review-view-modal></div>
      </modal>
    </div>

  </div>
</div>
