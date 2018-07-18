<div class="content-container">
  <p class="travel-content-info travel-text">
    Enter your return route of travel.
  </p>

  <form novalidate name="returnForm" id="returnForm">

    <div ng-if="returnForm.$submitted && !returnForm.$valid" class="margin-10">
      <ess-notification level="error" title="Return segments have errors" message="Fix the highlighted fields below.">
      </ess-notification>
    </div>

    <div class="travel-inner-container" ng-repeat="leg in route.returnLegs">
      <div class="travel-background" style="display: inline-block; width: 100%;">
        <h2 class="travel-subheader float-left">Return Segment {{$index + 1}}</h2>
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
                 address-validator
                 placeholder="From Address"
                 type="text" size="50" required>
        </div>
        <div class="itinerary-date">
          <label>Travel Date</label><br/>
          <input datepicker date-validator type="text" size="13" from-date="fromDate()"
                 name="travelDate_{{$index}}" ng-model="leg.travelDate" required>
        </div>
        <div class="clear"></div>

        <div class="itinerary-address">
          <label>To</label><br/>
          <input travel-address-autocomplete
                 name="toAddress_{{$index}}"
                 ng-model="leg.to.formattedAddress"
                 leg="leg"
                 callback="setToAddress(leg, address)"
                 address-validator
                 placeholder="To Address"
                 type="text"
                 size="50"
                 required>
        </div>

        <div class="itinerary-mot-container">
          <div class="itinerary-mot">
            <label>Mode of Transportation:</label><br/>
            <select mot-validator name="mot_{{$index}}" ng-model="leg.modeOfTransportation"
                    ng-options="mode.displayName for mode in modesOfTransportation track by mode.methodOfTravel"
                    required></select>
          </div>
          <div class="itinerary-mot-write-in" ng-if="leg.modeOfTransportation.methodOfTravel == 'OTHER'">
            <label>Please Specify:</label><br/>
            <input name="motOther_{{$index}}"
                   ng-required="leg.modeOfTransportation.methodOfTravel === 'OTHER'"
                   type="text" size="17" ng-model="leg.modeOfTransportation.description">
          </div>
        </div>
        <div class="clear"></div>

      </div>
    </div>
    <%--<input type="submit" value="submit" ng-disabled="returnForm.$invalid">--%>


    <div class="text-align-center">
      <input type="button" class="travel-neutral-button" value="Add Return Segment"
             ng-click="addSegment()">
    </div>


    <div class="text-align-center">
      <div class="travel-button-container">
        <input type="button" class="neutral-button" value="Cancel"
               ng-click="returnCallback(ACTIONS.CANCEL)">
        <input type="button" class="travel-neutral-button" value="Back"
               ng-click="returnCallback(ACTIONS.BACK)">
        <input type="submit" class="submit-button" value="Next"
               ng-click="submit()">
      </div>
    </div>

  </form>
</div>
