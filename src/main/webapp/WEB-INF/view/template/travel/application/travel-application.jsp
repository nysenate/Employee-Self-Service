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
      <div ng-if="state === 'LOCATION_SELECTION'">
        <h4 class="content-info">Enter origin and destination(s)</h4>
        <div class="grid text-align-center">
          <div class="col-6-12">
            <h4>Departure (From)</h4>
            <div class="padding-10">
              <input travel-autocomplete callback="setOrigin(address)" placeholder="Enter Origin Address" type="text" size="30">
            </div>
          </div>
          <div class="col-6-12">
            <h4>Destination (To)</h4>
            <div class="clearfix width-100" style="padding: 5px; margin: 10px; height: 50px; text-align: center; line-height: 20px; border: solid 1px black;"
                 ng-repeat="dest in app.itinerary.destinations">
              <div style="float: left; font-size: 0.8em;">
                Arrival Date: {{dest.arrivalDate}}<br/>
                Departure Date: {{dest.departureDate}}
              </div>
              <div style="float: right;">
                {{dest.address.addr1}}<br/>
                <span ng-if="dest.address.addr2.length <= 0">{{dest.address.addr2}}<br/></span>
                {{dest.address.city}} {{dest.address.state}} {{dest.address.zip5}}
              </div>
            </div>
            <div class="padding-10">
              <input type="button" class="submit-button"
                     value="Add Destination"
                     ng-click="addDestinationOnClick()">
            </div>
          </div>
        </div>

        <div modal-container>
          <modal modal-id="destination-selection-modal">
            <div destination-selection-modal></div>
          </modal>
        </div>




      </div>
    </div>


    <%-- Navigation Buttons --%>
    <div class="grid text-align-center padding-10">
      <div ng-if="state = 'LOCATION_SELECTION'">
        <div class="col-3-12">
          <input type="button" class="neutral-button"
                 value="Cancel">
        </div>
        <div class="col-6-12">&nbsp;
        </div>

        <div class="col-3-12">
          <input type="button" class="submit-button"
                 value="Next"
                 ng-disabled="!areLocationsEntered()">
        </div>
      </div>

    </div>
  </div>
</div>
