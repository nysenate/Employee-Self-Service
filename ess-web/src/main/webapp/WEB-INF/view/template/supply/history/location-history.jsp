<div ng-controller="SupplyLocationHistoryCtrl">
  <div class="supply-order-hero">
    <h2>Location History</h2>
  </div>

  <div loader-indicator ng-show="state.searching === true"></div>

  <div class="content-container" ng-show="state.searching === false">
    <h4 class="content-info dark-blue-purple">Recent requisitions for {{empLocation.code + '-' + empLocation.locationTypeCode}}</h4>

    <div class="content-info" ng-show="locOrders.length == 0">
      <h2 class="dark-gray">No Recent History.</h2>
    </div>

    <div class="padding-10" ng-show="locOrders.length > 0">
      <table class="ess-table supply-listing-table">
        <thead>
        <tr>
          <th>Ordered By</th>
          <th>Order Date</th>
          <th>Status</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="order in locOrders">
          <td>{{order.customer.lastName}}</td>
          <td>{{order.orderDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
          <td ng-class="{'pending-cell': order.status === 'PENDING',
                       'processing-cell': order.status === 'PROCESSING',
                       'completed-cell': order.status === 'COMPLETED'}">
            {{order.status}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>