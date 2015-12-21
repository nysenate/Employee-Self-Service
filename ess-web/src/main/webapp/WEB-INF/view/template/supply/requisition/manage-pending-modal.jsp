<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--Title--%>

<div class="padding-10">
  <div>
    <h3 class="content-info">Order submitted by {{order.customer.lastName}}</h3>
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
          <tr ng-class="{warn: highlightLineItem(lineItem)}" ng-repeat="lineItem in dirtyOrder.items">
            <td>{{getItemCommodityCode(lineItem.itemId)}}</td>
            <td>{{getItemName(lineItem.itemId)}}</td>
            <td><input type="number" ng-change="setDirty()" ng-model="lineItem.quantity" style="width:13%"></td>
            <%--<td contenteditable="true">{{lineItem.quantity}}</td>--%>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <%--Right Margin--%>

    <div class="col-4-12">
      <h3 class="content-info">Location: {{order.location.code + '-' + order.location.locationTypeCode}}</h3>
      <h3 class="content-info">Ordered: {{order.orderDateTime | date:'MM/dd/yy h:mm a'}}</h3>
      <div class="text-align-center" style="padding-bottom: 15px">
        <a target="_blank" href="${ctxPath}/supply/requisition/view?order={{order.id}}&print=true">
          Print
        </a>
        <a target="#" ng-click="close()" style="padding-left: 30px">
          Exit
        </a>
      </div>
      <input ng-click="processOrder(order)" class="submit-button col-4-12" type="button" value="Process">
      <input ng-click="saveOrder(order)" class="submit-button col-4-12" type="button" value="Save" ng-disabled="!dirty">
      <input ng-click="rejectOrder(order)" class="reject-button col-4-12" type="button" value="Reject">
    </div>
  </div>
</div>