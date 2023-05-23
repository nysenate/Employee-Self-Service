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
        <tr dir-paginate="app in apps | orderBy: '-travelStartDate' | itemsPerPage : 10"
            pagination-id="travel-table-pagination">

          <td ng-bind="::app.activeAmendment.startDate | date:'M/d/yyyy'" ng-click="onRowClick({app: app})"></td>
          <td ng-bind="::app.traveler.fullName" ng-click="onRowClick({app: app})"></td>
          <td ng-bind="::app.activeAmendment.destinationSummary" ng-click="onRowClick({app: app})"></td>
          <td ng-bind="::app.activeAmendment.totalAllowance | currency" ng-click="onRowClick({app: app})"></td>
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