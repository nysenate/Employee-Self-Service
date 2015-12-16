<div ng-controller="SupplyViewController">
  <div class="supply-order-hero">
    <h2>Requisition Order</h2>
  </div>

  <div class="content-container">
    <div class="order-view-header">
      <div class="grid grid-padding padding-10" style="text-align: center">
        <div class="col-6-12 supply-title">
          Location Code: {{order.location}}
        </div>
        <div class="col-6-12 supply-title">
          Order Date: {{order.orderDateTime}}
        </div>
      </div>
      <div class="grid grid-padding padding-10" style="text-align: center">
        <div class="col-6-12 supply-title">
          Ordered By: {{order.customer.lastName}}
        </div>
        <div class="col-6-12">
          <a class="supply-title" href="javascript:if(window.print)window.print()">
            Print Page
          </a>
        </div>
      </div>
    </div>
  </div>

  <div class="content-container">
    <div class="padding-10">
      <table class="ess-table supply-listing-table">
        <thead>
        <tr>
          <th>Commodity Code</th>
          <th>Item Name</th>
          <th>Unit Size</th>
          <th>Quantity</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-class="{warn: highlightOrder(order)}" ng-repeat="lineItem in order.items">
          <td>{{getItemCommodityCode(lineItem.itemId)}}</td>
          <td>{{getItemName(lineItem.itemId)}}</td>
          <td>{{getItemUnitSize(lineItem.itemId)}}</td>
          <td>{{lineItem.quantity}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>