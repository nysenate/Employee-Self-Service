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
    <div class="content-info" style="padding-bottom: 40px;">

      <%-- Location Selection --%>
      <div ng-show="state === 'LOCATION_SELECTION'">
        <h3 class="content-info">Enter departure and destination info</h3>
        <div>
          <div style="margin-top: 40px;">
            <h4>Departure (From)</h4>
            <div class="margin-10">
              <input travel-address-autocomplete callback="setOrigin(address)" placeholder="200 State St, Albany NY 12210" type="text" size="45">
            </div>
          </div>


          <div class="clearfix width-100" style="margin-top: 40px;">
            <h4>Destination (To)</h4>
            <div ng-repeat="dest in app.itinerary.destinations.items">

              <div>

                <div class="travel-location-div">
                  <div style="float: left; font-size: 0.8em;">
                    Arrival Date: {{dest.arrivalDate | date: 'shortDate'}}<br/>
                    Departure Date: {{dest.departureDate | date: 'shortDate'}}
                  </div>

                  <div>
                    {{dest.address.addr1}}<br/>
                    <span ng-if="dest.address.addr2.length <= 0">{{dest.address.addr2}}<br/></span>
                    {{dest.address.city}} {{dest.address.state}} {{dest.address.zip5}}
                  </div>

                  <div style="float: right; font-size: 0.8em;">
                    Mode of Transportation: <br/>
                    {{dest.modeOfTransportation}}
                  </div>
                </div>

                <div class="travel-location-edit">
                  <div class="icon-edit"
                       title="Edit Destination"
                       ng-click="editDestination(dest)">
                  </div>
                  <div class="icon-trash" style="right: -20px;"
                       title="Delete Destination"
                       ng-click="removeDestination(dest)">
                  </div>
                </div>

              </div>

            </div>
            <div class="clearfix width-100">
              <input type="button" class="submit-button"
                     value="Add Destination"
                     ng-click="addDestinationOnClick()">
            </div>
          </div>
        </div>
      </div>

      <%-- Method and Purpose --%>
      <div ng-show="state === 'METHOD_AND_PURPOSE'">
        <h3 class="content-info">Estimated Travel Expenses</h3>
        <div class="grid padding-10">
          <div class="col-6-12 padding-bottom-10">
            <label class="travel-allowance-label">Tolls:</label>
            <input ng-model="app.allowances.tolls" type="number" step="0.01" min="0">
          </div>
          <div class="col-6-12 padding-bottom-10">
            <label class="travel-allowance-label">Parking:</label>
            <input ng-model="app.allowances.parking" type="number" step="0.01" min="0">
          </div>
          <div class="col-6-12">
            <label class="travel-allowance-label">Taxi/Bus/Subway:</label>
            <input ng-model="app.allowances.alternate" type="number" step="0.01" min="0">
          </div>
          <div class="col-6-12">
            <label class="travel-allowance-label">Registration Fee:</label>
            <input ng-model="app.allowances.registrationFee" type="number" step="0.01" min="0">
          </div>
        </div>

        <h4 class="content-info" style="padding-top: 40px;">Purpose of Travel</h4>
        <textarea ng-model="app.purposeOfTravel" cols="80" rows="6" placeholder="Enter purpose of travel"></textarea>
      </div>

      <%-- Review and Submit --%>
      <div ng-show="state === 'REVIEW_AND_SUBMIT'">
        <h3 class="content-info">Review and Submit</h3>
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
            <div>
              {{dest.address.addr1}}<br/>
              <span ng-if="dest.address.addr2.length <= 0">{{dest.address.addr2}}<br/></span>
              {{dest.address.city}} {{dest.address.state}} {{dest.address.zip5}}
            </div>
            <div class="float-right" style="font-size: 0.8em;">
              Mode of Transportation: <br/>
              {{dest.modeOfTransportation}}
            </div>
          </div>
        </div>

        <div style="margin-top: 40px;">
          <h4>Estimated Allowances</h4>
          <div class="grid">
            <div class="col-3-12">
              <label class="travel-allowance-label">Meals: </label>
              {{app.allowances.gsa.meals | currency}}
            </div>
            <div class="col-3-12">
              <label class="travel-allowance-label">Lodging: </label>
              {{app.allowances.gsa.lodging | currency}}
            </div>
            <div class="col-3-12">
              <label class="travel-allowance-label">Mileage: </label>
              {{app.allowances.mileage | currency}}
            </div>
            <div class="col-3-12">
              <label class="travel-allowance-label">Tolls: </label>
              {{app.allowances.tolls | currency}}
            </div>
            <div class="col-3-12">
              <label class="travel-allowance-label">Parking: </label>
              {{app.allowances.parking | currency}}
            </div>
            <div class="col-3-12">
              <label class="travel-allowance-label">Taxi/Bus/Subway: </label>
              {{app.allowances.alternate | currency}}
            </div>
            <div class="col-3-12">
              <label class="travel-allowance-label">Registration Fee: </label>
              {{app.allowances.registrationFee | currency}}
            </div>
            <div class="col-3-12">
              <label class="travel-allowance-label bold">Total: </label>
              {{app.allowances.total | currency}}
            </div>
          </div>
        </div>

        <div style="margin-top: 40px;"
             ng-if="app.purposeOfTravel.length > 0">
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
      <modal modal-id="calculating-allowances">
        <div progress-modal title="Calculating allowances..."></div>
      </modal>
      <modal modal-id="submit-progress">
        <div progress-modal title="Saving travel application..."></div>
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
          <input type="button" class="neutral-button" value="Back"
                 ng-click="backToLocationSelection()">
        </div>
        <div class="col-6-12">&nbsp;
        </div>

        <div class="col-3-12">
          <input type="button" class="submit-button"
                 value="Next"
                 ng-disabled="!methodAndPurposeCompleted()"
                 ng-click="toReviewAndSubmit()">
        </div>
      </div>

      <%-- Review and Submit buttons --%>
      <div ng-show="state === 'REVIEW_AND_SUBMIT'">
        <div class="col-3-12">
          <input type="button" class="neutral-button" value="Back"
                 ng-click="backToMethodAndPurpose()">
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
