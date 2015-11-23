<div ng-controller="SupplyReconciliationController">
  <div class="supply-order-hero">
    <h2>Reconciliation</h2>
  </div>

  <div class="content-container">
    <div class="padding-10">
      <table class="ess-table supply-listing-table">
        <thead>
        <tr>
          <th>Commodity Code</th>
          <th>Item Name</th>
          <th>Quantity On Hand</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="item in reconcilableItems">
          <td>{{item.commodityCode}}</td>
          <td>{{item.name}}</td>
          <td>&nbsp;</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>