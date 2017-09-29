<div ng-controller="TravelHistoryController">
  <div class="travel-hero">
    <h2>Travel Request History</h2>
  </div>
  <div class="form-holder">
    <md-datepicker ng-model="ctrl.myDate" md-placeholder="Enter date"></md-datepicker>
    <form id="dates">
      <label id="location-from">
        From: <input type="date" name="location-from">
        <span class="glyphicon glyphicon-calendar" aria-hidden="true"></span>
      </label>
      <label id="location-to">
        To: <input type="date" name="location-to">
      </label>
    </form>
  </div>
  <div>{{data.success}}</div>
  <div>
    <dir-pagination-controls class="text-align-center" on-page-change="onPageChange()"
                             pagination-id="order-history-pagination"
                             boundary-links="true" max-size="10"></dir-pagination-controls>
  </div>
  <div>
    <table>
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
      <tr dir-paginate="row in travelHistory | itemsPerPage : paginate.itemsPerPage"
          current-page="paginate.currPage"
          pagination-id="order-history-pagination"
          total-items="paginate.totalItems"
          ng-click="viewRequisition(shipment)">
          <td>{{row.travelDate}}</td>
          <td>{{row.employee}}</td>
          <td>{{row.destination}}</td>
          <td>{{row.allottedFunds}}</td>
          <td>{{row.status}}</td>
        </tr>
      </tbody>
    </table>
    <div>
      <dir-pagination-controls class="text-align-center" pagination-id="order-history-pagination"
                               boundary-links="true" max-size="10"></dir-pagination-controls>
    </div>
    {{hi}}
  </div>
</div>
