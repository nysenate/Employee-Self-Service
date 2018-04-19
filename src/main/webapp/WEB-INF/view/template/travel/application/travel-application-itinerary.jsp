<div>
  <div class="content-container">
    <div>
      <h1 class="content-info">Itinerary</h1>
      <div class="padding-10">
        <p class="text-align-center">Add the route of travel for each step of your trip.</p>

        <form name="outboundForm">
        <div class="travel-container"
             ng-repeat="leg in outgoingLegs">

            <h3>Outgoing Segment {{$index + 1}}</h3>

            <div class="itinerary-address">
              <label>From</label><br/>
              <input travel-address-autocomplete
                     ng-model="leg.from.formattedAddress"
                     ng-if="pageState === STATES.ITINERARY"
                     callback="leg.setFrom(address)"
                     placeholder="200 State St, Albany NY 12210"
                     type="text" size="40">
            </div>
            <div class="itinerary-date">
              <label>Departure Date</label><br/>
              <input datepicker ng-model="departureDate"
                     ng-change="leg.setDepartureDate(departureDate)" size="13">
            </div>
            <div class="clear"></div>

            <div class="itinerary-address">
              <label>To</label><br/>
              <input travel-address-autocomplete
                     ng-model="leg.to.formattedAddress"
                     ng-if="pageState === STATES.ITINERARY"
                     callback="leg.setTo(address)"
                     placeholder="200 State St, Albany NY 12210"
                     type="text"
                     size="40">
            </div>
            <div class="itinerary-date">
              <label>Arrival Date</label><br/>
              <input datepicker ng-model="arrivalDate"
                     from-date="departureDate"
                     ng-change="leg.setArrivalDate(arrivalDate)" size="13">
            </div>
            <div class="clear"></div>

            <div class="itinerary-mot-container">
              <div class="itinerary-mot">
                <label>Mode of Transportation:</label><br/>
                <select ng-model="leg.modeOfTransportation"
                        ng-options="mode for mode in modesOfTransportation"></select>
              </div>
              <div class="itinerary-mot-write-in" ng-if="leg.modeOfTransportation == 'Other'">
                <label>Please Specify:</label><br/>
                <input type="text" size="17">
              </div>
            </div>
            <div class="itinerary-allowance-container">
              <label>This destination will be reimbursed for the following.</label><br/>
              <label>Mileage <input type="checkbox" ng-model="leg.isMileageRequested"></label>
              <label>Meals <input type="checkbox" ng-model="leg.isMealsRequested"></label>
              <label>Lodging <input type="checkbox" ng-model="leg.isLodgingRequested"></label>
            </div>
            <div class="clear"></div>

        </div>
        <input type="submit" value="submit" ng-disabled="outboundForm.$invalid">
        </form>



        <div class="text-align-center">
          <input type="button" class="travel-neutral-button" value="Add Segment"
                 ng-click="addSegment()">
        </div>


      </div>
    </div>

    <div class="text-align-center">
      <div class="travel-button-container">
        <input type="button" class="travel-neutral-button" value="Back"
               ng-click="originCallback(origin, ACTIONS.BACK)">
        <input type="button" class="submit-button"
               value="Next"
               ng-disabled="origin.formattedAddress.length == 0"
               ng-click="originCallback(origin, ACTIONS.NEXT)">
      </div>
    </div>
  </div>
</div>