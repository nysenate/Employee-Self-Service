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
        <div editable-order-listing></div>
      </div>
    </div>

    <%--Right Margin--%>

    <div class="col-4-12">
      <h4 class="content-info">Location: {{shipment.order.activeVersion.destination.code + '-' + shipment.order.activeVersion.destination.locationTypeCode}}</h4>
      <h4 class="content-info">Ordered: {{shipment.order.orderedDateTime | date:'MM/dd/yy h:mm a'}}</h4>
      <div class="text-align-center" style="padding-bottom: 25px; padding-top: 10px">
        <a target="_blank" href="${ctxPath}/supply/requisition/view?order={{shipment.order.id}}&print=true">
          Print
        </a>
        <a target="#" ng-click="close()" style="padding-left: 30px">
          Exit
        </a>
      </div>

      <div class="text-align-center" ng-show="status === 'PROCESSING'" style="padding-bottom: 20px;">
        <label>Assign to: </label>
        <select ng-model="assignedTo" ng-change="setIssuedBy()" ng-options="emp for emp in supplyEmployees"></select>
      </div>

      <input ng-show="status === 'PENDING'" ng-click="processOrder()" class="submit-button col-4-12" type="button" value="Process">
      <input ng-show="status === 'PROCESSING'" ng-click="completeOrder()" class="submit-button col-4-12" type="button" value="Complete">
      <input ng-click="saveOrder(order)" class="submit-button col-4-12" type="button" value="Save" ng-disabled="!dirty">
      <input ng-click="rejectOrder(order)" class="reject-button col-4-12" type="button" value="Reject">
    </div>
  </div>
</div>
