<div ng-controller="SupplyManageController">
  <div class="supply-order-hero">
    <h2>Manage Requisitions</h2>
  </div>

  <div class="content-container">
    <h1>Pending Requisition Requests</h1>
    <div style="padding: 0px 10px 10px 10px;">
      <div class="grid grid-padding supply-manage-header">
        <div class="col-2-12">
          Location Code
        </div>
        <div class="col-2-12">
          Location Type
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
      </div>

      <div  ng-repeat="order in pendingOrders()">
        <div class="grid grid-padding supply-manage-rows" ng-click="setSelected(order)">
          <div class="col-2-12">
            {{order.locCode}}
          </div>
          <div class="col-2-12">
            {{order.locType}}
          </div>
          <div class="col-2-12">
            {{order.purchaser}}
          </div>
          <div class="col-2-12">
            {{getOrderQuantity(order)}}
          </div>
          <div class="col-2-12">
            {{order.dateTime.format('YYYY-MM-DD hh:mm A')}}
          </div>
          <div class="col-2-12">
            Process Btn
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
