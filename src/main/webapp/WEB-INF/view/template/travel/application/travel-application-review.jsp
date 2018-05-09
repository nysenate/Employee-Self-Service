
<div class="content-container text-align-center">
    <h1 class="content-info"> Review</h1>
    <div class="padding-10">
      Please review your application below.
    </div>
</div>


<div class="content-container text-align-center">
  <div>
    <h1 class="content-info">Purpose of Travel
    <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.PURPOSE)" class="icon-edit pointer" title="Edit purpose of travel"></span>
    </h1>

    <div class="margin-20" style="white-space:pre-wrap;">
      {{reviewApp.purposeOfTravel}}
    </div>
  </div>
</div>


<div class="content-container">
  <div>
    <h1 class="content-info">Outbound Segments
      <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.OUTBOUND)" class="icon-edit pointer" title="Edit origin"></span>
    </h1>
    <div class="padding-10">

      <form name="outboundForm">
        <fieldset disabled="disabled" style="border: none;">
        <div class="travel-container"
             ng-repeat="leg in app.route.outboundLegs">

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
                      ng-options="mode for mode in modesOfTransportation"></select>
            </div>
            <div class="itinerary-mot-write-in" ng-if="leg.modeOfTransportation == 'Other'">
              <label>Please Specify:</label><br/>
              <input type="text" size="17">
            </div>
          </div>
          <div class="clear"></div>

        </div>


        </fieldset>
      </form>

    </div>
  </div>
</div>


<div class="content-container">
  <div>
    <h1 class="content-info">Return Segments
      <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.RETURN)" class="icon-edit pointer" title="Edit origin"></span>
    </h1>
    <div class="padding-10">

      <form name="returnForm">
        <fieldset disabled="disabled" style="border: none;">

          <div class="travel-container"
               ng-repeat="leg in app.route.returnLegs">

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
                        ng-options="mode for mode in modesOfTransportation"></select>
              </div>
              <div class="itinerary-mot-write-in" ng-if="leg.modeOfTransportation == 'Other'">
                <label>Please Specify:</label><br/>
                <input type="text" size="17">
              </div>
            </div>
            <div class="clear"></div>

          </div>

        </fieldset>
      </form>

    </div>
  </div>
</div>


<div class="content-container text-align-center">
  <h1 class="content-info">Expenses
    <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.ALLOWANCES)" class="icon-edit pointer" title="Edit expenses"></span>
  </h1>
  <div class="padding-10">
    <div class="grid" style="padding-left: 200px; padding-right: 200px;">
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
        {{reviewApp.route.mileageAllowance | currency}}
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
</div>







<div class="content-container text-align-center">
  <div>
    <h1 class="content-info">Route of Travel</h1>

    <%--Google Map--%>
    <div id="map" class="margin-top-20" style="width: 650px; height: 375px; margin-left: auto; margin-right: auto;"></div>

  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Back"
           ng-click="reviewCallback(ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Submit"
           ng-click="submitConfirmModal()">
    <a target="_blank" ng-href="${ctxPath}/travel/application/travel-application-print?id={{reviewApp.id}}" style="float: right">Print</a>
  </div>
</div>
