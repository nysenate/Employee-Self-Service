<div ng-controller="ReviewHistoryCtrl as vm">
  <div>
    <div class="travel-hero">
      <h2>Review History</h2>
    </div>

    <div class="content-container travel-content-controls">
      <h4 class="travel-content-info travel-text-bold" style="margin-bottom: 0px;">Search reviewed applications by date.</h4>
      <div class="text-align-center">
        <div class="padding-10 inline-block">
          <label class="bold">From:</label>
          <input datepicker readonly='true' id="dateFrom" style="margin-left: 1px;"
                 ng-model="vm.date.from" to-date="vm.date.to"
                 alt-filed=""
                 ng-change="vm.applyFilters()"/>
        </div>
        <div class="padding-10 inline-block">
          <label class="bold">To:</label>
          <input datepicker readonly='true' id="dateTo" style="margin-left: 1px;"
                 ng-model="vm.date.to" from-date="vm.date.from"
                 ng-change="vm.applyFilters()"/>
        </div>
      </div>
    </div>

    <div ng-if="vm.data.isLoading === false">
      <div ng-if="vm.data.apps.filtered.length === 0">
        <div class="content-container">
          <div class="content-info">
            <h2 class="dark-gray">No Review History.</h2>
          </div>
        </div>
      </div>

      <div ng-if="vm.data.apps.filtered.length > 0">
        <ess-app-summary-table
            apps="vm.data.apps.filtered"
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
