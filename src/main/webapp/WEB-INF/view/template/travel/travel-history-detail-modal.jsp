<div class="padding-10">
  <h3 class="content-info" style="margin-bottom: 0px;">Travel Detail Information</h3>
    <div class="padding-10 text-align-center">
      <div>
        <p class="col-6-12">{{requestInfo.applicant.fullName}}</p>
        <p class="col-6-12">Status: {{requestInfo.status}}</p>
      </div>
      <div>
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
            <tr style="border-bottom: 1px solid darkgrey;">
              <td>Incidentals</td>
              <td>{{ '$' + requestInfo.allowances.gsa.incidental}}</td>
            </tr>
            <tr>
              <td>GSA Total</td>
              <td>{{ '$' + requestInfo.allowances.gsa.total}}</td>
            </tr>
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
            <tr style="border-bottom: 1px solid darkgrey;">
              <td>Registration Fee</td>
              <td>{{ '$' + requestInfo.allowances.registrationFee}}</td>
            </tr>
            <tr>
              <td>Total Allowance</td>
              <td>{{ '$' + requestInfo.totalAllowance}}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div>
        <p>
          Origin: {{requestInfo.itinerary.origin.addr1 + ', ' + requestInfo.itinerary.origin.city +
          ', ' + requestInfo.itinerary.origin.state + ' ' + requestInfo.itinerary.origin.zip5}}
        </p>
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
      </div>
      <p>{{requestInfo.purposeOfTravel}}</p>
    </div>
  </div>
</div>