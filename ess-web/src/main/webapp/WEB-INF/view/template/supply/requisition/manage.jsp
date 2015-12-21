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
        <div class="col-2-12">
          Location
        </div>
        <div class="col-2-12">
          Employee
        </div>
        <div class="col-2-12">
          Quantity
        </div>
        <div class="col-2-12">
          Order Date
        </div>
        <div class="col-2-12">
          Process Order
        </div>
        <div class="col-2-12">
          Reject Order
        </div>
      </div>

      <div  ng-repeat="order in pendingOrders">
        <div class="grid grid-padding supply-manage-rows" ng-class="{warn: highlightOrder(order)}">
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{order.location}}
          </div>
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{order.customer.lastName}}
          </div>
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{getOrderQuantity(order)}}
          </div>
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{order.orderDateTime | date:'MM/dd/yyyy h:mm a'}}
          </div>
          <div class="col-2-12 supply-button-cell">
            <a target="_blank" href="${ctxPath}/supply/requisition/view?order={{order.id}}&print=true">
            <input ng-click="processOrder(order)" class="submit-button" type="button" value="Process" style="padding: 3px 8px">
            </a>
          </div>
          <div class="col-2-12 supply-button-cell">
            <input ng-click="rejectOrder(order)" class="reject-button" type="button" value="Reject" style="padding: 3px 8px">
          </div>
        </div>

        <%--Order details--%>
        <div ng-show="selectedOrder(order)" class="supply-details-table">
          <div class="grid grid-padding supply-detail-header">
            <div class="col-3-12">
              Commodity Code
            </div>
            <div class="col-3-12">
              Item Name
            </div>
            <div class="col-3-12">
              Unit Size
            </div>
            <div class="col-3-12">
              Quantity
            </div>
          </div>
          <div ng-repeat="lineItem in order.items" class="grid grid-padding supply-detail-rows" ng-class="{warn: highlightLineItem(lineItem)}">
            <div class="col-3-12">
              {{getItemCommodityCode(lineItem.itemId)}}
            </div>
            <div class="col-3-12">
              {{getItemName(lineItem.itemId)}}
            </div>
            <div class="col-3-12">
              {{getItemUnitSize(lineItem.itemId)}}/Pack
            </div>
            <div class="col-3-12">
              {{lineItem.quantity}}
            </div>
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
          Quantity
        </div>
        <div class="col-2-12">
          Order Date
        </div>
        <div class="col-2-12">
          Issuing Employee
        </div>
        <div class="col-2-12">
          Complete Order
        </div>
      </div>

      <div  ng-repeat="order in processingOrders">
        <div class="grid grid-padding supply-manage-rows">
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{order.location}}
          </div>
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{order.customer.lastName}}
          </div>
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{getOrderQuantity(order)}}
          </div>
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{order.orderDateTime | date:'MM/dd/yyyy h:mm a'}}
          </div>
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{order.issuingEmployee.lastName}}
          </div>
          <div class="col-2-12 supply-button-cell">
            <input ng-click="completeOrder(order)" class="submit-button" type="button" value="Complete" style="padding: 3px 8px">
          </div>
        </div>

        <%--Order details--%>
        <div ng-show="selectedOrder(order)" class="supply-details-table">
          <div class="grid grid-padding supply-detail-header">
            <div class="col-3-12">
              Commodity Code
            </div>
            <div class="col-3-12">
              Item Name
            </div>
            <div class="col-3-12">
              Unit Size
            </div>
            <div class="col-3-12">
              Quantity
            </div>
          </div>
          <div ng-repeat="lineItem in order.items" class="grid grid-padding supply-detail-rows">
            <div class="col-3-12">
              {{getItemCommodityCode(lineItem.itemId)}}
            </div>
            <div class="col-3-12">
              {{getItemName(lineItem.itemId)}}
            </div>
            <div class="col-3-12">
              {{getItemUnitSize(lineItem.itemId)}}/Pack
            </div>
            <div class="col-3-12">
              {{lineItem.quantity}}
            </div>
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
          Quantity
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
        <div class="grid grid-padding supply-manage-rows" ng-click="setSelected(order)">
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

        <%--Order details--%>
        <div ng-show="selectedOrder(order)" class="supply-details-table">
          <div class="grid grid-padding supply-detail-header">
            <div class="col-3-12">
              Commodity Code
            </div>
            <div class="col-3-12">
              Item Name
            </div>
            <div class="col-3-12">
              Unit Size
            </div>
            <div class="col-3-12">
              Quantity
            </div>
          </div>
          <div ng-repeat="lineItem in order.items" class="grid grid-padding supply-detail-rows">
            <div class="col-3-12">
              {{getItemCommodityCode(lineItem.itemId)}}
            </div>
            <div class="col-3-12">
              {{getItemName(lineItem.itemId)}}
            </div>
            <div class="col-3-12">
              {{getItemUnitSize(lineItem.itemId)}}/Pack
            </div>
            <div class="col-3-12">
              {{lineItem.quantity}}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

</div>
