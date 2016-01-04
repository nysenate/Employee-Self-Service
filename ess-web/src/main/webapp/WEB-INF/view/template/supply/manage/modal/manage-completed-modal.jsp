<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--Title--%>

<div class="padding-10">
  <div>
    <h3 class="content-info">Order from {{order.customer.firstName}} {{order.customer.initial}} {{order.customer.lastName}}</h3>
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
          <tr ng-class="{warn: highlightLineItem(lineItem)}" ng-repeat="lineItem in order.items">
            <td>{{getItemCommodityCode(lineItem.itemId)}}</td>
            <td>{{getItemName(lineItem.itemId)}}</td>
            <td>{{lineItem.quantity}}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <%--Right Margin--%>

    <div class="col-4-12">
      <h4 class="content-info">Location: {{order.location.code + '-' + order.location.locationTypeCode}}</h4>
      <h4 class="content-info">Ordered: {{order.orderDateTime | date:'MM/dd/yy h:mm a'}}</h4>
      <h4 class="content-info">Issued By: {{order.issuingEmployee.lastName}}</h4>
      <h4 class="content-info">Completed: {{order.completedDateTime | date:'MM/dd/yy h:mm a'}}</h4>

      <div class="text-align-center" style="padding-bottom: 25px; padding-top: 10px">
        <a target="_blank" href="${ctxPath}/supply/requisition/view?order={{order.id}}&print=true">
          Print
        </a>
        <a target="#" ng-click="close()" style="padding-left: 30px">
          Exit
        </a>
      </div>

      <div class="text-align-center">
        <input ng-click="undo(order)" class="reject-button" type="button" value="Undo">
      </div>
    </div>
  </div>
</div>