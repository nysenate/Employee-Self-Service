<div ng-controller="SupplyManageController">
  <div class="supply-order-hero">
    <h2>Manage Requisitions</h2>
  </div>

  <%--   Pending Orders   --%>
  <div class="content-container">
    <h1 style="background: #d19525; color: white;">Pending Requisition Requests</h1>

    <div class="content-info" ng-show="pendingShipments.length == 0">
        <h2 class="dark-gray">No Pending Requests.</h2>
    </div>

    <table class="ess-table supply-listing-table" ng-show="pendingShipments.length > 0">
      <thead>
      <tr>
        <th>Location</th>
        <th>Employee</th>
        <th>Item Count</th>
        <th>Order Date</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="shipment in pendingShipments" ng-class="{warn: highlightShipment(shipment)}" ng-click="showEditingDetails(shipment)">
        <td>{{shipment.order.activeVersion.destination.code + '-' + shipment.order.activeVersion.destination.locationTypeCode}}</td>
        <td>{{shipment.order.activeVersion.customer.lastName}}</td>
        <td>{{getOrderQuantity(shipment)}}</td>
        <td>{{shipment.order.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
      </tr>
      </tbody>
    </table>
  </div>

  <%--   Processing Orders   --%>
  <div class="content-container">
    <h1 style="background: #4196A7; color: white;">Processing Requisition Requests</h1>

    <div class="content-info" ng-show="processingShipments.length == 0">
      <h2 class="dark-gray">No Processing Requests.</h2>
    </div>

    <table class="ess-table supply-listing-table" ng-show="processingShipments.length > 0">
      <thead>
      <tr>
        <th>Location</th>
        <th>Employee</th>
        <th>Item Count</th>
        <th>Order Date</th>
        <th>Issuing Employee</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="shipment in processingShipments" ng-class="{warn: highlightShipment(shipment)}" ng-click="showEditingDetails(shipment)">
        <td>{{shipment.order.activeVersion.destination.code + '-' + shipment.order.activeVersion.destination.locationTypeCode}}</td>
        <td>{{shipment.order.activeVersion.customer.lastName}}</td>
        <td>{{getOrderQuantity(shipment)}}</td>
        <td>{{shipment.order.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{shipment.activeVersion.issuer.lastName}}</td>
      </tr>
      </tbody>
    </table>
  </div>

  <%--   Completed Orders   --%>
  <div class="content-container">
    <h1 style="background: #799933; color: white;">Completed Requisition Requests</h1>

    <div class="content-info" ng-show="completedShipments.length == 0">
      <h2 class="dark-gray">No Completed Requests.</h2>
    </div>

    <table class="ess-table supply-listing-table" ng-show="completedShipments.length > 0">
      <thead>
      <tr>
        <th>Location</th>
        <th>Employee</th>
        <th>Item Count</th>
        <th>Order Date</th>
        <th>Completed Date</th>
        <th>Issuing Employee</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="shipment in completedShipments" ng-click="showCompletedDetails(shipment)">
        <td>{{shipment.order.activeVersion.destination.code + '-' + shipment.order.activeVersion.destination.locationTypeCode}}</td>
        <td>{{shipment.order.activeVersion.customer.lastName}}</td>
        <td>{{getOrderQuantity(shipment)}}</td>
        <td>{{shipment.order.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{shipment.completedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{shipment.activeVersion.issuer.lastName}}</td>
      </tr>
      </tbody>
    </table>
  </div>

  <% /** Container for all modal dialogs */ %>
  <div modal-container>
    <div manage-editing-modal ng-if="isOpen('manage-editing-modal')"></div>
    <div manage-completed-modal ng-if="isOpen('manage-completed-modal')"></div>
  </div>

</div>
