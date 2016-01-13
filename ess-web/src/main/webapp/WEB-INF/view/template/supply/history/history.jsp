<div ng-controller="SupplyHistoryController">
  <div class="content-container">
    <div class="supply-order-hero">
      <h2>Requisition History</h2>
    </div>

    <div class="content-info" ng-show="orders.length == 0">
      <h2 class="dark-gray">No History</h2>
    </div>

    <div ng-show="orders.length > 0">
      <p class="content-info dark-blue-purple bold" style="margin-bottom: 0px;">Show Requisitions ordered during the following date range.</p>
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
          <input ng-model="filter.date.from" type="date" max="filter.date.max" min="filter.date.min"/>
        </div>
        <div class="col-6-12" style="padding: 0 10px 10px 10px;">
          <label class="bold">To:</label>
          <input ng-model="filter.date.to" type="date" max="filter.date.max" min="filter.date.min"/>
        </div>
      </div>
    </div>
  </div>

  <div class="content-container" ng-show="orders.length > 0">
    <div class="padding-10">
      <table class="ess-table supply-listing-table">
        <thead>
        <tr>
          <th>Location</th>
          <th>Quantity</th>
          <th>Order Date</th>
          <th>Complete Date</th>
          <th>Issued By</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="order in filteredOrders" ng-click="viewOrder(order)" ng-show="isInFilter(order)">
          <td>{{order.location.code + '-' + order.location.locationTypeCode}}</td>
          <td>{{getOrderQuantity(order)}}</td>
          <td>{{order.orderDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
          <td>{{order.completedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
          <td>{{order.issuingEmployee.lastName}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
  <div>
  </div>
</div>