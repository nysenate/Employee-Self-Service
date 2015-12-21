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

    <div style="padding: 0px 10px 10px 10px;" ng-show="pendingOrders.length > 0">
      <div class="grid grid-padding supply-manage-header">
        <div class="col-3-12">
          Location
        </div>
        <div class="col-3-12">
          Employee
        </div>
        <div class="col-3-12">
          Item Count
        </div>
        <div class="col-3-12">
          Order Date
        </div>
      </div>

      <div  ng-repeat="order in pendingOrders">
        <div class="grid grid-padding supply-manage-rows" ng-class="{warn: highlightOrder(order)}" ng-click="showPendingDetails(order)">
          <div class="col-3-12 supply-text-cell" >
            {{order.location}}
          </div>
          <div class="col-3-12 supply-text-cell">
            {{order.customer.lastName}}
          </div>
          <div class="col-3-12 supply-text-cell">
            {{getOrderQuantity(order)}}
          </div>
          <div class="col-3-12 supply-text-cell">
            {{order.orderDateTime | date:'MM/dd/yyyy h:mm a'}}
          </div>
        </div>
      </div>
    </div>
  </div>

  <%--   Processing Orders   --%>
  <div class="content-container">
    <h1 style="background: #4196A7; color: white;">Processing Requisition Requests</h1>

    <div class="content-info" ng-show="processingOrders.length == 0">
      <h2 class="dark-gray">No Processing Requests.</h2>
    </div>

    <div style="padding: 0px 10px 10px 10px;" ng-show="processingOrders.length > 0">
      <div class="grid grid-padding supply-manage-header">
        <div class="col-2-12">
          Location
        </div>
        <div class="col-2-12">
          Employee
        </div>
        <div class="col-2-12">
          Item Count
        </div>
        <div class="col-3-12">
          Order Date
        </div>
        <div class="col-3-12">
          Issuing Employee
        </div>
      </div>

      <div  ng-repeat="order in processingOrders">
        <div class="grid grid-padding supply-manage-rows" ng-click="showProcessingDetails(order)">
          <div class="col-2-12 supply-text-cell">
            {{order.location}}
          </div>
          <div class="col-2-12 supply-text-cell">
            {{order.customer.lastName}}
          </div>
          <div class="col-2-12 supply-text-cell">
            {{getOrderQuantity(order)}}
          </div>
          <div class="col-3-12 supply-text-cell">
            {{order.orderDateTime | date:'MM/dd/yyyy h:mm a'}}
          </div>
          <div class="col-3-12 supply-text-cell">
            {{order.issuingEmployee.lastName}}
          </div>
        </div>
      </div>
    </div>
  </div>

  <%--   Completed Orders   --%>
  <div class="content-container">
    <h1 style="background: #799933; color: white;">Completed Requisition Requests</h1>

    <div class="content-info" ng-show="completedOrders.length == 0">
      <h2 class="dark-gray">No Completed Requests.</h2>
    </div>

    <div style="padding: 0px 10px 10px 10px;" ng-show="completedOrders.length > 0">
      <div class="grid grid-padding supply-manage-header">
        <div class="col-2-12">
          Location
        </div>
        <div class="col-2-12">
          Employee
        </div>
        <div class="col-2-12">
          Item Count
        </div>
        <div class="col-2-12">
          Order Date
        </div>
        <div class="col-2-12">
          Completed Date
        </div>
        <div class="col-2-12">
          Issued By
        </div>
      </div>

      <div  ng-repeat="order in completedOrders">
        <div class="grid grid-padding supply-manage-rows" ng-click="showCompletedDetails(order)">
          <div class="col-2-12 supply-text-cell">
            {{order.location}}
          </div>
          <div class="col-2-12 supply-text-cell">
            {{order.customer.lastName}}
          </div>
          <div class="col-2-12 supply-text-cell">
            {{getOrderQuantity(order)}}
          </div>
          <div class="col-2-12 supply-text-cell">
            {{order.orderDateTime | date:'MM/dd/yyyy h:mm a'}}
          </div>
          <div class="col-2-12 supply-text-cell">
            {{order.completedDateTime | date:'MM/dd/yyyy h:mm a'}}
          </div>
          <div class="col-2-12 supply-text-cell">
            {{order.issuingEmployee.lastName}}
          </div>
        </div>
      </div>
    </div>
  </div>

  <% /** Container for all modal dialogs */ %>
  <div modal-container>
    <div manage-pending-modal ng-if="isOpen('manage-pending-modal')"></div>
    <div manage-processing-modal ng-if="isOpen('manage-processing-modal')"></div>
    <div manage-completed-modal ng-if="isOpen('manage-completed-modal')"></div>

  </div>

</div>
