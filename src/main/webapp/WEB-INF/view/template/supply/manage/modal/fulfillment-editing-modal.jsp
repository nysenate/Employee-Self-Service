<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<%--Title--%>

<div class="padding-10">
  <div>
    <h3 class="content-info">
      <span style="padding-right:10px;" ng-class="{'icon-aircraft': originalRequisition.deliveryMethod === 'DELIVERY',
                     'icon-shopping-cart': originalRequisition.deliveryMethod === 'PICKUP'}"></span>
      <span ng-if="originalRequisition.status === 'PENDING'">Pending</span>
      <span ng-if="originalRequisition.status === 'PROCESSING'">Processing</span>
      <span ng-if="originalRequisition.status === 'COMPLETED'">Completed</span>
      Requisition {{originalRequisition.requisitionId}} Requested By {{originalRequisition.customer.fullName}}
    </h3>
  </div>

  <%--Order content--%>

  <div class="grid grid-padding content-info">
    <div class="col-8-12">
      <div style="overflow-y: auto; max-height: 300px;">
        <div editable-order-listing></div>
      </div>

      <%--Add item--%>
      <div class="padding-10">
        <label> Add Commodity Code:
          <input type="text"
                 ng-model="newItemCommodityCode"
                 ng-change="resetCode()"
                 ui-autocomplete="getItemAutocompleteOptions()"
                 style="width: 100px; height: 20px;" capitalize>

        </label>
        <input ng-click="addItem()" class="neutral-button" type="button" value="Add Item">
        <p class="redorange" ng-show="warning">Item: {{newItemCommodityCode}} already exists in this order. Please
          adjust the quantity if it's not correct.</p>
      </div>

      <%-- Add Note --%>
      <div ng-show="displayRejectInstructions" style="color: #ff0000;">
        A note must be given when rejecting a requisition.
      </div>
      <div class="padding-top-10">
        <label class="col-1-12">Note:</label>
        <textarea class="col-11-12"
                  ng-model="editableRequisition.note"
                  ng-change="onUpdate()"
                  ng-class="{'warn-option': displayRejectInstructions}">
        </textarea>
      </div>
    </div>

    <%--Right Margin--%>

    <div class="col-4-12 requisition-modal-right-margin">

      <%--Change Location--%>
      <h4>Location</h4>
      <input type="text"
             ng-model="newLocationCode"
             ui-autocomplete="getLocationAutocompleteOptions()"
             ng-change="onLocationUpdated()"
             style="width: 100px;">

      <h4>Special Instructions</h4>
      <div
          ng-if="originalRequisition.specialInstructions === null || originalRequisition.specialInstructions.length === 0">
        No instructions provided for this requisition.
      </div>
      <div
          ng-if="originalRequisition.specialInstructions !== null || originalRequisition.specialInstructions.length > 0"
          class="fulfillment-modal-special-instructions">
        {{originalRequisition.specialInstructions}}
      </div>

      <h4>Ordered Date Time </h4>
      <div>{{originalRequisition.orderedDateTime | date:'MM/dd/yy h:mm a'}}</div>

      <h4>Actions</h4>
      <div class="text-align-center">
        <a target="_blank"
           href="${ctxPath}/supply/requisition/requisition-view?requisition={{originalRequisition.requisitionId}}&print=true">
          Print Requisition
        </a>
      </div>
      <div ng-if="originalRequisition.status === 'COMPLETED'" class="text-align-center">
        <a target="_blank"
           href="${ctxPath}/supply/requisition/requisition-view?requisition={{originalRequisition.requisitionId}}">
          View History
        </a>
      </div>

      <%--Assign issuer--%>

      <div class="text-align-center" style="padding-bottom: 20px;">
        <label>Assign to: </label>
        <select ng-model="editableRequisition.issuer"
                ng-options="emp.fullName for emp in supplyEmployees track by emp.employeeId"
                ng-change="onUpdate()">
        </select>
      </div>
    </div>
  </div>

  <%--Action buttons--%>
  <div class="padding-top-10" style="text-align: center">

    <%--Cancel button--%>
    <input ng-click="closeModal()" class="neutral-button" style="width: 15%" type="button" value="Cancel">

    <%--Save button. Requires a change to be made.--%>
    <input ng-click="saveChanges()" class="submit-button" style="width: 15%" type="button" value="Save"
           ng-disabled="!dirty">

    <%--Process button. Current status must be pending.--%>
    <input ng-show="originalRequisition.status === 'PENDING'" ng-click="processReq()"
           class="process-button" style="width: 15%" type="button" value="Process">

    <%--Complete button. Current status must be PENDING.--%>
    <input ng-show="originalRequisition.status === 'PROCESSING'" ng-click="processReq()"
           class="complete-button" style="width: 15%" type="button" value="Complete">

    <%--Approve button. Requires current status is COMPLETED and logged in employee has appropriate permissions.--%>
    <shiro:hasPermission name="supply:requisition:approve">
      <input class="approve-button"
             style="width: 15%;"
             ng-click="approveShipment()"
             type="button"
             value="Approve"
             ng-show="originalRequisition.status === 'COMPLETED'"
             ns-popover ns-popover-template="approve-message"
             ns-popover-theme="ns-popover-tooltip-theme"
             ns-popover-placement="top"
             ns-popover-trigger="mouseenter"
             ns-popover-timeout="0.2">
      <script type="text/ng-template" id="approve-message">
        <div ng-show="selfApprove" class="triangle"></div>
        <div ng-show="selfApprove" class="ns-popover-tooltip">
          <p>You are not allowed to approve your own order</p>
        </div>
      </script>
    </shiro:hasPermission>

    <%--Reject button. Requires a note to be entered. Has a popup confirmation.--%>
    <input ns-popover ns-popover-template="confirmReject" ns-popover-timeout="0.5"
           ns-popover-theme="ns-popover-tooltip-theme" ns-popover-placement="top"
           ng-show="originalRequisition.status === 'PENDING' || originalRequisition.status === 'PROCESSING'"
           class="reject-button" style="width: 15%; float: right;"
           type="button" value="Reject">
    <script type="text/ng-template" id="confirmReject">
      <div class="triangle"></div>
      <div class="ns-popover-tooltip">
        <h4 class="content-info">Are you sure?</h4>
        <input ng-click="closeModal()" class="neutral-button" type="button" value="Cancel">
        <input ng-click="rejectOrder()" class="reject-button" type="button" value="Reject">
      </div>
    </script>
  </div>
</div>
