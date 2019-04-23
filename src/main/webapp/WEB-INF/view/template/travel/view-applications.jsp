<div ng-controller="TravelHistoryController">
  <div class="travel-hero">
    <h2>View Applications</h2>
  </div>
  <div class="content-container travel-content-controls">
    <h4 class="travel-content-info travel-text-bold" style="margin-bottom: 0px;">Search submitted travel applications by
      date</h4>
    <div class="text-align-center">
      <div class="padding-10 inline-block">
        <label class="bold">From:</label>
        <input datepicker readonly='true' id="dateFrom" style="margin-left: 1px;"
               ng-model="date.from" to-date="date.to"
               ng-change="applyFilters()"/>
      </div>
      <div class="padding-10 inline-block">
        <label class="bold">To:</label>
        <input datepicker readonly='true' id="dateTo" style="margin-left: 1px;"
               ng-model="date.to" from-date="date.from"
               ng-change="applyFilters()"/>
      </div>
    </div>
  </div>

  <div ng-if="appRequest.$resolved === true">
    <travel-application-table
        apps="apps.filtered"
        onclick="viewApplicationDetails(app)">
    </travel-application-table>
  </div>

  <div modal-container>
    <modal modal-id="travel-history-detail-modal">
      <div travel-history-detail-modal></div>
    </modal>
  </div>
</div>
</div>
