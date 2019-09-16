<div class="content-container">
  <p class="travel-content-info travel-text-bold" ng-bind="::title"></p>

  <ess-travel-inner-container title="Purpose of Travel">
    <%--    <div style="white-space:pre-wrap;">--%>
    <%--      {{reviewApp.purposeOfTravel}}--%>
    <%--    </div>--%>
    <div>
      <div class="purpose-row">
        <label>Your purpose of travel:</label>
        <span ng-bind="::reviewApp.purposeOfTravel.eventType.displayName" style="width: 150px;"></span>
      </div>
      <div ng-if="reviewApp.purposeOfTravel.eventType.requiresName" class="purpose-row">
        <label>Name of the {{reviewApp.purposeOfTravel.eventType.displayName}}:</label>
        <span ng-bind="::reviewApp.purposeOfTravel.eventName" style="width: 350px;"></span>
      </div>
      <div ng-if="reviewApp.purposeOfTravel.additionalPurpose !== ''" class="purpose-row">
        <div ng-if="reviewApp.purposeOfTravel.eventType.requiresAdditionalPurpose">
          <label style="vertical-align: top;">Description:</label>
          <span ng-bind="::reviewApp.purposeOfTravel.additionalPurpose" style="width: 400px; display: inline-block;"></span>
        </div>
        <div ng-if="!reviewApp.purposeOfTravel.eventType.requiresAdditionalPurpose">
          <label style="vertical-align: top;">Additional information:</label>
          <span ng-bind="::reviewApp.purposeOfTravel.additionalPurpose" style="width: 400px; display: inline-block;"></span>
        </div>
      </div>
    </div>
  </ess-travel-inner-container>

  <ess-travel-inner-container title="Attachments" ng-if="reviewApp.attachments.length > 0">
    <div ng-repeat="attachment in reviewApp.attachments" class="travel-attachment-container padding-10">
      <div class="travel-attachment-filename">{{attachment.originalName}}</div>
    </div>
  </ess-travel-inner-container>


  <ess-travel-inner-container title="Segments">

    <form name="outboundForm">
      <fieldset disabled="disabled" style="border: none;">
        <div class="travel-container"
             ng-repeat="leg in reviewApp.route.outboundLegs">

          <div class="outbound width-100" style="display: inline-block;">
            <h3 class="float-left">Outbound Segment {{$index + 1}}</h3>
          </div>

          <div class="itinerary-address">
            <label>From</label><br/>
            <input ess-address-autocomplete
                   ng-model="leg.from.address.formattedAddress"
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
            <input ess-address-autocomplete
                   ng-model="leg.to.address.formattedAddress"
                   callback="leg.setTo(address)"
                   placeholder="200 State St, Albany NY 12210"
                   type="text"
                   size="40">
          </div>

          <div class="itinerary-mot-container">
            <div class="itinerary-mot">
              <label>Mode of Transportation:</label><br/>
              <select ng-model="leg.methodOfTravelDisplayName"
                      ng-options="name for name in methodsOfTravel"></select>
            </div>
            <div class="itinerary-mot-write-in" ng-if="leg.methodOfTravelDisplayName == 'Other'">
              <label>Please Specify:</label><br/>
              <input type="text" size="17" ng-model="leg.methodOfTravelDescription">
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
            <input ess-address-autocomplete
                   ng-model="leg.from.address.formattedAddress"
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
            <input ess-address-autocomplete
                   ng-model="leg.to.address.formattedAddress"
                   callback="leg.setTo(address)"
                   placeholder="200 State St, Albany NY 12210"
                   type="text"
                   size="40">
          </div>

          <div class="itinerary-mot-container">
            <div class="itinerary-mot">
              <label>Mode of Transportation:</label><br/>
              <select ng-model="leg.methodOfTravelDisplayName"
                      ng-options="name for name in methodsOfTravel"></select>
            </div>
            <div class="itinerary-mot-write-in" ng-if="leg.methodOfTravelDisplayName == 'Other'">
              <label>Please Specify:</label><br/>
              <input type="text" size="17" ng-model="leg.methodOfTravelDescription">
            </div>
          </div>
          <div class="clear"></div>

        </div>

      </fieldset>
    </form>
  </ess-travel-inner-container>

  <ess-travel-inner-container title="Expenses">
    <div>
      <div class="grid" style="padding-left: 300px; padding-right: 200px;">
        <div class="col-6-12 margin-bottom-5">
          Meals:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{reviewApp.mealAllowance | currency}}
          <span ng-if="reviewApp.mealAllowance > 0"
                class="icon-info pointer"
                ng-click="displayMealDetails()"
                title="View detailed meal expense info">
          </span>
        </div>
        <div class="col-6-12 margin-bottom-5">
          Lodging:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{reviewApp.lodgingAllowance | currency}}
          <span ng-if="reviewApp.lodgingAllowance > 0"
                class="icon-info pointer"
                ng-click="displayLodgingDetails()"
                title="View detailed lodging expense info">
          </span>
        </div>
        <div class="col-6-12 margin-bottom-5">
          Mileage:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{reviewApp.mileageAllowance | currency}}
          <span ng-if="reviewApp.mileageAllowance > 0"
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
          {{reviewApp.alternateTransportationAllowance | currency}}
        </div>
        <div class="col-6-12 margin-bottom-5">
          Train/Airplane:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{reviewApp.trainAndPlaneAllowance | currency}}
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
  </ess-travel-inner-container>


  <ess-travel-inner-container title="Driving Route">
    <div id="map" class="margin-top-20"
         style="width: 650px; height: 375px; margin-left: auto; margin-right: auto;"></div>
  </ess-travel-inner-container>

  <div class="travel-button-container" style="border: none;">
    <input type="button" class="reject-button"
           ng-show="showNegative"
           ng-value="::negativeLabel || 'Cancel'"
           ng-click="cancel()">
    <input type="button" class="travel-neutral-button" value="Back"
           title="Back"
           ng-click="back()">
    <input type="button" class="submit-button"
           ng-attr-title="{{::positiveBtnLabel}}"
           ng-value="::positiveBtnLabel"
           ng-click="next()">
  </div>

</div>
