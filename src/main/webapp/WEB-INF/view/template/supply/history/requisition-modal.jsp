<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--Title--%>

<div class="padding-10">
  <div>
    <h3 class="content-info">
      Requisition {{requisition.requisitionId}} requested by {{requisition.customer.firstName}}
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
          <tr ng-repeat="lineItem in requisition.lineItems">
            <td>{{lineItem.item.commodityCode}}</td>
            <td>{{lineItem.item.description}}</td>
            <td>{{lineItem.quantity}}</td>
          </trgg>
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
      <h4 class="content-info" style="margin-bottom: 5px;">Status</h4>
      <div>{{requisition.status}}</div>

      <h4 class="content-info" style="margin-bottom: 5px;">Location</h4>
      <div>{{requisition.destination.locId}}</div>

      <h4 class="content-info" style="margin-bottom: 5px;">Ordered Date Time</h4>
      <div>{{requisition.orderedDateTime | date:'MM/dd/yy h:mm a'}}</div>

      <div ng-show="requisition.issuer !== null">
      <h4 class="content-info" style="margin-bottom: 5px;">Issued By</h4>
      <div>{{requisition.issuer.lastName}}</div>
      </div>

      <div ng-show="requisition.status === 'REJECTED'">
      <h4 class="content-info" style="margin-bottom: 5px;">Rejected By</h4>
      <div>{{requisition.modifiedBy.lastName}}</div>
      </div>

      <div ng-show="requisition.status === 'APPROVED'">
      <h4 class="content-info" style="margin-bottom: 5px;">Completed Date Time</h4>
      <div>{{requisition.completedDateTime | date:'MM/dd/yy h:mm a'}}</div>
      </div>

      <div ng-show="requisition.status === 'REJECTED'">
      <h4 class="content-info" style="margin-bottom: 5px;">Rejected Date Time</h4>
      <div>{{requisition.rejectedDateTime | date:'MM/dd/yy h:mm a'}}</div>
      </div>

      <h4 style="margin-bottom: 5px;">Actions</h4>
      <div>
        <a target="_blank"
           href="${ctxPath}/supply/requisition/requisition-view?requisition={{requisition.requisitionId}}">
         View full history
        </a>
      </div>

      <div class="text-align-center" ng-show="requisition.status === 'CANCELED'">
        <input ng-click="acceptShipment(requisition)" class="submit-button" type="button" value="Accept">
      </div>

    </div>
  </div>

  <div class="padding-top-10" style="text-align: center">
    <input ng-click="closeModal()" class="neutral-button" style="width: 15%" type="button" value="Exit">
  </div>
</div>
