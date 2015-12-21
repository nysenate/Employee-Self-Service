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
      <h3 class="content-info">Location: {{order.location}}</h3>
      <h3 class="content-info">Ordered: {{order.orderDateTime | date:'MM/dd/yy h:mm a'}}</h3>
    </div>
  </div>
</div>