<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--Title--%>

<div class="padding-10">
  <div>
    <h3 class="content-info">
      <span ng-if="requisition.status === 'REJECTED'">Rejeted</span>
      <span ng-if="requisition.status === 'APPROVED' && requisition.savedInSfms == false">Sync Failed</span>
      Requisition {{requisition.requisitionId}} Requested By {{requisition.customer.firstName}}
      {{requisition.customer.initial}} {{requisition.customer.lastName}}</h3>
  </div>

  <%--Immutable Order content--%>

  <div class="grid grid-padding content-info">
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
          <tr ng-class="{warn: highlightLineItem(lineItem)}" ng-repeat="lineItem in requisition.lineItems">
            <td>{{lineItem.item.commodityCode}}</td>
            <td>{{lineItem.item.description}}</td>
            <td>{{lineItem.quantity}}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <div class="padding-top-10"
           ng-show="requisition.note">
        <div class="col-2-12 bold">
          Note:
        </div>
        <div class="col-10-12">
          {{requisition.note}}
        </div>
      </div>
    </div>

    <%--Right Margin--%>

    <div class="col-4-12 requisition-modal-right-margin">
      <h4 class="content-info">Location</h4>
      <div>{{requisition.destination.locId}}</div>

      <h4 class="content-info">Ordered Date Time</h4>
      <div>{{requisition.orderedDateTime | date:'MM/dd/yy h:mm a'}}</div>

      <h4 class="content-info">Issued By</h4>
      <div>{{requisition.issuer.lastName}}</div>

      <h4 class="content-info" ng-show="requisition.status !== 'CANCELED'">Approved Date Time</h4>
      <div>{{requisition.approvedDateTime | date:'MM/dd/yy h:mm a'}}</div>

      <h4>Actions</h4>
      <div>
        <a target="_blank"
           href="${ctxPath}/supply/requisition/requisition-view?requisition={{requisition.requisitionId}}&print=true">
          Print Requisition
        </a>
      </div>
      <div>
        <a target="#" ng-click="close()">Exit</a>
      </div>

      <div class="text-align-center" ng-show="requisition.status === 'CANCELED'">
        <input ng-click="acceptShipment(requisition)" class="submit-button" type="button" value="Accept">
      </div>

    </div>
  </div>
</div>