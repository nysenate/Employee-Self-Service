<div ng-controller="ReviewHistoryCtrl">
  <div>
    <div class="travel-hero">
      <h2>Review History</h2>
    </div>
    <div class="content-container content-controls">
      <h4 class="travel-content-info travel-text-bold">Applications you have reviewed.</h4>
    </div>

    <div ng-if="data.isLoading === false">
      <div ng-if="data.apps.length === 0">
        <div class="content-container">
          <div class="content-info">
            <h2 class="dark-gray">No Review History.</h2>
          </div>
        </div>
      </div>

      <div ng-if="data.apps.length > 0">
        <ess-app-summary-table
            apps="data.apps"
            on-row-click="displayAppFormViewModal(app)"
            show-status>
        </ess-app-summary-table>
      </div>
    </div>

    <div modal-container>
      <modal modal-id="app-form-view-modal">
        <div app-form-view-modal></div>
      </modal>
    </div>

  </div>
</div>
