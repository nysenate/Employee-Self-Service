<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<%--Title--%>

<div class="padding-10">
  <div>
    <h3 class="content-info">Order from {{shipment.order.activeVersion.customer.firstName}}
      {{shipment.order.activeVersion.customer.initial}} {{shipment.order.activeVersion.customer.lastName}}</h3>
  </div>

  <%--Order content--%>

  <div class="grid grid-padding">
    <div class="col-8-12">
      <div class="content-container" style="overflow-y: auto; max-height: 300px;">
        <div editable-order-listing></div>
      </div>
      <div class="padding-top-10">
        <label class="padding-10" style="vertical-align: middle;">note:</label>
        <textarea style="vertical-align: middle;" ng-model="note"
                  ng-change="onNoteUpdated()" rows="3", cols="65"></textarea>
      </div>
    </div>

    <%--Right Margin--%>

    <div class="col-4-12">


      <h4 class="content-info">Location:
        <input type="text"
               ng-model="dirtyLocationCode"
               ui-autocomplete="locationOption"
               ng-change="onAutocompleteUpdated()"
               style="width: 100px">
      </h4>
      <h4 class="content-info">Ordered: {{shipment.order.orderedDateTime | date:'MM/dd/yy h:mm a'}}</h4>
      <div class="text-align-center" style="padding-bottom: 25px; padding-top: 10px">
        <a target="_blank" href="${ctxPath}/supply/requisition/view?order={{shipment.order.id}}&print=true">
          Print
        </a>
        <a target="#" ng-click="close()" style="padding-left: 30px">
          Exit
        </a>
      </div>

      <%--Assign issuer--%>

      <div class="text-align-center" style="padding-bottom: 20px;">
        <label>Assign to: </label>
        <select ng-model="dirtyShipment.activeVersion.issuer"
                ng-options="emp.fullName for emp in supplyEmployees track by emp.employeeId"
                ng-change="onUpdate()">
        </select>
      </div>

      <%--Actions--%>

      <input ng-show="shipment.activeVersion.status === 'PENDING'" ng-click="processOrder()" class="submit-button col-4-12" type="button" value="Process">
      <input ng-show="shipment.activeVersion.status === 'PROCESSING'" ng-click="completeOrder()" class="submit-button col-4-12" type="button" value="Complete">
      <shiro:hasPermission name="supply:shipment:approve">
        <input ng-show="shipment.activeVersion.status === 'COMPLETED'" ng-click="approveShipment()" class="submit-button col-4-12" type="button" value="Approve">
      </shiro:hasPermission>
      <input ng-click="saveOrder(order)" class="submit-button col-4-12" type="button" value="Save" ng-disabled="!dirty">
      <input ng-show="shipment.activeVersion.status === 'PENDING' || shipment.activeVersion.status === 'PROCESSING'"
             ng-click="rejectOrder()" class="reject-button col-4-12" type="button" value="Reject">
    </div>
  </div>
</div>
