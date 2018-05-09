<div>
  <div class="content-container">
    <div>
      <h1 class="content-info">Return Trip</h1>
      <div class="padding-10">
        <p class="text-align-center">Add the route of travel of your return trip.</p>

        <form name="returnForm">
          <div class="travel-container"
               ng-repeat="leg in route.returnLegs">

            <div class="return width-100" style="display: inline-block;">
              <h3 class="float-left">Return Segment {{$index + 1}}</h3>
              <span class="icon-cross travel-container-cross"
                    ng-if="$index > 0 && isLastSegment($index)"
                    ng-click="deleteSegment()"></span>
            </div>

            <div class="itinerary-address">
              <label>From</label><br/>
              <input travel-address-autocomplete
                     ng-model="leg.from.formattedAddress"
                     callback="leg.setFrom(address)"
                     placeholder="From Address"
                     type="text" size="40">
            </div>
            <div class="itinerary-date">
              <label>Travel Date</label><br/>
              <input datepicker ng-model="leg.travelDate" size="13">
            </div>
            <div class="clear"></div>

            <div class="itinerary-address">
              <label>To</label><br/>
              <input travel-address-autocomplete
                     ng-model="leg.to.formattedAddress"
                     callback="leg.setTo(address)"
                     placeholder="To Address"
                     type="text"
                     size="40">
            </div>

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
            <div class="clear"></div>

          </div>
          <%--<input type="submit" value="submit" ng-disabled="returnForm.$invalid">--%>
        </form>


        <div class="text-align-center">
          <input type="button" class="time-neutral-button" value="Add Segment"
                 ng-click="addSegment()">
        </div>


      </div>
    </div>

    <div class="text-align-center">
      <div class="travel-button-container">
        <input type="button" class="travel-neutral-button" value="Back"
               ng-click="returnCallback(route, ACTIONS.BACK)">
        <input type="button" class="submit-button"
               value="Next"
               ng-disabled="origin.formattedAddress.length == 0"
               ng-click="returnCallback(route, ACTIONS.NEXT)">
      </div>
    </div>
  </div>
</div>