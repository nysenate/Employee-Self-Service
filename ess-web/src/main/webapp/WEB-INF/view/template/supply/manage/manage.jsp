<div ng-controller="SupplyManageController">
  <div class="supply-order-hero">
    <h2>Manage Requisitions</h2>
  </div>

  <%--   Pending Orders   --%>

  <%--Pending request loading animation. loader-indicator styling is bad inside a content-container this gets around that.--%>
  <div ng-show="!pendingSearch.response.$resolved">
    <div class="content-container">
      <h1 style="background: #d19525; color: white;">Pending Requisition Requests</h1>
    </div>
    <div loader-indicator class="sm-loader"></div>
  </div>

  <%-- Pending Done loading --%>
  <div class="content-container" ng-show="pendingSearch.response.$resolved">
    <h1 style="background: #d19525; color: white;">Pending Requisition Requests</h1>

    <div class="content-info" ng-show="pendingSearch.matches.length === 0 && pendingSearch.error === false">
        <h2 class="dark-gray">No Pending Requests.</h2>
    </div>

    <table class="ess-table supply-listing-table" ng-show="pendingSearch.matches.length > 0">
      <thead>
      <tr>
        <th>Location</th>
        <th>Employee</th>
        <th>Item Count</th>
        <th>Order Date</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="shipment in pendingSearch.matches" ng-class="{warn: highlightShipment(shipment)}" ng-click="showEditingDetails(shipment)">
        <td>{{shipment.order.activeVersion.destination.code + '-' + shipment.order.activeVersion.destination.locationTypeCode}}</td>
        <td>{{shipment.order.activeVersion.customer.lastName}}</td>
        <td>{{getOrderQuantity(shipment)}}</td>
        <td>{{shipment.order.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
      </tr>
      </tbody>
    </table>
  </div>

  <%--   Processing Orders   --%>
  <%--Loading indicator--%>
  <div ng-show="!processingSearch.response.$resolved">
    <div class="content-container">
      <h1 style="background: #4196A7; color: white;">Processing Requisition Requests</h1>
    </div>
    <div loader-indicator class="sm-loader"></div>
  </div>

  <%--Processing Done Loading--%>
  <div class="content-container" ng-show="processingSearch.response.$resolved">
    <h1 style="background: #4196A7; color: white;">Processing Requisition Requests</h1>

    <div class="content-info" ng-show="processingSearch.matches.length == 0 && processingSearch.error === false">
      <h2 class="dark-gray">No Processing Requests.</h2>
    </div>

    <table class="ess-table supply-listing-table" ng-show="processingSearch.matches.length > 0">
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
      <tr ng-repeat="shipment in processingSearch.matches" ng-class="{warn: highlightShipment(shipment)}" ng-click="showEditingDetails(shipment)">
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

  <%--Loading indicator--%>
  <div ng-show="!completedSearch.response.$resolved">
    <div class="content-container">
      <h1 style="background: #799933; color: white;">Completed Requisition Requests</h1>
    </div>
    <div loader-indicator class="sm-loader"></div>
  </div>

  <%--Done Loading Completed--%>
  <div class="content-container" ng-show="completedSearch.response.$resolved">
    <h1 style="background: #799933; color: white;">Completed Requisition Requests</h1>

    <div class="content-info" ng-show="completedSearch.matches.length === 0 && completedSearch.error === false">
      <h2 class="dark-gray">No Completed Requests.</h2>
    </div>

    <table class="ess-table supply-listing-table" ng-show="completedSearch.matches.length > 0">
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
      <tr ng-repeat="shipment in completedSearch.matches" ng-click="showEditingDetails(shipment)">
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

  <%--  Approved Orders   --%>

  <%--Loading indicator--%>
  <div ng-show="!approvedSearch.response.$resolved">
    <div class="content-container">
      <h1 style="background: #6270BD; color: white;">Approved Requisition Requests</h1>
    </div>
    <div loader-indicator class="sm-loader"></div>
  </div>

  <%--Done Loading Approved--%>
  <div class="content-container" ng-show="approvedSearch.response.$resolved">
    <h1 style="background: #6270BD; color: white;">Approved Requisition Requests</h1>

    <div class="content-info" ng-show="approvedSearch.matches.length === 0 && approvedSearch.error === false">
      <h2 class="dark-gray">No Approved Requests.</h2>
    </div>

    <table class="ess-table supply-listing-table" ng-show="approvedSearch.matches.length > 0">
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
      <tr ng-repeat="shipment in approvedSearch.matches" ng-click="showEditingDetails(shipment)">
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
