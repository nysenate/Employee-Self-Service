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
  </tr>
  </tbody>
</table>