<div class="text-align-center padding-10">
  <div class="travel-gray-bottom-border clearfix padding-10 width-100">
    <p class="col-6-12 bold">{{requestInfo.traveler.fullName}}</p>
    <p class="col-6-12"><span class="bold">Status:</span> {{requestInfo.status}}</p>
  </div>
  <div class="travel-gray-bottom-border clearfix padding-10 width-100">
    <table class="travel-table col-6-12">
      <tbody>
        <tr>
          <td>Meals</td>
          <td>{{ '$' + requestInfo.allowances.gsa.meals}}</td>
        </tr>
        <tr>
          <td>Lodging</td>
          <td>{{ '$' + requestInfo.allowances.gsa.lodging}}</td>
        </tr>
        <tr>
          <td>Registration Fee</td>
          <td>{{ '$' + requestInfo.allowances.registrationFee}}</td>
        </tr>
        <!-- People might be confused if they try to add up to the overall total
        <tr>
          <td>GSA Total</td>
          <td>{{ '$' + requestInfo.allowances.gsa.total}}</td>
        </tr> -->
      </tbody>
    </table>
    <table class="travel-table col-6-12">
      <tbody>
        <tr>
          <td>Mileage</td>
          <td>{{ '$' + requestInfo.allowances.mileage}}</td>
        </tr>
        <tr>
          <td>Tolls</td>
          <td>{{ '$' + requestInfo.allowances.tolls}}</td>
        </tr>
        <tr>
          <td>Parking</td>
          <td>{{ '$' + requestInfo.allowances.parking}}</td>
        </tr>
        <tr>
          <td>Alternate</td>
          <td>{{ '$' + requestInfo.allowances.alternate}}</td>
        </tr>
      </tbody>
    </table>
    <p class="width-100 clearfix margin-top-10 padding-top-10 text-align-center"><span class="bold">Total Allowance:</span> {{'$' + requestInfo.totalAllowance}}</p>
  </div>
  <div class="padding-10 clearfix width-100">
    <div class="clearfix width-100 margin-bottom-20">
      <p class="col-6-12">
        <span class="bold">Origin:</span> {{requestInfo.itinerary.origin.addr1 + ', ' + requestInfo.itinerary.origin.city +
        ', ' + requestInfo.itinerary.origin.state + ' ' + requestInfo.itinerary.origin.zip5}}
      </p>
      <p class="col-6-12"><span class="bold">Mode of transportation:</span> {{requestInfo.modeOfTransportation}}</p>
    </div>
    <table class="travel-table margin-top-10 padding-top-10">
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
    <p class="padding-10"><span class="bold">Purpose of travel:</span> {{requestInfo.purposeOfTravel}}</p>
  </div>
</div>