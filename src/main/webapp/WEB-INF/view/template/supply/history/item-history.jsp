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
      <div class="col-6-12 padding-10">
        <label class="supply-text">Location:</label>
        <select ng-model="filters.location.selected"
                ng-options="loc for loc in filters.location.values"
                ng-required="true"
                ng-change="onFilterChange()">
        </select>
      </div>
      <div class="col-6-12" style="padding: 0 10px 10px 10px;">
        <label class="supply-text">From:</label>
        <input datepicker style="margin-left: 1px;"
               readonly='true'
               ng-model="filters.date.from"
               to-date="filters.date.to"
               ng-change="onFilterChange()"/>
      </div>
      <div class="col-6-12" style="padding: 0 10px 10px 10px;">
        <label class="supply-text">To:</label>
        <input datepicker style="margin-left: 2px;"
               readonly='true'
               ng-model="filters.date.to"
               from-date="filters.date.from"
               ng-change="onFilterChange()"/>
      </div>
    </div>
  </div>

  <div loader-indicator class="loader" ng-show="loading === true"></div>

  <div class="content-container large-print-font-size"
       ng-show="loading === false">
    <div class="content-info" ng-show="result.array.length === 0 && loading === false">
      <h2 class="dark-gray">No results were found.</h2>
    </div>

    <div ng-show="result.array.length > 0 && loading === false">
      <div>
        <dir-pagination-controls class="text-align-center"
                                 pagination-id="item-history-pagination"
                                 boundary-links="true"
                                 max-size="10">
        </dir-pagination-controls>
      </div>

      <div class="padding-10">
        <table class="ess-table supply-listing-table">
          <thead>
          <tr>
            <th>Commodity Code</th>
            <th>Location Code</th>
            <th>Quantity</th>
          </tr>
          </thead>
          <tbody>
          <tr dir-paginate="value in result.array | itemsPerPage: 15"
              pagination-id="item-history-pagination">
            <td>{{value.commodityCode}}</td>
            <td>{{value.locationCode}}</td>
            <td>{{value.quantity}}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>


</div>