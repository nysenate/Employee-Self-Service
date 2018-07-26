<div class="content-container">
  <p class="travel-content-info travel-text">
    Enter your outbound route of travel.
  </p>

  <form novalidate name="outbound.form" id="outboundForm">

    <div ng-if="outbound.form.$submitted && !outbound.form.$valid" class="margin-10">
      <ess-notification level="error" title="Outbound segments have errors" message="Fix the highlighted fields below.">
      </ess-notification>
    </div>

    <div class="travel-inner-container" ng-repeat="leg in dirtyApp.route.outboundLegs">
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
                 name="fromAddress_{{$index}}"
                 ng-model="leg.from.formattedAddress"
                 leg="leg"
                 callback="setFromAddress(leg, address)"
                 autocomplete-address-validator
                 placeholder="From Address"
                 type="text" size="50" required>
        </div>
        <div class="itinerary-date">
          <label>Travel Date</label><br/>
          <input datepicker date-validator name="travelDate_{{$index}}" ng-model="leg.travelDate" size="13" type="text" required>
        </div>
        <div class="clear"></div>

        <div class="itinerary-address">
          <label>To</label><br/>
          <input travel-address-autocomplete
                 name="toAddress_{{$index}}"
                 ng-model="leg.to.formattedAddress"
                 leg="leg"
                 callback="setToAddress(leg, address)"
                 placeholder="To Address"
                 autocomplete-address-validator
                 type="text"
                 size="50" required>
        </div>
        <%--<div class="clear"></div>--%>

        <div class="itinerary-mot-container">
          <div class="itinerary-mot">
            <label>Mode of Transportation:</label><br/>
            <select mot-validator name="mot_{{$index}}" ng-model="leg.modeOfTransportation"
                    ng-options="mode.displayName for mode in modesOfTransportation track by mode.methodOfTravel"
                    ng-change="motChange(leg, $index)"
                    required></select>
          </div>
          <div class="itinerary-mot-write-in" ng-if="leg.modeOfTransportation.methodOfTravel == 'OTHER'">
            <label>Please Specify:</label><br/>
            <input id="outboundMotOtherInput_{{$index}}" name="motOther_{{$index}}"
                   ng-required="leg.modeOfTransportation.methodOfTravel === 'OTHER'"
                   type="text" size="17" ng-model="leg.modeOfTransportation.description">
          </div>
        </div>
        <div class="clear"></div>

      </div>
    </div>

    <div>
      <div class="margin-10 travel-highlight-text pointer" style="margin-left:20px;"
           ng-click="addSegment()">
        <span class="icon-circle-with-plus" style="font-size: large; vertical-align: middle;"></span>
        <span style="vertical-align: middle;"> Add Outbound Segment (optional)</span>
      </div>
    </div>


    <div class="text-align-center">
      <div class="travel-button-container">
        <input type="button" class="neutral-button" value="Cancel"
               ng-click="cancel()">
        <input type="button" class="travel-neutral-button" value="Back"
               ng-click="previousState()">
        <input type="submit" class="submit-button" value="Next"
               ng-click="next()">
      </div>
    </div>
  </form>
</div>
