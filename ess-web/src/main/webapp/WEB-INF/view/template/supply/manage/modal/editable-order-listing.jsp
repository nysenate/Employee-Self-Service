<table class="ess-table supply-listing-table">
  <thead>
  <tr>
    <th>Commodity Code</th>
    <th>Item</th>
    <th>Quantity</th>
  </tr>
  </thead>
  <tbody>
  <tr ng-class="{warn: highlightLineItem(lineItem)}"
      ng-repeat="lineItem in displayOrderVersion.lineItems | orderBy: 'item.description'">
    <td>{{getItemCommodityCode(lineItem.item.id)}}</td>
    <td>{{lineItem.item.description}}</td>
    <td><input type="number" ng-change="onUpdate()" ng-model="lineItem.quantity" min="0" max="100" step="1" style="width: 40px;"></td>
  </tr>
  </tbody>
</table>
