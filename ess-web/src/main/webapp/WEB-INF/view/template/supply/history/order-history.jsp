<div ng-controller="SupplyOrderHistoryCtrl">
  <div class="supply-order-hero">
    <h2>Order History</h2>
  </div>

  <div loader-indicator class="loader" ng-show="loading === true"></div>

  <div class="content-container" ng-show="loading === false">
    <h4 class="content-info dark-blue-purple" style="margin-bottom: 0;">Recently ordered requisitions</h4>

    <%--Filters--%>
    <div class="grid">
      <div class="col-4-12 padding-10" style="padding-top: 50px">
        <label class="bold">From:</label>
        <input datepicker ng-model="filter.date.from" ng-change="updateRequisitions()"/>
      </div>
      <div class="col-4-12 padding-10" style="padding-top: 50px">
        <label class="bold">To:</label>
        <input datepicker ng-model="filter.date.to" ng-change="updateRequisitions()"/>
      </div>
      <div class="col-4-12 padding-10">
        <label class="bold" style="display: inline-block; vertical-align: middle;">Status:</label>
        <select style="display: inline-block; vertical-align: middle;"
                ng-model="filter.status"
                ng-change="updateRequisitions()"
                ng-model-options='{ debounce: 1000 }'
                size="5"
                multiple>
          <option ng-repeat="status in STATUSES">{{status}}</option>
        </select>
      </div>
    </div>

    <div class="content-info" ng-show="requisitions.length == 0">
      <h2 class="dark-gray">No Recent History.</h2>
    </div>
  </div>

  <div class="content-container" ng-show="loading === false">
    <div>
      <dir-pagination-controls class="text-align-center" on-page-change="updateRequisitions()" pagination-id="order-history-pagination"
                               boundary-links="true" max-size="10"></dir-pagination-controls>
    </div>

    <div class="padding-10" ng-show="requisitions.length > 0">
      <table class="ess-table supply-listing-table">
        <thead>
        <tr>
          <th>Id</th>
          <th>Ordered By</th>
          <th>Destination</th>
          <th>Order Date</th>
          <th>Status</th>
        </tr>
        </thead>
        <tbody>
        <%--<tr ng-repeat="requisition in allRequisitions" >--%>
        <tr dir-paginate="requisition in requisitions | itemsPerPage : paginate.itemsPerPage"
            current-page="paginate.currPage"
            pagination-id="order-history-pagination"
            total-items="paginate.totalItems"
            ng-click="viewRequisition(requisition)">
          <td>{{requisition.requisitionId}}</td>
          <td>{{requisition.customer.lastName}}</td>
          <td>{{requisition.destination.locId}}</td>
          <td>{{requisition.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
          <td ng-class="{'pending-cell': requisition.activeVersion.status === 'PENDING',
                       'processing-cell': requisition.activeVersion.status === 'PROCESSING',
                       'completed-cell': requisition.activeVersion.status === 'COMPLETED' || requisition.activeVersion.status === 'APPROVED',
                       'rejected-cell': requisition.activeVersion.status === 'REJECTED'}">
            {{requisition.status}}
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div>
      <dir-pagination-controls class="text-align-center" pagination-id="order-history-pagination"
                               boundary-links="true" max-size="10"></dir-pagination-controls>
    </div>

  </div>
</div>