<div>
  <div ng-if="return.form.$submitted && !return.form.$valid" class="margin-10">
    <ess-notification level="error" title="Return segments have errors">
      <ul>
        <li ng-if="return.form.$error.dateValidator">One or more travel dates are invalid.
          <span class="icon-help-with-circle" style="padding: 5px;"
                title="Select a date from the calendar or enter a date in the form mm/dd/yyyy"></span></li>
        </li>
        <li ng-if="return.form.$error.motRequired">Enter a mode of transportation for each segment.</li>
        <li ng-if="return.form.$error.motDescription">Specify how you will travel for segments with mode of
          transportation of Other.
        </li>
        <li ng-if="return.form.$error.addressValidator">One or more of your addresses are invalid.
          <span class="icon-help-with-circle" style="padding: 5px;"
                title="Addresses must be selected from the drop down and contain a zip code. Include a street number to help ensure your selected address will be valid."></span>
        </li>
      </ul>
    </ess-notification>
  </div>

  <div class="travel-card">
    <form novalidate name="return.form" id="returnForm">
      <div class="travel-card-item">
        <h1 class="">Return Segments</h1>
        <div class="padding-10">
          <span style="margin: 5px 10px 5px 10px;">Enter your return route from the last destination to the origin.</span>

          <div class="padding-10" ng-repeat="leg in route.returnLegs">
            <div>
              <h2 class="travel-title-small inline-block">Segment {{$index + 1}}</h2>
              <span class="icon-circle-with-cross travel-cross"
                    ng-if="$index > 0 && isLastSegment($index)"
                    ng-click="deleteSegment()"></span>
            </div>

            <div class="padding-10">
              <div class="itinerary-address">
                <label>From</label><br/>
                <input ess-address-autocomplete
                       class="travel-input"
                       name="fromAddress_{{$index}}"
                       ng-model="leg.from.address.formattedAddressWithCounty"
                       leg="leg"
                       callback="setFromAddress(leg, address)"
                       autocomplete-address-validator
                       placeholder="From Address"
                       type="text" size="50" required>
              </div>
              <div class="itinerary-date">
                <label>Travel Date</label><br/>
                <input datepicker date-validator
                       class="travel-input"
                       placeholder="MM/DD/YYYY"
                       type="text"
                       from-date="fromDate()"
                       name="travelDate_{{$index}}"
                       ng-model="leg.travelDate"
                       autocomplete="off" required>
              </div>
              <div class="clear"></div>

              <div class="itinerary-address">
                <label>To</label><br/>
                <input ess-address-autocomplete
                       class="travel-input"
                       name="toAddress_{{$index}}"
                       ng-model="leg.to.address.formattedAddressWithCounty"
                       leg="leg"
                       callback="setToAddress(leg, address)"
                       autocomplete-address-validator
                       placeholder="To Address"
                       type="text"
                       size="50"
                       required>
              </div>

              <div class="itinerary-mot-container">
                <div class="itinerary-mot">
                  <label>Mode of Transportation:</label><br/>
                  <select mot-validator name="mot_{{$index}}"
                          class="travel-input"
                          ng-model="leg.methodOfTravelDisplayName"
                          ng-options="name for name in methodsOfTravel"
                          ng-change="motChange(leg, $index, 'returnMotOtherInput_')"
                          required></select>
                </div>
                <div class="itinerary-mot-write-in" ng-if="leg.methodOfTravelDisplayName === 'Other'">
                  <label>Please Specify:</label><br/>
                  <input mot-description-validator
                         class="travel-input"
                         id="returnMotOtherInput_{{$index}}"
                         name="motOther_{{$index}}"
                         type="text"
                         ng-model="leg.methodOfTravelDescription">
                </div>
              </div>
              <div class="itinerary-address" ng-if="$index === route.returnLegs.length - 1">
                <input type="checkbox" id="arrival-time-checkbox" ng-model="route.lastLegQualifiesForDinner">
                <label for="arrival-time-checkbox">Arriving after 7pm?</label>
              </div>
              <div class="clear"></div>

            </div>
          </div>
        </div>

        <div class="text-align-center">
          <button class="travel-primary-btn"
                  type="button"
                  ng-click="addSegment()">
            <span class="icon-circle-with-plus" style="font-size: large; vertical-align: middle;"></span>
            <span style="vertical-align: middle;"> Add Return Segment</span>
          </button>
        </div>
      </div>

      <div class="travel-button-container">
        <button type="button"
                class="travel-primary-btn"
                ng-click="back()">
          Back
        </button>
        <button class="travel-neutral-btn"
                ng-if="mode === 'EDIT' || mode === 'RESUBMIT'"
                type="button"
                ng-value="::negativeLabel || 'Cancel'"
                ng-click="cancel()">
          {{::negativeLabel || 'Cancel'}}
        </button>
        <button class="travel-teal-btn"
                ng-if="mode === 'NEW'"
                type="button"
                ng-click="save()">
          Save
        </button>
        <button type="submit" class="travel-submit-btn"
                ng-click="next()">
          Next
        </button>
      </div>
    </form>
  </div>
</div>
