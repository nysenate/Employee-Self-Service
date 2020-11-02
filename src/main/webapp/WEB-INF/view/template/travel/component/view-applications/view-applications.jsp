<div ng-controller="UserAppsCtrl as vm">
  <div class="travel-hero">
    <h2>View Travel Applications</h2>
  </div>
  <div class="content-container travel-content-controls">
    <h4 class="travel-content-info travel-text-bold" style="margin-bottom: 0px;">Search submitted travel applications by
      date</h4>
    <div class="text-align-center">
      <div class="padding-10 inline-block">
        <label class="bold">From:</label>
        <input datepicker readonly='true' id="dateFrom" style="margin-left: 1px;"
               ng-model="vm.date.from" to-date="vm.date.to"
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

  <div ng-if="vm.appRequest.$resolved === true">
    <div ng-if="vm.apps.filtered.length === 0">
      <div class="content-container">
        <div class="content-info">
          <h2 class="dark-gray">No results were found.</h2>
        </div>
      </div>
    </div>

    <div ng-if="vm.apps.filtered.length > 0">
      <ess-app-summary-table
          apps="vm.apps.filtered"
          on-row-click="vm.viewApplicationForm(app)"
          show-status>
      </ess-app-summary-table>
    </div>
  </div>

  <div modal-container>
    <modal modal-id="app-form-view-modal">
      <div app-form-view-modal></div>
    </modal>
    <modal modal-id="app-expense-summary-modal">
      <div app-expense-summary-modal></div>
    </modal>
  </div>
</div>
