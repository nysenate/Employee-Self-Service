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
        <div class="grid text-align-center" style="border-bottom: 1px black solid">
          <div class="bold-text col-4-12">
            Commodity Code
          </div>
          <div class="bold-text col-4-12">
            Location Code
          </div>
          <div class="bold-text col-4-12">
            Quantity
          </div>
        </div>
        <div class="div-table-body">
          <div class="div-row-container"
               dir-paginate="value in result.array | itemsPerPage: 15"
               pagination-id="item-history-pagination">
            <div class="div-table-row"
                 ng-class="{'supply-highlight-row': showDetails}"
                 ng-click="showDetails = !showDetails">
              <div class="col-4-12">
                {{value.commodityCode}}
              </div>
              <div class="col-4-12">
                {{value.locationCode}}
              </div>
              <div class="col-4-12">
                {{value.quantity}}
              </div>
            </div>
            <div ng-show="showDetails"
                 class="supply-sub-table">
              <table class="ess-table supply-listing-table">
                <thead>
                <tr>
                  <th>Id</th>
                  <th>Ordered By</th>
                  <th>Quantity</th>
                  <th>Completed Date</th>
                  <th>Issued By</th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="req in value.requisitions"
                    ng-click="openReqModal(req)">
                  <td>{{req.requisitionId}}</td>
                  <td>{{req.customer.lastName}}</td>
                  <td>{{getItemQuantity(req, value.commodityCode)}}</td>
                  <td>{{req.completedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
                  <td>{{req.issuer.lastName}}</td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div modal-container>
    <modal modal-id="requisition-modal">
      <div requisition-modal></div>
    </modal>
  </div>


</div>