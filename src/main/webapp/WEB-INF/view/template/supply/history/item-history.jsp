<div ng-controller="SupplyItemHistoryCtrl">
  <div class="supply-order-hero">
    <h2>Item History</h2>
  </div>

  <div class="content-container content-controls">
     <h4 class="content-info supply-text" style="margin-bottom: 0px;">
      Search item order counts.
     </h4>

    <div class="grid text-align-center">
      <div class="col-6-12 padding-10">
        <label class="supply-text">Item:</label>
        <select ng-model="filters.item.selected"
                ng-options="item for item in filters.item.values"
                ng-required="true"
                ng-change="onFilterChange()">
        </select>
      </div>
    </div>

  </div>


</div>