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
  <div ng-show="!data.reqRequest.response.$resolved">
    <div class="content-container">
      <h1 style="background: #d19525; color: white;">Pending Requisition Requests</h1>
    </div>
    <div loader-indicator class="sm-loader"></div>
  </div>

  <%-- Pending Done loading --%>
  <div class="content-container" ng-show="data.reqRequest.response.$resolved">
    <h1 style="background: #d19525; color: white;">Pending Requisition Requests</h1>

    <div class="content-info" ng-show="data.reqs.pending.length === 0">
      <h2 class="dark-gray">No Pending Requests.</h2>
    </div>

    <table class="ess-table supply-listing-table" ng-show="data.reqs.pending.length > 0">
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
      <tr ng-repeat="requisition in data.reqs.pending | orderBy:'requisitionId':true"
          ng-class="calculateHighlighting(requisition)"
          ng-click="setRequisitionSearchParam(requisition.requisitionId)">
        <td ng-class="{'supply-pickup-icon': requisition.deliveryMethod === 'PICKUP'}"></td>
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
  <div ng-show="!data.reqRequest.response.$resolved">
    <div class="content-container">
      <h1 style="background: #4196A7; color: white;">Processing Requisition Requests</h1>
    </div>
    <div loader-indicator class="sm-loader"></div>
  </div>

  <%--Processing Done Loading--%>
  <div class="content-container" ng-show="data.reqRequest.response.$resolved">
    <h1 style="background: #4196A7; color: white;">Processing Requisition Requests</h1>

    <div class="content-info" ng-show="data.reqs.processing.length == 0">
      <h2 class="dark-gray">No Processing Requests.</h2>
    </div>

    <table class="ess-table supply-listing-table" ng-show="data.reqs.processing.length > 0">
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
      <tr ng-repeat="requisition in data.reqs.processing | orderBy:'requisitionId':true"
          ng-class="calculateHighlighting(requisition)"
          ng-click="setRequisitionSearchParam(requisition.requisitionId)">
        <td ng-class="{'supply-pickup-icon': requisition.deliveryMethod === 'PICKUP'}"></td>
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
  <div ng-show="!data.reqRequest.response.$resolved">
    <div class="content-container">
      <h1 style="background: #799933; color: white;">Completed Requisition Requests</h1>
    </div>
    <div loader-indicator class="sm-loader"></div>
  </div>

  <%--Done Loading Completed--%>
  <div class="content-container" ng-show="data.reqRequest.response.$resolved">
    <h1 style="background: #799933; color: white;">Completed Requisition Requests</h1>

    <div class="content-info" ng-show="data.reqs.completed.length === 0">
      <h2 class="dark-gray">No Completed Requests.</h2>
    </div>

    <table class="ess-table supply-listing-table" ng-show="data.reqs.completed.length > 0">
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
      <tr ng-repeat="requisition in data.reqs.completed | orderBy:'requisitionId':true"
          ng-click="setRequisitionSearchParam(requisition.requisitionId)">
        <td ng-class="{'supply-pickup-icon': requisition.deliveryMethod === 'PICKUP'}"></td>
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
  <div ng-show="!data.reqRequest.response.$resolved">
    <div class="content-container">
      <h1 style="background: #6270BD; color: white;">Approved Requisition Requests</h1>
    </div>
    <div loader-indicator class="sm-loader"></div>
  </div>

  <%--Done Loading Approved--%>
  <div class="content-container" ng-show="data.reqRequest.response.$resolved">
    <h1 style="background: #6270BD; color: white;">Approved Requisition Requests</h1>

    <div class="content-info" ng-show="data.reqs.approved.length === 0">
      <h2 class="dark-gray">No Approved Requests.</h2>
    </div>

    <table class="ess-table supply-listing-table" ng-show="data.reqs.approved.length > 0">
      <thead>
      <tr>
        <th></th>
        <th>Id</th>
        <th>Location</th>
        <th>Employee</th>
        <th>Item Count</th>
        <th>Approved Date</th>
        <th>Issuing Employee</th>
        <th>Sync Status</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="requisition in data.reqs.approved | orderBy:'requisitionId':true"
          ng-click="setRequisitionSearchParam(requisition.requisitionId)">
        <td ng-class="{'supply-pickup-icon': requisition.deliveryMethod === 'PICKUP'}"></td>
        <td>{{requisition.requisitionId}}</td>
        <td>{{requisition.destination.locId}}</td>
        <td>{{requisition.customer.lastName}}</td>
        <td>{{distinctItemQuantity(requisition)}}</td>
        <td>{{requisition.approvedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
        <td>{{requisition.issuer.lastName}}</td>
        <td><span class="tick" ng-show="requisition.lastSfmsSyncDateTime && requisition.savedInSfms"></span>
          <span class="cross"  ng-show="requisition.lastSfmsSyncDateTime && !requisition.savedInSfms"></span></td>
      </tr>
      </tbody>
    </table>
  </div>

  <%--  Rejected Shipments   --%>

  <div class="content-container" ng-show="data.reqs.rejected.length > 0">
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
      <tr ng-repeat="requisition in data.reqs.rejected | orderBy:'requisitionId':true"
          ng-click="setRequisitionSearchParam(requisition.requisitionId)">
        <td>{{requisition.requisitionId}}</td>
        <td>{{requisition.destination.locId}}</td>
        <td>{{requisition.customer.lastName}}</td>
        <td>{{distinctItemQuantity(requisition)}}</td>
        <td>{{requisition.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
      </tr>
      </tbody>
    </table>
  </div>

  <%--Container for all modal dialogs--%>
  <div modal-container>
    <modal modal-id="fulfillment-editing-modal">
      <div fulfillment-editing-modal
           supply-employees='data.supplyEmployees'
           location-statistics='data.locationStatistics'>
      </div>
    </modal>

    <modal modal-id="fulfillment-immutable-modal">
      <div fulfillment-immutable-modal></div>
    </modal>
  </div>
</div>
