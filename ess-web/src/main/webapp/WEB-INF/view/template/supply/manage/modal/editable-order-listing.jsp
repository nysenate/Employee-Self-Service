<table class="ess-table supply-listing-table">
  <thead>
  <tr>
    <th>Commodity Code</th>
    <th>Item</th>
    <th>Quantity</th>
  </tr>
  </thead>
  <tbody>
  <tr ng-class="{warn: highlightLineItem(lineItem)}" ng-repeat="lineItem in dirtyShipment.order.activeVersion.lineItems">
    <td>{{getItemCommodityCode(lineItem.item.id)}}</td>
    <td>{{lineItem.item.description}}</td>
    <td><input type="number" ng-change="setDirty()" ng-model="lineItem.quantity" style="width:30%"></td>
  </tr>
  </tbody>
</table>