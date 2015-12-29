<div ng-controller="SupplyViewController">
  <div class="supply-order-hero">
    <h2>Requisition Order</h2>
  </div>

  <div class="content-container">
    <div class="content-info">
      <div class="grid padding-10">
        <div class="col-6-12">
          <b>Location Code:</b> {{order.location.code + '-' + order.location.locationTypeCode}}
        </div>
        <div class="col-6-12">
          <b>Order Date:</b> {{order.orderDateTime | date:'MM/dd/yyyy h:mm a'}}
        </div>
      </div>
      <div class="grid padding-10">
        <div class="col-6-12">
          <b>Ordered By:</b> {{order.customer.lastName}}
        </div>
        <div class="col-6-12" ng-show="order.completedDateTime">
          <b>Completed Date:</b> {{order.completedDateTime | date:'MM/dd/yyyy h:mm a'}}
        </div>
      </div>
      <div class="grid padding-10">
        <div class="col-6-12" ng-show="order.issuingEmployee.lastName">
          <b>Issued By:</b> {{order.issuingEmployee.lastName}}
        </div>
        <div class="col-6-12 no-print">
          <a class="" href="javascript:if(window.print)window.print()">
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
          <th>Quantity</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="lineItem in order.items">
          <td>{{getItemCommodityCode(lineItem.itemId)}}</td>
          <td>{{getItemName(lineItem.itemId)}}</td>
          <td>{{lineItem.quantity}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>