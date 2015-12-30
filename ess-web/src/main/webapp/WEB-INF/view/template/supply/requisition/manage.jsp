<div ng-controller="SupplyManageController">
  <div class="supply-order-hero">
    <h2>Manage Requisitions</h2>
  </div>

  <%--   Pending Orders   --%>
  <div class="content-container">
    <h1 style="background: #d19525; color: white;">Pending Requisition Requests</h1>

    <div class="content-info" ng-show="pendingOrders.length == 0">
        <h2 class="dark-gray">No Pending Requests.</h2>
    </div>

    <table class="ess-table supply-listing-table" ng-show="pendingOrders.length > 0">
      <thead>
      <tr>
        <th>Location</th>
        <th>Employee</th>
        <th>Item Count</th>
        <th>Order Date</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="order in pendingOrders" ng-class="{warn: highlightOrder(order)}" ng-click="showEditingDetails(order)">
        <td>{{order.location.code + '-' + order.location.locationTypeCode}}</td>
        <td>{{order.customer.lastName}}</td>
        <td>{{getOrderQuantity(order)}}</td>
        <td>{{order.orderDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
      </tr>
      </tbody>
    </table>
  </div>

  <%--   Processing Orders   --%>
  <div class="content-container">
    <h1 style="background: #4196A7; color: white;">Processing Requisition Requests</h1>

    <div class="content-info" ng-show="processingOrders.length == 0">
      <h2 class="dark-gray">No Processing Requests.</h2>
    </div>

    <table class="ess-table supply-listing-table" ng-show="processingOrders.length > 0">
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
      <tr ng-repeat="order in processingOrders" ng-class="{warn: highlightOrder(order)}" ng-click="showEditingDetails(order)">
        <td>{{order.location.code + '-' + order.location.locationTypeCode}}</td>
        <td>{{order.customer.lastName}}</td>
        <td>{{getOrderQuantity(order)}}</td>
        <td>{{order.orderDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{order.issuingEmployee.lastName}}</td>
      </tr>
      </tbody>
    </table>
  </div>

  <%--   Completed Orders   --%>
  <div class="content-container">
    <h1 style="background: #799933; color: white;">Completed Requisition Requests</h1>

    <div class="content-info" ng-show="completedOrders.length == 0">
      <h2 class="dark-gray">No Completed Requests.</h2>
    </div>

    <table class="ess-table supply-listing-table" ng-show="completedOrders.length > 0">
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
      <tr ng-repeat="order in completedOrders" ng-click="showCompletedDetails(order)">
        <td>{{order.location.code + '-' + order.location.locationTypeCode}}</td>
        <td>{{order.customer.lastName}}</td>
        <td>{{getOrderQuantity(order)}}</td>
        <td>{{order.orderDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{order.completedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{order.issuingEmployee.lastName}}</td>
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
