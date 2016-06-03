<div ng-controller="SupplyLocationHistoryCtrl">
  <div class="supply-order-hero">
    <h2>Location History</h2>
  </div>

  <div loader-indicator class="loader" ng-show="loading === true"></div>

  <div class="content-container" ng-show="loading === false">
    <h4 class="content-info dark-blue-purple" style="margin-bottom: 0;">Recently ordered requisitions</h4>

    <div class="content-info" ng-show="allRequisitions.length == 0">
      <h2 class="dark-gray">No Recent History.</h2>
    </div>

    <div class="padding-10" ng-show="allRequisitions.length > 0">
      <table class="ess-table supply-listing-table">
        <thead>
        <tr>
          <th>Id</th>
          <th>Ordered By</th>
          <th>Destination</th>
          <th>Order Date</th>
          <th>Status</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="requisition in allRequisitions" ng-click="viewRequisition(requisition)">
          <td>{{requisition.id}}</td>
          <td>{{requisition.activeVersion.customer.lastName}}</td>
          <td>{{requisition.activeVersion.destination.locId}}</td>
          <td>{{requisition.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
          <td ng-class="{'pending-cell': requisition.activeVersion.status === 'PENDING',
                       'processing-cell': requisition.activeVersion.status === 'PROCESSING',
                       'completed-cell': requisition.activeVersion.status === 'COMPLETED' || requisition.activeVersion.status === 'APPROVED',
                       'rejected-cell': requisition.activeVersion.status === 'REJECTED'}">
            {{requisition.activeVersion.status}}
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>