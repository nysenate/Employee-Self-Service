<div ng-controller="NewTravelApplicationCtrl">
  <div class="travel-hero">
    <h2>Travel Application</h2>
  </div>
  <div class="content-container content-controls">
    <div class="padding-10 text-align-center">
      Travel application for: {{app.applicant.firstName}} {{app.applicant.lastName}}
    </div>
  </div>

  <div class="content-container">
    <div class="content-info">

      <%-- Location Selection --%>
      <div ng-show="state === 'LOCATION_SELECTION'">
        <h4 class="content-info">Enter origin and destination(s)</h4>
        <div class="grid text-align-center">
          <div class="col-6-12">
            <h4>Departure (From)</h4>
            <div class="margin-10">
              <input travel-autocomplete callback="setOrigin(address)" placeholder="Enter Origin Address" type="text" size="30">
            </div>
          </div>
          <div class="col-6-12">
            <h4>Destination (To)</h4>
            <div ng-repeat="dest in app.itinerary.destinations.items">
              <div class="travel-location-div width-90 float-left">
                <div style="float: left; font-size: 0.8em;">
                  Arrival Date: {{dest.arrivalDate | date: 'shortDate'}}<br/>
                  Departure Date: {{dest.departureDate | date: 'shortDate'}}
                </div>
                <div style="float: right;">
                  {{dest.address.addr1}}<br/>
                  <span ng-if="dest.address.addr2.length <= 0">{{dest.address.addr2}}<br/></span>
                  {{dest.address.city}} {{dest.address.state}} {{dest.address.zip5}}
                </div>
              </div>
              <div class="icon-trash float-right" style="padding-top: 30px; cursor: pointer;"
                   ng-click="removeDestination(dest)">
              </div>
            </div>
            <div class="">
              <input type="button" class="submit-button"
                     value="Add Destination"
                     ng-click="addDestinationOnClick()">
            </div>
          </div>
        </div>
      </div>

      <%-- Method and Purpose --%>
      <div ng-show="state === 'METHOD_AND_PURPOSE'">
        <h4 class="content-info">Select Mode of Transportation</h4>
        <div>
          <select ng-model="app.modeOfTransportation"
                  ng-options="mode for mode in MODES_OF_TRANSPORTATION"></select>
        </div>

        <h4 class="content-info" style="padding-top: 40px;">Estimated Travel Expenses</h4>
        <div class="grid padding-10">
          <div class="col-6-12 padding-bottom-5">
            <label class="travel-allowance-label">Tolls:</label>
            <input ng-model="app.transportationAllowance.tolls" type="number" step="0.01" min="0">
          </div>
          <div class="col-6-12 padding-bottom-5">
            <label class="travel-allowance-label">Parking:</label>
            <input ng-model="app.parkingAllowance" type="number" step="0.01" min="0">
          </div>
          <div class="col-6-12">
            <label class="travel-allowance-label">Taxi/Bus/Subway:</label>
            <input ng-model="app.alternateTravelAllowance" type="number" step="0.01" min="0">
          </div>
          <div class="col-6-12">
            <label class="travel-allowance-label">Registration Fee:</label>
            <input ng-model="app.registrationFeeAllowance" type="number" step="0.01" min="0">
          </div>
        </div>

        <h4 class="content-info" style="padding-top: 40px;">Purpose of Travel</h4>
        <textarea ng-model="app.purposeOfTravel" cols="80" rows="6" placeholder="Enter purpose of travel"></textarea>
      </div>

      <%-- Review and Submit --%>
      <div ng-show="state === 'REVIEW_AND_SUBMIT'">
        <h2 class="content-info">Review and Submit</h2>
        <div>
          <h4>Departure (from)</h4>
          <div class="travel-location-div width-50">
            <div>
              {{app.itinerary.origin.addr1}}<br/>
              <span ng-if="app.itinerary.origin.addr2.length <= 0">{{app.itinerary.origin.addr2}}<br/></span>
              {{app.itinerary.origin.city}} {{app.itinerary.origin.state}} {{app.itinerary.origin.zip5}}
            </div>
          </div>
        </div>
        <div>
          <h4>Destinations (to)</h4>
          <div ng-repeat="dest in app.itinerary.destinations.items"
               class="travel-location-div width-50">
            <div style="float: left; font-size: 0.8em;">
              Arrival Date: {{dest.arrivalDate | date: 'shortDate'}}<br/>
              Departure Date: {{dest.departureDate | date: 'shortDate'}}
            </div>
            <div style="float: right;">
              {{dest.address.addr1}}<br/>
              <span ng-if="dest.address.addr2.length <= 0">{{dest.address.addr2}}<br/></span>
              {{dest.address.city}} {{dest.address.state}} {{dest.address.zip5}}
            </div>
          </div>
        </div>
        <div style="margin-top: 40px;">
          <h4>Travel and Allowances</h4>
          <div>
            <label class="travel-allowance-label">Mode of Transportation: </label>
            {{app.modeOfTransportation}}
            <label class="travel-allowance-label">Meals: </label>
            {{app.gsaAllowance.meals | currency}}
            <label class="travel-allowance-label">Lodging: </label>
            {{app.gsaAllowance.lodging | currency}}
            <label class="travel-allowance-label">Mileage: </label>
            {{app.transportationAllowance.mileage | currency}}
            <label class="travel-allowance-label">Tolls: </label>
            {{app.transportationAllowance.tolls | currency}}
             <label class="travel-allowance-label">Parking: </label>
            {{app.parkingAllowance | currency}}
             <label class="travel-allowance-label">Taxi/Bus/Subway: </label>
            {{app.alternateTravelAllowance | currency}}
             <label class="travel-allowance-label">Registration Fee: </label>
            {{app.registrationFeeAllowance | currency}}
          </div>
        </div>
        <div style="margin-top: 40px;">
          <h4>Purpose</h4>
          {{app.purposeOfTravel}}
        </div>

        <%-- END --%>
      </div>
    </div>

    <div modal-container>
      <modal modal-id="destination-selection-modal">
        <div destination-selection-modal></div>
      </modal>
    </div>

    <%-- Navigation Buttons --%>
    <div class="grid text-align-center padding-10">

      <%-- Location selection buttons --%>
      <div ng-show="state === 'LOCATION_SELECTION'">
        <div class="col-3-12">
          <input type="button" class="neutral-button"
                 value="Cancel">
        </div>
        <div class="col-6-12">&nbsp;
        </div>

        <div class="col-3-12">
          <input type="button" class="submit-button"
                 value="Next"
                 <%--ng-disabled="!locationsCompleted()"--%>
                 ng-click="toMethodAndPurpose()">
        </div>
      </div>

      <%-- Method and Purpose buttons --%>
      <div ng-show="state === 'METHOD_AND_PURPOSE'">
        <div class="col-3-12">
          <input type="button" class="neutral-button"
                 value="Back">
        </div>
        <div class="col-6-12">&nbsp;
        </div>

        <div class="col-3-12">
          <input type="button" class="submit-button"
                 value="Next"
                 <%--ng-disabled="!methodAndPurposeCompleted()"--%>
                 ng-click="toReviewAndSubmit()">
        </div>
      </div>

        <%-- Review and Submit buttons --%>
        <div ng-show="state === 'REVIEW_AND_SUBMIT'">
          <div class="col-3-12">
            <input type="button" class="neutral-button"
                   value="Back">
          </div>
          <div class="col-6-12">&nbsp;
          </div>

          <div class="col-3-12">
            <input type="button" class="submit-button"
                   value="Submit"
                   ng-click="submitApplication()">
          </div>
        </div>

    </div>
  </div>
</div>
