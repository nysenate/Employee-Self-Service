<table class="ess-table supply-listing-table">
  <thead>
  <tr>
    <th>Commodity Code</th>
    <th>Item Name</th>
    <th>Quantity</th>
  </tr>
  </thead>
  <tbody>
  <tr ng-class="{warn: highlightLineItem(lineItem)}" ng-repeat="lineItem in dirtyShipment.order.activeVersion.lineItems">
    <td>{{getItemCommodityCode(lineItem.item.id)}}</td>
    <td>{{getItemName(lineItem.item.id)}}</td>
    <td><input type="number" ng-change="setDirty()" ng-model="lineItem.quantity" style="width:13%"></td>
  </tr>
  </tbody>
</table>