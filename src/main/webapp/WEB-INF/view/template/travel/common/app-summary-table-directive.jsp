<div class="content-container">
  <div ng-show="apps.length > 0">
    <div class="padding-10">

      <table class="travel-table travel-hover">
        <thead>
        <tr>
          <th>Travel Date</th>
          <th>Employee</th>
          <th>Destination</th>
          <th>Allotted Funds</th>
          <th ng-if="options.showStatus">Status</th>
        </tr>
        </thead>

        <tbody>
        <tr dir-paginate="app in apps | orderBy: '-startDate' : true | itemsPerPage : 10"
            pagination-id="travel-table-pagination"
            ng-click="onRowClick({app: app})">

          <td ng-bind="::app.startDate | date:'M/d/yyyy'"></td>
          <td ng-bind="::app.traveler.lastName"></td>
          <td ng-bind="::destinationSummary(app)"></td>
          <td ng-bind="::app.totalAllowance | currency"></td>
          <td ng-if="options.showStatus" ng-class="statusClass(app)" ng-bind="::statusDescription(app)"></td>

        </tr>
        </tbody>
      </table>

      <div>
        <dir-pagination-controls class="text-align-center" pagination-id="travel-table-pagination"
                                 boundary-links="true" max-size="10"></dir-pagination-controls>
      </div>

    </div>
  </div>
</div>