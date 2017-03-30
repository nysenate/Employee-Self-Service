<div ng-controller="SupplyHistoryController">
  <div class="supply-order-hero">
    <h2>Requisition History</h2>
  </div>

  <div class="content-container content-controls">
    <h4 class="content-info supply-text" style="margin-bottom: 0px;">
      Search approved and rejected requisitions.</h4>
    <div class="grid text-align-center">
      <div class="col-6-12 padding-10">
        <label class="supply-text">Location:</label>
        <select ng-model="selectedLocation" ng-options="location for location in locations"
                ng-required="true"
                ng-change="onFilterChange()"></select>
      </div>
      <div class="col-6-12 padding-10">
        <label class="supply-text">Issuer:</label>
        <select ng-model="selectedIssuer" ng-options="emp for emp in issuers"
                ng-change="onFilterChange()"></select>
      </div>
      <div class="col-6-12" style="padding: 0 10px 10px 10px;">
        <label class="supply-text">From:</label>
        <input datepicker readonly='true' style="margin-left: 1px;"
               ng-model="filter.date.from" to-date="filter.date.to"
               ng-change="onFilterChange()"/>
      </div>
      <div class="col-6-12" style="padding: 0 10px 10px 10px;">
        <label class="supply-text">To:</label>
        <input datepicker readonly='true' style="margin-left: 2px;"
               ng-model="filter.date.to" from-date="filter.date.from"
               ng-change="onFilterChange()"/>
      </div>
    </div>
  </div>

  <div loader-indicator class="loader" ng-show="loading === true"></div>

  <div class="content-container large-print-font-size" ng-show="loading === false">
    <div class="content-info" ng-show="shipments.length == 0">
      <h2 class="dark-gray">No results were found.</h2>
    </div>
    <div ng-show="shipments.length > 0">
      <div>
        <dir-pagination-controls class="text-align-center" on-page-change="onPageChange()"
                                 pagination-id="order-history-pagination"
                                 boundary-links="true" max-size="10"></dir-pagination-controls>
      </div>
      <div class="padding-10">
        <table class="ess-table supply-listing-table">
          <thead>
          <tr>
            <th>Id</th>
            <th>Location</th>
            <th>Ordered By</th>
            <th>Item Count</th>
            <th>Order Date</th>
            <th>Complete Date</th>
            <th>Issued By</th>
          </tr>
          </thead>
          <tbody>
          <tr dir-paginate="shipment in shipments | itemsPerPage : paginate.itemsPerPage"
              current-page="paginate.currPage"
              pagination-id="order-history-pagination"
              total-items="paginate.totalItems"
              ng-click="viewRequisition(shipment)">
            <td>{{shipment.requisitionId}}</td>
            <td>{{shipment.destination.locId}}</td>
            <td>{{shipment.customer.lastName}}</td>
            <td>{{distinctItemQuantity(shipment)}}</td>
            <td>{{shipment.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
            <td>{{shipment.completedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
            <td>{{shipment.issuer.lastName}}</td>
          </tr>
          </tbody>
        </table>
      </div>
      <div>
        <dir-pagination-controls class="text-align-center" pagination-id="order-history-pagination"
                                 boundary-links="true" max-size="10"></dir-pagination-controls>
      </div>
    </div>
  </div>
  <div modal-container></div>
</div>