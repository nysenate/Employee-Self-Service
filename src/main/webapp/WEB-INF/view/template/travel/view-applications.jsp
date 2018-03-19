<div ng-controller="TravelHistoryController">
  <div class="travel-hero">
    <h2>View Applications</h2>
  </div>
  <div class="content-container">
    <div>
      <h4 class="content-info">View Applications by Date</h4>
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
    <div class="content-container">
      <div class="padding-10">
        <table class="travel-table travel-hover">
          <thead>
            <tr>
              <th>Travel Date</th>
              <th>Employee</th>
              <th>Destination</th>
              <th>Allotted Funds</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr dir-paginate="app in apps.filtered | orderBy: '-travelDate' : true | itemsPerPage : 10"
                pagination-id="travel-history-pagination"
                ng-click="viewApplicationDetails(row.id)">
              <td>{{app.startDate | date:'M/d/yyyy'}}</td>
              <td>{{app.traveler.lastName}}</td>
              <td>{{app.destinations[0].address.city}}</td>
              <td>{{app.totalAllowance | currency}}</td>
              <td>{{app.status}}</td>
            </tr>
          </tbody>
        </table>
        <div>
          <dir-pagination-controls class="text-align-center" pagination-id="travel-history-pagination"
                                   boundary-links="true" max-size="10"></dir-pagination-controls>
        </div>
      </div>
    </div>
    <div modal-container>
      <modal modal-id="travel-history-detail-modal">
        <div travel-history-detail-modal></div>
      </modal>
    </div>
  </div>
</div>
