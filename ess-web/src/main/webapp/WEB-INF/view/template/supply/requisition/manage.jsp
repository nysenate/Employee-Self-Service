<div ng-controller="SupplyManageController">
  <div class="supply-order-hero">
    <h2>Manage Requisitions</h2>
  </div>

  <div class="content-container">
    <h1>Pending Requisition Requests</h1>
    <div class="padding-10">
      <table class="ess-table supply-manage-table">
        <thead>
        <tr>
          <th>Location Code</th>
          <th>Location Type</th>
          <th>Employee</th>
          <th>Quantity</th>
          <th>Order Date</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-class="{warn: highlightOrder(order)}" ng-repeat="order in pendingOrders" ng-click="showDetails(req)">
          <td>{{order.locCode}}</td>
          <td>{{order.locType}}</td>
          <td>{{order.purchaser}}</td>
          <td>{{getOrderQuantity(order)}}</td>
          <td>{{order.dateTime.format('YYYY-MM-DD hh:mm A')}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>