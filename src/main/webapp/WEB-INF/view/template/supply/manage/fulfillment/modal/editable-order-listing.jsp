<div class="content-container">
  <table class="ess-table supply-listing-table">
    <thead>
    <tr>
      <th>Commodity Code</th>
      <th>Item</th>
      <th>Quantity</th>
    </tr>
    </thead>
    <tbody>
    <tr ng-class="calculateHighlighting(lineItem)"
        ng-repeat="lineItem in editableRequisition.lineItems | orderBy: 'item.commodityCode'">
      <td>{{lineItem.item.commodityCode}}</td>
      <td>{{lineItem.item.description}}</td>
      <form>
        <td><input order-quantity-validator
                   type="text"
                   ng-change="onUpdate()"
                   ng-model="lineItem.quantity"
                   min="0"
                   step="1"
                   maxlength="4"
                   style="width: 50px;"></td>
      </form>
    </tr>
    </tbody>
  </table>
</div>
