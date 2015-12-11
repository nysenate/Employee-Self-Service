<div ng-controller="SupplyViewController">
  <div class="supply-order-hero">
    <h2>Requisition Order</h2>
  </div>

  <div class="content-container">
    <div class="order-view-header">
      <div class="grid grid-padding padding-10" style="text-align: center">
        <div class="col-6-12 supply-title">
          Location Code: {{order.locCode}}-{{order.locType}}
        </div>
        <div class="col-6-12 supply-title">
          Order Date: {{order.dateTime.format('YYYY-MM-DD hh:mm A')}}
        </div>
      </div>
      <div class="grid grid-padding padding-10" style="text-align: center">
        <div class="col-6-12 supply-title">
          Ordered By: {{order.purchaser}}
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
        <tr ng-class="{warn: highlightOrder(order)}" ng-repeat="item in order.items">
          <td>{{item.product.commodityCode}}</td>
          <td>{{item.product.name}}</td>
          <td>{{item.product.unitSize}}</td>
          <td>{{item.quantity}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>