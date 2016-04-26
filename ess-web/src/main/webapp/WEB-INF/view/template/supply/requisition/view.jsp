<div ng-controller="SupplyViewController">
  <div class="supply-order-hero">
    <h2>Requisition Order</h2>
  </div>

  <div class="content-container large-print-font-size">
    <div class="content-info">
      <div class="grid padding-10">
        <div class="col-6-12">
          <b>Location Code:</b> {{order.activeVersion.destination.code + '-' + order.activeVersion.destination.locationTypeCode}}
        </div>
        <div class="col-6-12">
          <b>Order Date:</b> {{order.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}
        </div>
      </div>
      <div class="grid padding-10">
        <div class="col-6-12">
          <b>Ordered By:</b> {{order.activeVersion.customer.lastName}}
        </div>
        <%--<div class="col-6-12" ng-show="order.completedDateTime">--%>
          <%--<b>Completed Date:</b> {{order.completedDateTime | date:'MM/dd/yyyy h:mm a'}}--%>
        <%--</div>--%>
      </div>
      <div class="grid padding-10">
        <div class="col-6-12">
          <b>Issued By:</b> {{shipment.activeVersion.issuer.lastName}}
        </div>
        <div class="col-6-12 no-print">
          <a class="" href="javascript:if(window.print)window.print()">
            Print Page
          </a>
        </div>
      </div>
    </div>
  </div>

  <div class="content-container large-print-font-size">
    <div class="padding-10">
      <table class="ess-table supply-listing-table">
        <thead>
        <tr>
          <th>Commodity Code</th>
          <th>Item</th>
          <th>Quantity</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="lineItem in order.activeVersion.lineItems">
          <td>{{lineItem.item.commodityCode}}</td>
          <td>{{lineItem.item.description}}</td>
          <td>{{lineItem.quantity}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>