<div class="content-container">
  <p class="travel-content-info travel-text-bold" ng-bind="::title"></p>

  <ess-travel-inner-container title="Purpose of Travel">
    <%--    <div style="white-space:pre-wrap;">--%>
    <%--      {{reviewAmendment.purposeOfTravel}}--%>
    <%--    </div>--%>
    <div>
      <div class="purpose-row">
        <label>Your purpose of travel:</label>
        <span ng-bind="::reviewAmendment.purposeOfTravel.eventType.displayName" style="width: 150px;"></span>
      </div>
      <div ng-if="reviewAmendment.purposeOfTravel.eventType.requiresName" class="purpose-row">
        <label>Name of the {{reviewAmendment.purposeOfTravel.eventType.displayName}}:</label>
        <span ng-bind="::reviewAmendment.purposeOfTravel.eventName" style="width: 350px;"></span>
      </div>
      <div ng-if="reviewAmendment.purposeOfTravel.additionalPurpose !== ''" class="purpose-row">
        <div ng-if="reviewAmendment.purposeOfTravel.eventType.requiresAdditionalPurpose">
          <label style="vertical-align: top;">Description:</label>
          <span ng-bind="::reviewAmendment.purposeOfTravel.additionalPurpose"
                style="width: 400px; display: inline-block;"></span>
        </div>
        <div ng-if="!reviewAmendment.purposeOfTravel.eventType.requiresAdditionalPurpose">
          <label style="vertical-align: top;">Additional information:</label>
          <span ng-bind="::reviewAmendment.purposeOfTravel.additionalPurpose"
                style="width: 400px; display: inline-block;"></span>
        </div>
      </div>
    </div>
  </ess-travel-inner-container>

  <ess-travel-inner-container title="Attachments" ng-if="reviewAmendment.attachments.length > 0">
    <ul ng-repeat="attachment in reviewAmendment.attachments" class="travel-attachment-container">
      <li class="travel-attachment-filename">{{attachment.originalName}}</li>
    </ul>
  </ess-travel-inner-container>


  <ess-travel-inner-container title="Segments">

    <form name="outboundForm">
      <fieldset disabled="disabled" style="border: none;">
        <div class="travel-container"
             ng-repeat="leg in reviewAmendment.route.outboundLegs">

          <div class="outbound width-100" style="display: inline-block;">
            <h3 class="float-left">Outbound Segment {{$index + 1}}</h3>
          </div>

          <div class="itinerary-address">
            <label>From</label><br/>
            <input ess-address-autocomplete
                   ng-model="leg.from.address.formattedAddressWithCounty"
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
                   ng-model="leg.to.address.formattedAddressWithCounty"
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
             ng-repeat="leg in reviewAmendment.route.returnLegs">

          <div class="return width-100" style="display: inline-block;">
            <h3 class="float-left">Return Segment {{$index + 1}}</h3>
          </div>

          <div class="itinerary-address">
            <label>From</label><br/>
            <input ess-address-autocomplete
                   ng-model="leg.from.address.formattedAddressWithCounty"
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
                   ng-model="leg.to.address.formattedAddressWithCounty"
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
    <div class="app-review-allowances-box">
      <table>
        <tbody>
        <tr>
          <td class="label">
            Meals:
          </td>
          <td class="price">
            {{reviewAmendment.mealAllowance | currency}}
          </td>
          <td>
            <ess-meal-summary-popover amd="reviewAmendment"/>
          </td>
        </tr>
        <tr>
          <td class="label">
            Lodging:
          </td>
          <td class="price">
            {{reviewAmendment.lodgingAllowance | currency}}
          </td>
          <td>
            <ess-lodging-summary-popover amd="reviewAmendment"/>
          </td>
        </tr>
        <tr>
          <td class="label">
            Mileage:
          </td>
          <td class="price">
            {{reviewAmendment.mileageAllowance | currency}}
          </td>
          <td>
            <ess-transportation-summary-popover amd="reviewAmendment"/>
          </td>
        </tr>
        <tr>
          <td class="label">
            Tolls:
          </td>
          <td class="price">
            {{reviewAmendment.tollsAllowance | currency}}
          </td>
        </tr>
        <tr>
          <td class="label">
            Parking:
          </td>
          <td class="price">
            {{reviewAmendment.parkingAllowance | currency}}
          </td>
        </tr>
        <tr>
          <td class="label">
            Taxi/Bus/Subway:
          </td>
          <td class="price">
            {{reviewAmendment.alternateTransportationAllowance | currency}}
          </td>
        </tr>
        <tr>
          <td class="label">
            Train/Airplane:
          </td>
          <td class="price">
            {{reviewAmendment.trainAndPlaneAllowance | currency}}
          </td>
        </tr>
        <tr>
          <td class="label">
            Registration Fee:
          </td>
          <td class="price">
            {{reviewAmendment.registrationAllowance | currency}}
          </td>
        </tr>
        <tr>
          <td class="label">
            <span class="bold">Total:</span>
          </td>
          <td class="price">
            {{reviewAmendment.totalAllowance | currency}}
          </td>
        </tr>
        </tbody>
      </table>
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
