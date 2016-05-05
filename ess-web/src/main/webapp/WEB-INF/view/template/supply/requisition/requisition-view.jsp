<div ng-controller="SupplyViewController">
  <div class="supply-order-hero">
    <h2>Requisition Order</h2>
  </div>

  <div loader-indicator class="loader"
       ng-show="!shipmentResource.$resolved"></div>

  <%--Version selection--%>
  <div ng-show="shipmentResource.$resolved">
    <div class="content-container large-print-font-size">
      <div class="content-info">
        <label>Select Version:</label>
        <select ng-model="selectedVersion"
                ng-options="version.name for version in requisitionHistory.versions track by version.name">
        </select>
        <a class="float-right" style="padding: 5px 20px 0px 0px;" href="javascript:if(window.print)window.print()">
          Print Page
        </a>
      </div>
    </div>

    <%--General information--%>
    <div class="content-container large-print-font-size">
      <div class="content-info">
        <div class="grid padding-10">
          <div class="col-4-12">
            <b>Requested By:</b> {{selectedVersion.order.customer.lastName}}
          </div>
          <div class="col-4-12">
            <b>Requesting Office:</b> {{selectedVersion.order.destination.locId}}
          </div>
          <div class="col-4-12">
            <b>Requested Date:</b> {{shipment.order.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}
          </div>
        </div>
        <div class="grid padding-10">
          <div class="col-4-12">
            <b>Status:</b> {{selectedVersion.shipment.status}}
          </div>
          <div class="col-4-12">
            <b>Issued By:</b> {{selectedVersion.shipment.issuer.lastName}}
          </div>
          <div class="col-4-12">
            <b>Issued Date:</b> {{shipment.completedDateTime | date:'MM/dd/yyyy h:mm a'}}
          </div>
        </div>
        <div class="grid padding-10">
          <div class="col-4-12">
            <b>Modified By:</b> {{selectedVersion.modifiedBy.lastName}}
          </div>
        </div>
      </div>
    </div>


    <%--Note--%>
    <div class="content-container no-print"
         ng-show="selectedVersion.order.note">
      <div class="content-info">
        <div class="grid padding-10">
          <div class="col-2-12">
            Note:
          </div>
          <div class="col-10-12">
            {{selectedVersion.order.note}}
          </div>
        </div>
      </div>
    </div>

    <%--Order Items--%>
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
          <tr ng-repeat="lineItem in sortSelectedVersionLineItems()">
            <td>{{lineItem.item.commodityCode}}</td>
            <td>{{lineItem.item.description}}</td>
            <td>{{lineItem.quantity}}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="print-only large-print-font-size" style="margin-top: 60px; padding: 20px;">
      Received By: ________________________________
    </div>
  </div>
</div>