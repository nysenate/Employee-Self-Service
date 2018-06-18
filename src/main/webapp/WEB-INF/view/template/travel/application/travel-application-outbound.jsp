<div class="content-container">
  <p class="travel-content-info travel-text">
    Enter your outbound route of travel.
  </p>

  <form name="outboundForm">
    <div class="travel-inner-container" ng-repeat="leg in route.outboundLegs">
      <div class="travel-background" style="display: inline-block; width: 100%;">
        <h2 class="travel-subheader float-left">Outbound Segment {{$index + 1}}</h2>
        <span class="icon-cross travel-container-cross float-right" style=""
              ng-if="$index > 0 && isLastSegment($index)"
              ng-click="deleteSegment()"></span>
      </div>
      <div class="travel-inner-container-content">

        <div class="itinerary-address">
          <label>From</label><br/>
          <input travel-address-autocomplete
                 ng-model="leg.from.formattedAddress"
                 leg="leg"
                 callback="setFromAddress(leg, address)"
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
                 leg="leg"
                 callback="setToAddress(leg, address)"
                 placeholder="To Address"
                 type="text"
                 size="40">
        </div>
        <%--<div class="clear"></div>--%>

        <div class="itinerary-mot-container">
          <div class="itinerary-mot">
            <label>Mode of Transportation:</label><br/>
            <select ng-model="leg.modeOfTransportation"
                    ng-options="mode.displayName for mode in modesOfTransportation track by mode.methodOfTravel"></select>
          </div>
          <div class="itinerary-mot-write-in" ng-if="leg.modeOfTransportation.methodOfTravel == 'OTHER'">
            <label>Please Specify:</label><br/>
            <input type="text" size="17" ng-model="leg.modeOfTransportation.description">
          </div>
        </div>
        <div class="clear"></div>

      </div>
    </div>
  </form>

  <div class="text-align-center">
    <input type="button" class="travel-neutral-button" value="Add Outbound Segment"
           ng-click="addSegment()">
  </div>


  <div class="text-align-center">
    <div class="travel-button-container">
      <input type="button" class="neutral-button" value="Cancel"
             ng-click="outboundCallback(ACTIONS.CANCEL)">
      <input type="button" class="travel-neutral-button" value="Back"
             ng-click="outboundCallback(ACTIONS.BACK)">
      <input type="button" class="submit-button"
             value="Next"
             ng-disabled="origin.formattedAddress.length == 0"
             ng-click="outboundCallback(ACTIONS.NEXT, route)">
    </div>
  </div>
</div>
