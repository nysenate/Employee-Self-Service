<div ng-controller="SupplyManageController">
  <div class="supply-order-hero">
    <h2>Manage Requisitions</h2>
  </div>

  <%--   Pending Orders   --%>
  <div class="content-container">
    <h1 style="background: #d19525; color: white;">Pending Requisition Requests</h1>
    <div style="padding: 0px 10px 10px 10px;">
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

      <div  ng-repeat="order in pendingOrders()">
        <div class="grid grid-padding supply-manage-rows" ng-class="{warn: highlightOrder(order)}">
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{order.locCode}} - {{order.locType}}
          </div>
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{order.purchaser}}
          </div>
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{getOrderQuantity(order)}}
          </div>
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{order.dateTime.format('MM-DD-YYYY hh:mm A')}}
          </div>
          <div class="col-2-12 supply-button-cell">
            <input ng-click="processOrder(order)" class="submit-button" type="button" value="Process" style="padding: 3px 8px">
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
          <div ng-repeat="item in order.items" class="grid grid-padding supply-detail-rows" ng-class="{warn: highlightLineItem(item)}">
            <div class="col-3-12">
              {{item.product.commodityCode}}
            </div>
            <div class="col-3-12">
              {{item.product.name}}
            </div>
            <div class="col-3-12">
              {{item.product.unitSize}}/Pack
            </div>
            <div class="col-3-12">
              {{item.quantity}}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <%--   InProcess Orders   --%>
  <div class="content-container">
    <h1 style="background: #4196A7; color: white;">Inprocess Requisition Requests</h1>
    <div style="padding: 0px 10px 10px 10px;">
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

      <div  ng-repeat="order in inprocessOrders()">
        <div class="grid grid-padding supply-manage-rows">
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{order.locCode}} - {{order.locType}}
          </div>
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{order.purchaser}}
          </div>
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{getOrderQuantity(order)}}
          </div>
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{order.dateTime.format('MM-DD-YYYY hh:mm A')}}
          </div>
          <div class="col-2-12 supply-text-cell" ng-click="setSelected(order)">
            {{order.issueEmployee}}
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
          <div ng-repeat="item in order.items" class="grid grid-padding supply-detail-rows">
            <div class="col-3-12">
              {{item.product.commodityCode}}
            </div>
            <div class="col-3-12">
              {{item.product.name}}
            </div>
            <div class="col-3-12">
              {{item.product.unitSize}}/Pack
            </div>
            <div class="col-3-12">
              {{item.quantity}}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <%--   Completed Orders   --%>
  <div class="content-container">
    <h1 style="background: #799933; color: white;">Completed Requisition Requests</h1>
    <div style="padding: 0px 10px 10px 10px;">
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
          Issued By
        </div>
      </div>

      <div  ng-repeat="order in completedOrders()">
        <div class="grid grid-padding supply-manage-rows" ng-click="setSelected(order)">
          <div class="col-2-12 supply-text-cell">
            {{order.locCode}} - {{order.locType}}
          </div>
          <div class="col-2-12 supply-text-cell">
            {{order.purchaser}}
          </div>
          <div class="col-2-12 supply-text-cell">
            {{getOrderQuantity(order)}}
          </div>
          <div class="col-2-12 supply-text-cell">
            {{order.dateTime.format('MM-DD-YYYY hh:mm A')}}
          </div>
          <div class="col-2-12 supply-text-cell">
            {{order.issueEmployee}}
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
          <div ng-repeat="item in order.items" class="grid grid-padding supply-detail-rows">
            <div class="col-3-12">
              {{item.product.commodityCode}}
            </div>
            <div class="col-3-12">
              {{item.product.name}}
            </div>
            <div class="col-3-12">
              {{item.product.unitSize}}/Pack
            </div>
            <div class="col-3-12">
              {{item.quantity}}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

</div>
