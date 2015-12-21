<div ng-controller="SupplyHistoryController">
  <div class="supply-order-hero">
    <h2>Requisition History</h2>
  </div>

  <div class="content-container">
    <div class="content-info">
      <label>Filter by Location:</label>
      <select ng-model="selectedLocation" ng-options="location for location in locations"></select>
    </div>
  </div>

  <div class="content-container">
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
        <tr ng-repeat="order in filteredOrders" ng-click="viewOrder(order)" ng-show="shouldShowOrder(order)">
          <td>{{order.location}}</td>
          <td>{{getOrderQuantity(order)}}</td>
          <td>{{order.orderDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
          <td>{{order.completedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
          <td>{{order.issuingEmployee.lastName}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>