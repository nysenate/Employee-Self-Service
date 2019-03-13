<div class="content-container">

  <form novalidate name="allowancesForm" id="allowancesForm"
        ng-submit="allowancesForm.$valid && next()">

    <p class="travel-content-info travel-text-bold">
      Enter your estimated expenses for the following categories.
    </p>

    <div ng-if="allowancesForm.$submitted && !allowancesForm.$valid">
      <ess-notification level="error" message="">
        <ul>
          <li ng-show="allowancesForm.$error.step">Expenses must be in increments of 0.01</li>
          <li ng-show="allowancesForm.$error.min">Expenses cannot be negative</li>
          <li ng-show="allowancesForm.$error.number">Expenses must be a number</li>
        </ul>
      </ess-notification>
    </div>

    <travel-inner-container title="Miscellaneous Expenses">
      <div class="text-align-center" style="width: 70%; margin: auto;">
        <div class="grid" style="min-width: 0;">
          <div class="col-6-12 padding-bottom-10" title="Meal Expenses">
            <label class="travel-allowance-label">Meals: $</label>
            <input ng-model="dirtyApp.allowances.meals" type="number" step="0.01" min="0"
                   style="width: 5em;">
            <span ng-if="dirtyApp.allowances.meals"
                  class="icon-info pointer"
                  ng-click="displayMealDetails()"
                  title="View calculated meal expense info"
                  style="position: fixed; padding-top: 6px; padding-left: 2px;">
          </span>
          </div>
          <div class="col-6-12 padding-bottom-10">
            <label class="travel-allowance-label">Lodging: $</label>
            <input ng-model="dirtyApp.allowances.lodging" type="number" step="0.01" min="0"
                   style="width: 5em;">
            <span ng-if="dirtyApp.allowances.lodging"
                  class="icon-info pointer"
                  ng-click="displayLodgingDetails()"
                  title="View calculated lodging expense info"
                  style="position: fixed; padding-top: 6px; padding-left: 2px;">
            </span>
          </div>
          <div class="col-6-12 padding-bottom-10">
            <label class="travel-allowance-label">Mileage: $</label>
            <input ng-model="dirtyApp.allowances.mileage" type="number" step="0.01" min="0"
                   style="width: 5em;"/>
            <span ng-if="dirtyApp.allowances.mileage"
                  class="icon-info pointer"
                  ng-click="displayMileageDetails()"
                  title="View calculated mileage expense info"
                  style="position: fixed; padding-top: 6px; padding-left: 2px;">
          </span>
          </div>
          <div class="col-6-12 padding-bottom-10">
            <label class="travel-allowance-label">Tolls: $</label>
            <input ng-model="dirtyApp.allowances.tolls" type="number" step="0.01" min="0"
                   style="width: 5em;">
          </div>
          <div class="col-6-12 padding-bottom-10">
            <label class="travel-allowance-label">Parking: $</label>
            <input ng-model="dirtyApp.allowances.parking" type="number" step="0.01" min="0"
                   style="width: 5em;">
          </div>
          <div class="col-6-12 padding-bottom-10">
            <label class="travel-allowance-label">Taxi/Bus/Subway: $</label>
            <input ng-model="dirtyApp.allowances.alternateTransportation" type="number" step="0.01"
                   min="0" style="width: 5em;">
          </div>
          <div class="col-6-12 padding-bottom-10">
            <label class="travel-allowance-label">Train/Airplane: $</label>
            <input ng-model="dirtyApp.allowances.trainAndPlane" type="number" step="0.01" min="0"
                   style="width: 5em;">
          </div>
          <div class="col-6-12">
            <label class="travel-allowance-label">Registration Fee: $</label>
            <input ng-model="dirtyApp.allowances.registration" type="number" step="0.01" min="0"
                   style="width: 5em;">
          </div>
        </div>
        <p class="travel-text-bold">
          Note: Meals, lodging, and mileage expenses were calculated automatically.
        </p>
      </div>
    </travel-inner-container>

    <div class="travel-button-container">
      <input type="button" class="neutral-button" value="Cancel"
             ng-click="cancel()">
      <input type="button" class="travel-neutral-button" value="Back"
             ng-click="previousState()">
      <input type="submit" class="submit-button" value="Next">
    </div>
  </form>
</div>