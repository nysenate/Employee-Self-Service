<div ng-controller="TravelHistoryController">
  <div class="travel-hero">
    <h2>Travel Request History</h2>
  </div>
  <div class="content-container">
    <div>
      <h2 class="content-info">Travel Request History</h2>
      <div class="text-align-center">
        <div class="padding-10 inline-block">
          <label class="bold">From:</label>
          <input datepicker readonly='true' style="margin-left: 1px;"
                 ng-model="date.from" to-date="date.to"
                 ng-change="updateRequisitions()"/>
        </div>
        <div class="padding-10 inline-block">
          <label class="bold">To:</label>
          <input datepicker readonly='true' style="margin-left: 1px;"
                 ng-model="date.to" from-date="date.from"
                 ng-change="updateRequisitions()"/>
        </div>
      </div>

      <div class="padding-10">
        <table class="travel-table">
          <tbody>
          <tr>
          <thead>
          <td>Travel Date</td>
          <td>Employee</td>
          <td>Destination</td>
          <td>Allotted Funds</td>
          <td>Status</td>
          </thead>
          </tr>
          <tr dir-paginate="row in travelHistory | orderBy: '-travelDate' : true | itemsPerPage : 5"
              pagination-id="travel-history-pagination">
            <td>{{row.travelDate | date:'M/d/yyyy'}}</td>
            <td>{{row.empName}}</td>
            <td>{{row.destination}}</td>
            <td>{{row.allottedFunds}}</td>
            <td>{{row.status}}</td>
          </tr>
          </tbody>
        </table>
        <div>
          <dir-pagination-controls class="text-align-center" pagination-id="travel-history-pagination"
                                   boundary-links="true" max-size="10"></dir-pagination-controls>
        </div>
      </div>

    </div>
  </div>
</div>
