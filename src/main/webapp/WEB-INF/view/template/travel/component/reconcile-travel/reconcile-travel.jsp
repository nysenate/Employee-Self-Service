<div ng-controller="ReconcileTravelCtrl as vm">
  <div>
    <div class="travel-hero">
      <h2>Reconcile Travel</h2>
    </div>

    <div class="content-container travel-content-controls">
      <h4 class="travel-content-info travel-text-bold" style="margin-bottom: 0;">
        Search Travel Applications by date and traveler.
      </h4>

      <div class="text-align-center">
        <div class="padding-10 inline-block">
          <label class="bold">From:</label>
          <input datepicker readonly='true' id="dateFrom" style="margin-left: 1px;"
                 ng-model="vm.date.from" to-date="vm.date.to"
                 alt-filed=""
                 ng-change="vm.onFilterChange()"/>
        </div>
        <div class="padding-10 inline-block">
          <label class="bold">To:</label>
          <input datepicker readonly='true' id="dateTo" style="margin-left: 1px;"
                 ng-model="vm.date.to" from-date="vm.date.from"
                 ng-change="vm.onFilterChange()"/>
        </div>
      </div>
      <div style="padding-bottom: 10px;">
        <label class="bold">Traveler:</label>
        <ui-select ng-model="vm.data.travelers.selected" style="min-width: 175px;" ng-change="vm.onFilterChange()">
          <ui-select-match allow-clear="true">
            <span ng-bind="$select.selected.fullName"></span>
          </ui-select-match>
          <ui-select-choices repeat="traveler in vm.data.travelers.all | filter: $select.search track by traveler.employeeId">
            <div ng-bind-html="traveler.fullName"></div>
          </ui-select-choices>
        </ui-select>
      </div>
    </div>

    <div ng-if="vm.data.isLoading === false">
      <div ng-if="vm.data.reviews.filtered.length === 0">
        <div class="content-container">
          <div class="content-info">
            <h2 class="dark-gray">No Review History.</h2>
          </div>
        </div>
      </div>

      <div ng-if="vm.data.reviews.filtered.length > 0">
        <ess-app-review-summary-table
            reviews="vm.data.reviews.filtered"
            on-row-click="vm.displayReviewViewModal(review)">
        </ess-app-review-summary-table>
      </div>
    </div>

    <div modal-container>
      <modal modal-id="app-review-view-modal">
        <div app-review-view-modal></div>
      </modal>
    </div>

  </div>
</div>
