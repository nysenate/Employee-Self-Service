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
        </tr>
        </thead>
        <tbody>
        <tr dir-paginate="app in apps | orderBy: '-travelDate' : true | itemsPerPage : 10"
            pagination-id="travel-table-pagination"
            ng-click="onRowClick({app: app})">
          <td>{{app.startDate | date:'M/d/yyyy'}}</td>
          <td>{{app.traveler.lastName}}</td>
          <td>{{getDestinations(app)}}</td>
          <td>{{app.totalAllowance | currency}}</td>
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