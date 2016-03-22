<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--Title--%>

<div class="padding-10">
  <div>
    <h3 class="content-info">Order from {{shipment.order.activeVersion.customer.firstName}}
      {{shipment.order.activeVersion.customer.initial}} {{shipment.order.activeVersion.customer.lastName}}</h3>
  </div>

  <%--Order content--%>

  <div class="grid grid-padding">
    <div class="col-8-12">
      <div class="content-container">
        <table class="ess-table supply-listing-table">
          <thead>
          <tr>
            <th>Commodity Code</th>
            <th>Item Name</th>
            <th>Quantity</th>
          </tr>
          </thead>
          <tbody>
          <tr ng-class="{warn: highlightLineItem(lineItem)}" ng-repeat="lineItem in shipment.order.activeVersion.lineItems">
            <td>{{getItemCommodityCode(lineItem.item.id)}}</td>
            <td>{{getItemName(lineItem.item.id)}}</td>
            <td>{{lineItem.quantity}}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <%--Right Margin--%>

    <div class="col-4-12">
      <h4 class="content-info">Location: {{shipment.order.activeVersion.destination.code + '-' + shipment.order.activeVersion.destination.locationTypeCode}}</h4>
      <h4 class="content-info">Ordered: {{shipment.order.orderedDateTime | date:'MM/dd/yy h:mm a'}}</h4>
      <h4 class="content-info">Issued By: {{shipment.activeVersion.issuer.lastName}}</h4>
      <h4 class="content-info">Completed: {{shipment.completedDateTime | date:'MM/dd/yy h:mm a'}}</h4>

      <div class="text-align-center" style="padding-bottom: 25px; padding-top: 10px">
        <a target="_blank" href="${ctxPath}/supply/requisition/view?order={{shipment.order.id}}&print=true">
          Print
        </a>
        <a target="#" ng-click="close()" style="padding-left: 30px">
          Exit
        </a>
      </div>

      <div class="text-align-center">
        <input ng-click="undo(shipment)" class="reject-button" type="button" value="Undo">
      </div>
    </div>
  </div>
</div>