<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<%--Title--%>

<div class="padding-10">
  <div>
    <h3 class="content-info">Order from {{shipment.activeVersion.customer.firstName}}
      {{shipment.activeVersion.customer.initial}} {{shipment.activeVersion.customer.lastName}}</h3>
  </div>

  <%--Order content--%>

  <div class="grid grid-padding">
    <div class="col-8-12">
      <div style="overflow-y: auto; max-height: 300px;">
        <div editable-order-listing></div>
      </div>

      <%--Add item--%>
      <div class="padding-10">
        <label> Add Commodity Code:
        <input type="text"
               ng-model="addItemFeature.newItemCommodityCode"
               ui-autocomplete="addItemAutocompleteOptions"
               style="width: 100px; height: 20px;">
        </label>
        <input ng-click="addItem()" class="neutral-button" type="button" value="Add Item">
      </div>

      <%-- Add Note --%>
      <div class="padding-top-10">
        <label class="col-1-12">note:</label>
        <textarea class="col-11-12" ng-model="displayedVersion.note"
                  ng-change="onUpdate()"></textarea>
      </div>
    </div>

    <%--Right Margin--%>

    <div class="col-4-12">

      <%--Change Location--%>
      <h4 class="content-info">Location:
        <input type="text"
               ng-model="dirtyLocationCode"
               ui-autocomplete="locationAutocompleteOptions"
               ng-change="onLocationUpdated()"
               style="width: 100px">
      </h4>

      <h4 class="content-info">Ordered: {{shipment.orderedDateTime | date:'MM/dd/yy h:mm a'}}</h4>
      <div class="text-align-center" style="padding-bottom: 25px; padding-top: 10px">
        <a target="_blank" href="${ctxPath}/supply/requisition/requisition-view?requisition={{shipment.id}}&print=true">
          Print
        </a>
        <a target="#" ng-click="close()" style="padding-left: 30px">
          Exit
        </a>
      </div>

      <%--Assign issuer--%>

      <div class="text-align-center" style="padding-bottom: 20px;">
        <label>Assign to: </label>
        <select ng-model="displayedVersion.issuer"
                ng-options="emp.fullName for emp in supplyEmployees track by emp.employeeId"
                ng-change="onUpdate()">
        </select>
      </div>

      <%--Rejection Confirmation template TODO: generalize this so it can be used elsewhere--%>
      <script type="text/ng-template" id="confirm">
        <div class="margin-10">
          <h4 class="content-info">Are you sure?</h4>
          <div class="">
            <input ng-click="closeModal()" class="neutral-button" type="button" value="Cancel">
            <input ng-click="rejectOrder()" class="reject-button" type="button" value="Reject">
          </div>
        </div>
      </script>

      <%--Actions--%>

      <input ng-show="shipment.activeVersion.status === 'PENDING'" ng-click="processOrder()" class="submit-button col-4-12" type="button" value="Process">
      <input ng-show="shipment.activeVersion.status === 'PROCESSING'" ng-click="completeOrder()" class="submit-button col-4-12" type="button" value="Complete">
      <shiro:hasPermission name="supply:shipment:approve">
        <input ng-show="shipment.activeVersion.status === 'COMPLETED'" ng-click="approveShipment()" class="submit-button col-4-12" type="button" value="Approve">
      </shiro:hasPermission>
      <input ng-click="saveChanges()" class="submit-button col-4-12" type="button" value="Save" ng-disabled="!dirty">
      <input ns-popover ns-popover-template="confirm" ns-popover-timeout="0.5"
             ng-show="shipment.activeVersion.status === 'PENDING' || shipment.activeVersion.status === 'PROCESSING'"
             class="reject-button col-4-12" type="button" value="Reject">
    </div>
  </div>
</div>
