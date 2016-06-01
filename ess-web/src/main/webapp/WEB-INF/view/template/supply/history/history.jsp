<div ng-controller="SupplyHistoryController">
  <div class="content-container">
    <div class="supply-order-hero">
      <h2>Requisition History</h2>
    </div>

    <div>
      <p class="content-info dark-blue-purple bold large-print-font-size" style="margin-bottom: 0px;">Show Requisitions ordered during the following date range.</p>
      <div class="grid text-align-center">
        <div class="col-6-12 padding-10">
          <label class="bold">Location:</label>
          <select ng-model="selectedLocation" ng-options="location for location in locations"></select>
        </div>
        <div class="col-6-12 padding-10">
          <label class="bold">Issuer:</label>
          <select ng-model="selectedIssuer" ng-options="emp for emp in issuers"></select>
        </div>
        <div class="col-6-12" style="padding: 0 10px 10px 10px;">
          <label class="bold">From:</label>
          <input ng-model="filter.date.from" ng-change="reloadShipments()" type="date"/>
        </div>
        <div class="col-6-12" style="padding: 0 10px 10px 10px;">
          <label class="bold">To:</label>
          <input ng-model="filter.date.to" ng-change="reloadShipments()" type="date"/>
        </div>
      </div>
    </div>
  </div>

  <div class="content-container" ng-show="shipments.length == 0">
    <div class="content-info">
      <h2 class="dark-gray">No History</h2>
    </div>
  </div>

  <div class="content-container large-print-font-size" ng-show="shipments.length > 0">
    <div class="padding-10">
      <table class="ess-table supply-listing-table">
        <thead>
        <tr>
          <th>Location</th>
          <th>Ordered By</th>
          <th>Quantity</th>
          <th>Order Date</th>
          <th>Complete Date</th>
          <th>Issued By</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="shipment in filteredShipments" ng-click="viewOrder(shipment)" ng-show="isInFilter(shipment)">
          <td>{{shipment.activeVersion.destination.locId}}</td>
          <td>{{shipment.activeVersion.customer.lastName}}</td>
          <td>{{getOrderQuantity(shipment)}}</td>
          <td>{{shipment.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
          <td>{{shipment.completedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
          <td>{{shipment.activeVersion.issuer.lastName}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
  <div>
  </div>
</div>