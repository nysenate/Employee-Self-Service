<div class="content-container">
  <p class="travel-content-info travel-text-bold">
    Please review your application.
  </p>

  <travel-inner-container title="Purpose of Travel">
    <div style="white-space:pre-wrap;">
      {{reviewApp.purposeOfTravel}}
    </div>
  </travel-inner-container>

  <travel-inner-container title="Attachments" ng-if="reviewApp.attachments.length > 0">
    <div ng-repeat="attachment in reviewApp.attachments" class="travel-attachment-container padding-10">
      <div class="travel-attachment-filename">{{attachment.originalName}}</div>
    </div>
  </travel-inner-container>


  <travel-inner-container title="Segments">

    <form name="outboundForm">
      <fieldset disabled="disabled" style="border: none;">
        <div class="travel-container"
             ng-repeat="leg in reviewApp.route.outboundLegs">

          <div class="outbound width-100" style="display: inline-block;">
            <h3 class="float-left">Outbound Segment {{$index + 1}}</h3>
          </div>

          <div class="itinerary-address">
            <label>From</label><br/>
            <input travel-address-autocomplete
                   ng-model="leg.from.formattedAddress"
                   callback="leg.setFrom(address)"
                   placeholder="200 State St, Albany NY 12210"
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
                   placeholder="200 State St, Albany NY 12210"
                   type="text"
                   size="40">
          </div>

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
      </fieldset>
    </form>


    <form name="returnForm">
      <fieldset disabled="disabled" style="border: none;">

        <div class="travel-container"
             ng-repeat="leg in reviewApp.route.returnLegs">

          <div class="return width-100" style="display: inline-block;">
            <h3 class="float-left">Return Segment {{$index + 1}}</h3>
          </div>

          <div class="itinerary-address">
            <label>From</label><br/>
            <input travel-address-autocomplete
                   ng-model="leg.from.formattedAddress"
                   callback="leg.setFrom(address)"
                   placeholder="200 State St, Albany NY 12210"
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
                   placeholder="200 State St, Albany NY 12210"
                   type="text"
                   size="40">
          </div>

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

      </fieldset>
    </form>
  </travel-inner-container>

  <travel-inner-container title="Expenses">
    <div>
      <div class="grid" style="padding-left: 300px; padding-right: 200px;">
        <div class="col-6-12 margin-bottom-5">
          Meals:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{reviewApp.mealAllowance.totalMealAllowance | currency}}
          <span ng-if="reviewApp.mealAllowance.totalMealAllowance > 0"
                class="icon-info pointer"
                ng-click="displayMealDetails()"
                title="View detailed meal expense info">
          </span>
        </div>
        <div class="col-6-12 margin-bottom-5">
          Lodging:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{reviewApp.lodgingAllowance.totalLodgingAllowance | currency}}
          <span ng-if="reviewApp.lodgingAllowance.totalLodgingAllowance > 0"
                class="icon-info pointer"
                ng-click="displayLodgingDetails()"
                title="View detailed lodging expense info">
          </span>
        </div>
        <div class="col-6-12 margin-bottom-5">
          Mileage:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{reviewApp.mileageAllowance.totalMileageAllowance | currency}}
          <span ng-if="reviewApp.mileageAllowance.totalMileageAllowance > 0"
                class="icon-info pointer"
                ng-click="displayMileageDetails()"
                title="View detailed mileage expense info">
          </span>
        </div>
        <div class="col-6-12 margin-bottom-5">
          Tolls:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{reviewApp.tollsAllowance | currency}}
        </div>
        <div class="col-6-12 margin-bottom-5">
          Parking:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{reviewApp.parkingAllowance | currency}}
        </div>
        <div class="col-6-12 margin-bottom-5">
          Taxi/Bus/Subway:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{reviewApp.alternateAllowance | currency}}
        </div>
        <div class="col-6-12 margin-bottom-5">
          Registration Fee:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{reviewApp.registrationAllowance | currency}}
        </div>
        <div class="col-6-12 margin-bottom-5">
          <span class="bold">Total:</span>
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{reviewApp.totalAllowance | currency}}
        </div>
      </div>
    </div>
  </travel-inner-container>


  <travel-inner-container title="Driving Route">
    <div id="map" class="margin-top-20"
         style="width: 650px; height: 375px; margin-left: auto; margin-right: auto;"></div>
  </travel-inner-container>

  <div class="travel-button-container" style="border: none;">
    <input type="button" class="neutral-button" value="Cancel"
           ng-click="cancel()">
    <input type="button" class="travel-neutral-button" value="Back"
           ng-click="previousState()">
    <input type="button" class="submit-button"
           value="Submit"
           ng-click="next()">
  </div>
</div>
