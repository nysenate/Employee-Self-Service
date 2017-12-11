<div class="content-container text-align-center padding-bottom-10">
  <div>
    <h4 class="content-info">Destination (To)</h4>
    <p>
      Add all destinations of your trip.
    </p>
    <div ng-repeat="dest in destinations">
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

  <div class="margin-top-20">
    <input type="button" class="neutral-button" value="Back"
           ng-click="destinationCallback(destinations, ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Next"
           ng-click="destinationCallback(destinations, ACTIONS.NEXT)">
  </div>
</div>