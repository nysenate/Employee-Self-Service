<div class="padding-10">
  <h3 class="content-info">Travel Detail Information</h3>
  <div class="content-info">
    <div class="padding-10">
      <!--
      <div>
        <p class="col-6-12">Name: {{requestInfo.applicant.fullName}}</p>
        <p class="col-6-12">Status: {{requestInfo.status}}</p>
      </div>
      -->
      <div>
        Origin: {{requestInfo.itinerary.origin.addr1 + ', ' + requestInfo.itinerary.origin.city +
        ', ' + requestInfo.itinerary.origin.state + ' ' + requestInfo.itinerary.origin.zip5}}
      </div>
      <caption>Destinations</caption>
      <table class="travel-table">
        <thead>
          <tr>
            <td>Arrival Date</td>
            <td>Departure Date</td>
            <td>Address</td>
          </tr>
        </thead>
        <tbody>
          <tr ng-repeat="destination in requestInfo.itinerary.destinations">
            <td>{{destination.arrivalDate | date:'M/d/yyyy'}}</td>
            <td>{{destination.departureDate | date:'M/d/yyyy'}}</td>
            <td>{{destination.address.addr1 + ', ' + destination.address.city + ', '
              + destination.address.state + ' ' + destination.address.zip5}}</td>
          </tr>
        </tbody>
      </table>
      <div>
        <p class="inline-block col-4-12">Meal Allowance:{{ '$' + requestInfo.gsaAllowance.meals}}</p>
        <p class="inline-block col-4-12">Lodging Allowance:{{ '$' + requestInfo.gsaAllowance.lodging}}</p>
        <p class="inline-block col-4-12">Travel Allowance:{{ '$' + requestInfo.transportationAllowance.total}}</p>
      </div>
    </div>
  </div>
</div>