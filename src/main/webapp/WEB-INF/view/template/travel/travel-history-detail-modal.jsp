<div class="content-container no-top-margin padding-left-10 padding-right-10">
  <h1>Travel Application for: {{app.traveler.fullName}}</h1>
  <div class="grid margin-top-10">
    <div class="col-2-12">
      <label>Purpose of Travel:</label>
    </div>
    <div class="col-10-12">
      {{app.purposeOfTravel}}
    </div>
  </div>

  <div class="grid margin-top-10">
    <div class="col-2-12">
      <label>Origin:</label>
    </div>
    <div class="col-10-12">
      {{app.route.origin.formattedAddress}}
    </div>
  </div>

  <div class="grid margin-top-10">
    <div class="col-2-12">
      <label>Mode of Transportation:</label>
    </div>
    <div class="col-10-12">
      <span ng-repeat="mot in getModesOfTransportation()">
        {{mot.description}}<span ng-if="!$last">, </span>
      </span>
    </div>
  </div>

  <div class="grid margin-top-10">
    <div class="col-2-12">
      <label>Destination:</label>
    </div>
    <div class="col-10-12">
      <table class="travel-table">
        <thead>
        <tr>
          <th>Arrival Date</th>
          <th>Departure Date</th>
          <th>Address</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="accommodation in app.accommodations">
          <td>{{accommodation.arrivalDate}}</td>
          <td>{{accommodation.departureDate}}</td>
          <td>{{accommodation.address.formattedAddressWithCounty}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>


  <div class="grid margin-top-10">
    <div class="col-2-12">
      <label>Mileage:</label>
    </div>
    <div class="col-10-12">
      {{app.mileageAllowance | currency}}
    </div>
  </div>

  <div class="grid margin-top-10">
    <div class="col-2-12">
      <label>Tolls:</label>
    </div>
    <div class="col-10-12">
      {{app.tollsAllowance | currency}}
    </div>
  </div>

  <div class="grid margin-top-10">
    <div class="col-2-12">
      <label>Food:</label>
    </div>
    <div class="col-10-12">
      {{app.mealAllowance | currency}}
    </div>
  </div>

  <div class="grid margin-top-10">
    <div class="col-2-12">
      <label>Lodging:</label>
    </div>
    <div class="col-10-12">
      {{app.lodgingAllowance | currency}}
    </div>
  </div>

  <div class="grid margin-top-10">
    <div class="col-2-12">
      <label>Parking/Tolls:</label>
    </div>
    <div class="col-10-12">
      {{app.parkingAllowance | currency}}
    </div>
  </div>

  <div class="grid margin-top-10">
    <div class="col-2-12">
      <label>Taxi/Bus/Subway:</label>
    </div>
    <div class="col-10-12">
      {{app.alternateAllowance | currency}}
    </div>
  </div>

  <div class="grid margin-top-10">
    <div class="col-2-12">
      <label>Registration Fee:</label>
    </div>
    <div class="col-10-12">
      {{app.registrationAllowance | currency}}
    </div>
  </div>

  <div class="grid margin-top-10 bold">
    <div class="col-2-12">
      <label class="bold">Total:</label>
    </div>
    <div class="col-10-12">
      {{app.totalAllowance | currency}}
    </div>
  </div>

  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Exit"
           ng-click="exit()">
    <a target="_blank" ng-href="${ctxPath}/travel/application/travel-application-print?id={{app.id}}">Print</a>
  </div>
</div>