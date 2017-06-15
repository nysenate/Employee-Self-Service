<div ng-controller="SupplyFulfillmentController">
  <div class="supply-order-hero">
    <h2>Fulfillment</h2>
  </div>

  <%--Error saving requisition notification--%>
  <div class="margin-10" ess-notification level="error" title="Error saving requisition."
       message="Unable to save requisition due to multiple users updating at once. Please reload this page and try again."
       ng-show="saveResponse.error"></div>

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
        <th></th>
        <th>Id</th>
        <th>Location</th>
        <th>Employee</th>
        <th>Item Count</th>
        <th>Order Date</th>
        <th>Assigned To</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="requisition in pendingSearch.matches" ng-class="calculateHighlighting(requisition)"
          ng-click="setRequisitionSearchParam(requisition.requisitionId)">
        <td ng-class="{'supply-delivery-icon': requisition.deliveryMethod === 'DELIVERY',
                     'supply-pickup-icon': requisition.deliveryMethod === 'PICKUP'}"
            style="padding-left: 50px; padding-right: 0px;"></td>
        <td>{{requisition.requisitionId}}</td>
        <td>{{requisition.destination.locId}}</td>
        <td>{{requisition.customer.lastName}}</td>
        <td>{{distinctItemQuantity(requisition)}}</td>
        <td>{{requisition.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{requisition.issuer.lastName || "-"}}</td>
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
        <th></th>
        <th>Id</th>
        <th>Location</th>
        <th>Employee</th>
        <th>Item Count</th>
        <th>Order Date</th>
        <th>Issuing Employee</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="requisition in processingSearch.matches" ng-class="calculateHighlighting(requisition)"
          ng-click="setRequisitionSearchParam(requisition.requisitionId)">
         <td ng-class="{'supply-delivery-icon': requisition.deliveryMethod === 'DELIVERY',
                     'supply-pickup-icon': requisition.deliveryMethod === 'PICKUP'}"
            style="padding-left: 50px; padding-right: 0px;"></td>
        <td>{{requisition.requisitionId}}</td>
        <td>{{requisition.destination.locId}}</td>
        <td>{{requisition.customer.lastName}}</td>
        <td>{{distinctItemQuantity(requisition)}}</td>
        <td>{{requisition.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{requisition.issuer.lastName}}</td>
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
        <th></th>
        <th>Id</th>
        <th>Location</th>
        <th>Employee</th>
        <th>Item Count</th>
        <th>Order Date</th>
        <th>Completed Date</th>
        <th>Issuing Employee</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="requisition in completedSearch.matches" ng-class="calculateHighlighting(requisition)"
          ng-click="setRequisitionSearchParam(requisition.requisitionId)">
        <td ng-class="{'supply-delivery-icon': requisition.deliveryMethod === 'DELIVERY',
                     'supply-pickup-icon': requisition.deliveryMethod === 'PICKUP'}"
            style="padding-left: 50px; padding-right: 0px;"></td>
        <td>{{requisition.requisitionId}}</td>
        <td>{{requisition.destination.locId}}</td>
        <td>{{requisition.customer.lastName}}</td>
        <td>{{distinctItemQuantity(requisition)}}</td>
        <td>{{requisition.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{requisition.completedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{requisition.issuer.lastName}}</td>
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
        <th>Id</th>
        <th>Location</th>
        <th>Employee</th>
        <th>Item Count</th>
        <th>Order Date</th>
        <th>Approved Date</th>
        <th>Issuing Employee</th>
        <th>Sync Status</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="requisition in approvedSearch.matches" ng-click="setRequisitionSearchParam(requisition.requisitionId)">
        <td>{{requisition.requisitionId}}</td>
        <td>{{requisition.destination.locId}}</td>
        <td>{{requisition.customer.lastName}}</td>
        <td>{{distinctItemQuantity(requisition)}}</td>
        <td>{{requisition.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{requisition.approvedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{requisition.issuer.lastName}}</td>
        <td><span class="tick" ng-show="requisition.lastSfmsSyncDateTime && requisition.savedInSfms"></span><span class="cross"  ng-show="requisition.lastSfmsSyncDateTime && !requisition.savedInSfms"></span></td>
      </tr>
      </tbody>
    </table>
  </div>

  <%-- Sync Fail Shipments--%>
  <div class="content-container" ng-show="syncFailedSearch.matches.length > 0">
    <h1 style="background: #001f3f; color: white;">Sync Failed Requisition Requests</h1>

    <table class="ess-table supply-listing-table" ng-show="syncFailedSearch.matches.length > 0">
      <thead>
      <tr>
        <th>Id</th>
        <th>Location</th>
        <th>Employee</th>
        <th>Order Date</th>
        <th>Approved Date</th>
        <th>Last Sync Time</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="requisition in syncFailedSearch.matches" ng-click="setRequisitionSearchParam(requisition.requisitionId)">
        <td>{{requisition.requisitionId}}</td>
        <td>{{requisition.destination.locId}}</td>
        <td>{{requisition.customer.lastName}}</td>
        <td>{{requisition.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{requisition.approvedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{requisition.lastSfmsSyncDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
      </tr>
      </tbody>
    </table>
  </div>

  <%--  Rejected Shipments   --%>

  <div class="content-container" ng-show="canceledSearch.response.$resolved && canceledSearch.matches.length > 0">
    <h1 style="background: #8D9892; color: white;">Rejected Requisition Requests</h1>

    <table class="ess-table supply-listing-table">
      <thead>
      <tr>
        <th>Id</th>
        <th>Location</th>
        <th>Employee</th>
        <th>Item Count</th>
        <th>Order Date</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="requisition in canceledSearch.matches" ng-click="setRequisitionSearchParam(requisition.requisitionId)">
        <td>{{requisition.requisitionId}}</td>
        <td>{{requisition.destination.locId}}</td>
        <td>{{requisition.customer.lastName}}</td>
        <td>{{distinctItemQuantity(requisition)}}</td>
        <td>{{requisition.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
      </tr>
      </tbody>
    </table>
  </div>

  <% /** Container for all modal dialogs */ %>
  <div modal-container>
    <modal modal-id="fulfillment-editing-modal">
      <div fulfillment-editing-modal
           supply-employees='supplyEmployees'
           location-statistics='locationStatistics'>
      </div>
    </modal>

    <modal modal-id="fulfillment-immutable-modal">
      <div fulfillment-immutable-modal></div>
    </modal>
  </div>

</div>
