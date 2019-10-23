<div ng-controller="UpcomingTravelController">
  <div class="travel-hero">
    <h2>My Upcoming Travel</h2>
  </div>
  <div class="content-container">
    <div>
      <h4 class="content-info">Submitted Travel Applications</h4>
      <div class="padding-10">
        <table class="travel-table">
          <thead>
          <tr>
            <th>Travel Date</th>
            <th>Employee</th>
            <th>Destination</th>
            <th>Allotted Funds</th>
            <th>Status</th>
          </tr>
          </thead>
          <tbody>
            <tr dir-paginate="app in applications | orderBy: 'travelDate' | itemsPerPage: 10"
            pagination-id="current-applications-pagination">
              <td>{{app.travelDate | date:'M/d/yyyy'}}</td>
              <td>{{app.applicant.lastName}}</td>
              <td>{{app.itinerary.destinations[0].address.city}}</td>
              <td>{{app.totalAllowance | currency}}</td>
              <td>{{app.status}}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div>
        <dir-pagination-controls class="text-align-center" pagination-id="current-applications-pagination"
                                 boundary-links="true" max-size="10"></dir-pagination-controls>
      </div>
    </div>
  </div>

  <div class="content-container">
    <div ng-show="savedApplications.length > 0">
      <h4 class="content-info">Saved Applications</h4>
      <div class="padding-10">
        <table class="travel-table">
          <thead>
          <tr>
            <th>Travel Date</th>
            <th>Employee</th>
            <th>Destination</th>
            <th>Allotted Funds</th>
            <th>Status</th>
          </tr>
          </thead>
          <tbody>
          <tr dir-paginate="app in applications | itemsPerPage: 10"
              pagination-id="saved-applications-pagination">
            <td>{{app.travelDate | date:'M/d/yyyy'}}</td>
            <td>{{app.applicant.lastName}}</td>
            <td>{{app.itinerary.destinations[0].address.city}}</td>
            <td>{{app.totalAllowance | currency}}</td>
            <td>{{app.status}}</td>
          </tr>
          </tbody>
        </table>
      </div>
      <div>
        <dir-pagination-controls class="text-align-center" pagination-id="saved-applications-pagination"
                                 boundary-links="true" max-size="10"></dir-pagination-controls>
      </div>
    </div>
  </div>

  <div class="content-container">
    <input type="button" class="width-100 submit-button" style="font-size: 1.25em; padding: 20px;"
           value="Apply for Travel"
           ng-click="startNewApplication()">
  </div>
</div>
