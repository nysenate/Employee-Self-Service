<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--Title--%>

<div class="padding-10">
  <div>
    <h3 class="content-info">Order from {{shipment.activeVersion.customer.firstName}}
      {{shipment.activeVersion.customer.initial}} {{shipment.activeVersion.customer.lastName}}</h3>
  </div>

  <%--Immutable Order content--%>

  <div class="grid grid-padding">
    <div class="col-8-12">
      <div class="content-container" style="overflow-y: auto; max-height: 300px;">
        <table class="ess-table supply-listing-table">
          <thead>
          <tr>
            <th>Commodity Code</th>
            <th>Item</th>
            <th>Quantity</th>
          </tr>
          </thead>
          <tbody>
          <tr ng-class="{warn: highlightLineItem(lineItem)}" ng-repeat="lineItem in shipment.activeVersion.lineItems">
            <td>{{lineItem.item.commodityCode}}</td>
            <td>{{lineItem.item.description}}</td>
            <td>{{lineItem.quantity}}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <div class="padding-top-10"
           ng-show="shipment.activeVersion.note">
        <div class="col-2-12 bold">
          Note:
        </div>
        <div class="col-10-12">
          {{shipment.activeVersion.note}}
        </div>
      </div>
    </div>

    <%--Right Margin--%>

    <div class="col-4-12">
      <h4 class="content-info">Location: {{shipment.activeVersion.destination.locId}}</h4>
      <h4 class="content-info">Ordered: {{shipment.orderedDateTime | date:'MM/dd/yy h:mm a'}}</h4>
      <h4 class="content-info">Issued By: {{shipment.activeVersion.issuer.lastName}}</h4>
      <h4 class="content-info" ng-show="shipment.activeVersion.status !== 'CANCELED'" >Approved: {{shipment.approvedDateTime | date:'MM/dd/yy h:mm a'}}</h4>

      <div class="text-align-center" style="padding-bottom: 25px; padding-top: 10px">
        <a target="_blank" href="${ctxPath}/supply/requisition/requisition-view?requisition={{shipment.id}}&print=true">
          Print
        </a>
        <a target="#" ng-click="close()" style="padding-left: 30px">
          Exit
        </a>
      </div>

      <div class="text-align-center" ng-show="shipment.activeVersion.status === 'CANCELED'">
        <input ng-click="acceptShipment(shipment)" class="submit-button" type="button" value="Accept">
      </div>

    </div>
  </div>
</div>