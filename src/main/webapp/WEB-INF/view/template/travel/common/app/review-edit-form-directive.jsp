<div>
  <div class="travel-card">
    <p class="travel-content-info travel-text-bold">
      Please review your application.
    </p>
    <div class="travel-card-item">
      <h3 class="travel-title">Purpose of Travel</h3>
      <div class="margin-left-20">
        <div class="review-purpose">
          <label>Your purpose of travel:</label>
          <span>{{::reviewAmendment.purposeOfTravel.eventType.displayName}}</span>

          <label ng-if="reviewAmendment.purposeOfTravel.eventType.requiresName">
            Name of the {{::reviewAmendment.purposeOfTravel.eventType.displayName}}:
          </label>
          <span ng-if="reviewAmendment.purposeOfTravel.eventType.requiresName">
          {{::reviewAmendment.purposeOfTravel.eventName}}
        </span>

          <label ng-if="reviewAmendment.purposeOfTravel.additionalPurpose !== ''">
            Additional information:
          </label>
          <span ng-if="!reviewAmendment.purposeOfTravel.eventType.requiresAdditionalPurpose">
          {{::reviewAmendment.purposeOfTravel.additionalPurpose}}
        </span>
        </div>
      </div>
    </div>

    <div class="travel-card-item"
         ng-if="reviewAmendment.attachments.length > 0">
      <h3 class="travel-title">Attachments</h3>
      <div class="margin-left-20">
        <ul style="padding: 0px;">
          <li ng-repeat="attachment in reviewAmendment.attachments">
            <span class="travel-attachment-filename">
              {{attachment.originalName}}
            </span>
          </li>
        </ul>
      </div>
    </div>

    <div class="travel-card-item">
      <h3 class="travel-title">Outbound Segments</h3>
      <div class="margin-left-20">
        <table class="travel-table">
          <thead>
          <tr>
            <td>Travel Date</td>
            <td>From</td>
            <td>To</td>
            <td>Mode of Transportation</td>
          </tr>
          </thead>
          <tbody>
          <tr ng-repeat="leg in reviewAmendment.route.outboundLegs">
            <td>{{leg.travelDate | date: 'shortDate'}}</td>
            <td>{{leg.from.address.formattedAddressWithCounty}}</td>
            <td>{{leg.to.address.formattedAddressWithCounty}}</td>
            <td>{{leg.methodOfTravelDisplayName}}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="travel-card-item">
      <h3 class="travel-title">Return Segments</h3>
      <div class="margin-left-20">
        <table class="travel-table">
          <thead>
          <tr>
            <td>Travel Date</td>
            <td>From</td>
            <td>To</td>
            <td>Mode of Transportation</td>
          </tr>
          </thead>
          <tbody>
          <tr ng-repeat="leg in reviewAmendment.route.returnLegs">
            <td>{{leg.travelDate | date: 'shortDate'}}</td>
            <td>{{leg.from.address.formattedAddressWithCounty}}</td>
            <td>{{leg.to.address.formattedAddressWithCounty}}</td>
            <td>{{leg.methodOfTravelDisplayName}}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="travel-card-item">
      <h3 class="travel-title">Expenses</h3>
      <div class="margin-left-20">
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
      </div>
    </div>

    <div class="travel-card-item">
      <h3 class="travel-title">Driving Route</h3>
      <div id="map" class="margin-top-20"
           style="width: 650px; height: 375px; margin-left: auto; margin-right: auto;"></div>
    </div>

    <div class="travel-button-container">
      <button type="button" class="travel-neutral-btn"
              ng-show="showNegative"
              ng-click="cancel()">
        {{::negativeLabel || 'Cancel'}}
      </button>
      <button type="button" class="travel-neutral-btn"
              ng-click="back()">
        Back
      </button>
      <button type="submit" class="travel-primary-btn"
              ng-click="next()">
        {{::positiveBtnLabel}}
      </button>
    </div>

  </div>
