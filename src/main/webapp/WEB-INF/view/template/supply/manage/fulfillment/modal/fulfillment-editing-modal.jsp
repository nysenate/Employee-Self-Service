<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<%--Title--%>

<div class="padding-10">
  <div>
    <h3 class="content-info">
      <span ng-if="originalRequisition.status === 'PENDING'">Pending</span>
      <span ng-if="originalRequisition.status === 'PROCESSING'">Processing</span>
      <span ng-if="originalRequisition.status === 'COMPLETED'">Completed</span>
      requisition requested by {{originalRequisition.customer.fullName}}
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
                 ui-autocomplete="getItemAutocompleteOptions()"
                 style="width: 100px; height: 20px;" capitalize>
        </label>
        <input ng-click="addItem()" class="neutral-button" type="button" value="Add Item">
      </div>

      <%-- Add Note --%>
      <div class="padding-top-10">
        <label class="col-1-12">Note:</label>
        <textarea class="col-11-12" ng-model="editableRequisition.note"
                  ng-change="onUpdate()"></textarea>
      </div>
    </div>

    <%--Right Margin--%>

    <div class="col-4-12">

      <%--Change Location--%>
      <h4 class="content-info">Location:
        <input type="text"
               ng-model="newLocationCode"
               ui-autocomplete="getLocationAutocompleteOptions()"
               ng-change="onLocationUpdated()"
               style="width: 100px">
      </h4>

      <h4 class="content-info">Ordered: {{originalRequisition.orderedDateTime | date:'MM/dd/yy h:mm a'}}</h4>
      <div class="text-align-center" style="padding-bottom: 25px; padding-top: 10px">
        <a target="_blank"
           href="${ctxPath}/supply/requisition/requisition-view?requisition={{originalRequisition.requisitionId}}&print=true">
          Print
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
    <%--Rejection Confirmation template TODO: generalize this so it can be used elsewhere--%>
    <script type="text/ng-template" id="confirmReject">
      <div class="triangle"></div>
      <div class="margin-10">
        <h4 class="content-info">Are you sure?</h4>
        <div class="">
          <input ng-click="closeModal()" class="neutral-button" type="button" value="Cancel">
          <input ng-click="rejectOrder()" class="reject-button" type="button" value="Reject">
        </div>
      </div>
    </script>

    <%--Cancel button--%>
    <input ng-click="closeModal()" class="neutral-button" style="width: 15%" type="button" value="Cancel">

    <%--Save button. Requires a change to be made.--%>
    <input ng-click="saveChanges()" class="submit-button" style="width: 15%" type="button" value="Save"
           ng-disabled="!dirty">

    <%--Process button. Current status must be pending.--%>
    <input ng-show="originalRequisition.status === 'PENDING'" ng-click="processOrder()"
           class="process-button" style="width: 15%" type="button" value="Process">

    <%--Complete button. Current status must be PENDING.--%>
    <input ng-show="originalRequisition.status === 'PROCESSING'" ng-click="completeOrder()"
           class="complete-button" style="width: 15%" type="button" value="Complete">

    <%--Approve button. Requires current status is COMPLETED and logged in employee has appropriate permissions.--%>
    <shiro:hasPermission name="supply:shipment:approve">
      <input ng-show="originalRequisition.status === 'COMPLETED'" ng-click="approveShipment()"
             class="approve-button" style="width: 15%" type="button" value="Approve">
    </shiro:hasPermission>

    <%--Reject button. Requires a note to be entered. Has a popup confirmation.--%>
    <input ns-popover ns-popover-template="confirmReject" ns-popover-timeout="0.5"
           ng-show="originalRequisition.status === 'PENDING' || originalRequisition.status === 'PROCESSING'"
           class="reject-button" style="width: 15%; float: right;"
           type="button" value="Reject" ng-disabled="!editableRequisition.note">

  </div>
</div>
