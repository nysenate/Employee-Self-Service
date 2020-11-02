<div class="content-container">
  <div ng-show="apps.length > 0">
    <div class="padding-10">

      <table class="travel-table travel-hover">
        <thead>
        <tr>
          <th>Travel Date</th>
          <th>Traveler</th>
          <th>Destination</th>
          <th>Allotted Funds</th>
          <th ng-if="options.showStatus">Status</th>
        </tr>
        </thead>

        <tbody>
        <tr dir-paginate="app in apps | orderBy: 'startDate' | itemsPerPage : 10"
            pagination-id="travel-table-pagination">

          <td ng-bind="::app.startDate | date:'M/d/yyyy'" ng-click="onRowClick({app: app})"></td>
          <td ng-bind="::app.traveler.lastName" ng-click="onRowClick({app: app})"></td>
          <td ng-bind="::app.destinationSummary" ng-click="onRowClick({app: app})"></td>
          <td ng-bind="::app.totalAllowance | currency" ng-click="onRowClick({app: app})"></td>
          <td ess-app-status="app" ng-if="options.showStatus" ng-click="onRowClick({app: app})"></td>

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