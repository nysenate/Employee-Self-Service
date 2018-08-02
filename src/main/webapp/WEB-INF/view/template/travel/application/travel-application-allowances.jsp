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

    <travel-inner-container title="Miscellaneous Expenses (Optional)">
      <div class="text-align-center" style="width: 70%; margin: auto;">
        <div class="grid" style="min-width: 0;">
          <div class="col-6-12 padding-bottom-10">
            <label class="travel-allowance-label">Tolls: $</label>
            <input ng-model="allowances.tollsAllowance" type="number" step="0.01" min="0" style="width: 5em;">
          </div>
          <div class="col-6-12 padding-bottom-10">
            <label class="travel-allowance-label">Parking: $</label>
            <input ng-model="allowances.parkingAllowance" type="number" step="0.01" min="0" style="width: 5em;">
          </div>
          <div class="col-6-12 padding-bottom-10">
            <label class="travel-allowance-label">Taxi/Bus/Subway: $</label>
            <input ng-model="allowances.alternateAllowance" type="number" step="0.01" min="0" style="width: 5em;">
          </div>
          <div class="col-6-12 padding-bottom-10">
            <label class="travel-allowance-label">Registration Fee: $</label>
            <input ng-model="allowances.registrationAllowance" type="number" step="0.01" min="0" style="width: 5em;">
          </div>
          <div class="col-6-12" ng-if="false">
            <label class="travel-allowance-label">Train/Airplane: $</label>
            <input ng-model="allowances.trailAirplaneStub" type="number" step="0.01" min="0" style="width: 5em;">
          </div>
        </div>
        <p class="travel-text-bold">
          Note: Meals, lodging, and mileage expenses will be calculated automatically.
        </p>
      </div>
    </travel-inner-container>

    <travel-inner-container title="Meals Adjustment (Optional)">
      <p class="travel-text">
        You qualify for the following meal reimbursements. Uncheck anything you would <span class="bold">not</span> like
        to be reimbursed for.
      </p>
      <div class="grid">
        <div ng-repeat="allowance in dirtyApp.mealAllowance.mealAllowances">
          <div class="expenses-opt-out-list">
            <div class="col-6-12">
              {{allowance.address.formattedAddress}}
            </div>
            <div class="col-3-12">
              {{allowance.date | date: 'shortDate'}}
            </div>
            <div class="col-3-12">
              <label>Meals: </label><input type="checkbox" ng-model="allowance.isMealsRequested">
            </div>
          </div>
        </div>
      </div>
    </travel-inner-container>

    <travel-inner-container title="Lodging Adjustment (Optional)" ng-show="tripHasLodging()">
      <p class="travel-text">
        You qualify for the following lodging reimbursements. Uncheck anything you would <span class="bold">not</span>
        like to be reimbursed for.
      </p>
      <div class="grid">
        <div ng-repeat="allowance in dirtyApp.lodgingAllowance.lodgingAllowances">
          <div class="expenses-opt-out-list">
            <div class="col-6-12">
              {{allowance.address.formattedAddress}}
            </div>
            <div class="col-3-12">
              {{previousDay(allowance.date) | date: 'shortDate'}} - {{allowance.date | date: 'shortDate'}}
            </div>
            <div class="col-3-12">
              <label>Lodging: </label><input type="checkbox" ng-model="allowance.isLodgingRequested">
            </div>
          </div>
        </div>
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