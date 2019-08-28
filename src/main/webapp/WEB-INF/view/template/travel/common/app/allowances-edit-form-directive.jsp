<div class="content-container">

  <form novalidate name="allowancesForm" id="allowancesForm"
        ng-submit="allowancesForm.$valid && next()">

    <p class="travel-content-info travel-text-bold" ng-bind="::title"></p>

    <div ng-if="allowancesForm.$submitted && !allowancesForm.$valid">
      <ess-notification level="error" message="">
        <ul>
          <li ng-show="allowancesForm.$error.step">Expenses must be in increments of 0.01</li>
          <li ng-show="allowancesForm.$error.min">Expenses cannot be negative</li>
          <li ng-show="allowancesForm.$error.number">Expenses must be a number</li>
        </ul>
      </ess-notification>
    </div>

    <ess-travel-inner-container title="Miscellaneous Expenses">
      <div class="text-align-center" style="width: 70%; margin: auto;">
        <div class="grid" style="min-width: 0;">
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
    </ess-travel-inner-container>

    <ess-travel-inner-container title="Meals Adjustment (Optional)">
      <p class="travel-text margin-bottom-20">
        You qualify for the following meal reimbursements. Uncheck anything you would <span class="bold">not</span> like
        to be reimbursed for.
      </p>
      <div>
      <table class="travel-table">
        <thead>
        <tr>
          <td>Address</td>
          <td>Date</td>
          <td>Request Reimbursement?</td>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="perDiem in dirtyApp.route.mealPerDiems.allMealPerDiems">
          <td>{{perDiem.address.formattedAddress}}</td>
          <td>{{perDiem.date | date: 'shortDate'}}</td>
          <td><label>Request Meals: </label><input type="checkbox" ng-model="perDiem.isReimbursementRequested"></td>
        </tr>
        </tbody>
      </table>
</div>
    </ess-travel-inner-container>

    <ess-travel-inner-container title="Lodging Adjustment (Optional)" ng-show="tripHasLodging()">
      <p class="travel-text margin-bottom-20">
        You qualify for the following lodging reimbursements. Uncheck anything you would <span class="bold">not</span>
        like to be reimbursed for.
      </p>
      <div>
        <table class="travel-table">
          <thead>
          <tr>
            <td>Address</td>
            <td>Date</td>
            <td>Request Reimbursement?</td>
          </tr>
          </thead>
          <tbody>
          <tr ng-repeat="perDiem in dirtyApp.route.lodgingPerDiems.allLodgingPerDiems">
            <td>{{perDiem.address.formattedAddress}}</td>
            <td>{{previousDay(perDiem.date) | date: 'shortDate'}} - {{perDiem.date | date: 'shortDate'}}</td>
            <td><label>Request Lodging: </label><input type="checkbox" ng-model="perDiem.isReimbursementRequested"></td>
          </tr>
          </tbody>
        </table>
      </div>
    </ess-travel-inner-container>

    <ess-travel-inner-container title="Mileage Adjustment (Optional)" ng-show="tripHasMileage()">
      <p class="travel-text margin-bottom-20">
        You qualify for the following mileage reimbursements. Uncheck anything you would <span class="bold">not</span>
        like to be reimbursed for.
      </p>
      <div>
        <table class="travel-table">
          <thead>
          <tr>
            <td>From</td>
            <td>To</td>
            <td>Request Reimbursement?</td>
          </tr>
          </thead>
          <tbody>
          <tr ng-repeat="leg in dirtyApp.route.mileagePerDiems.qualifyingLegs">
            <td>{{leg.from.address.formattedAddress}}</td>
            <td>{{leg.to.address.formattedAddress}}</td>
            <td><label>Request Mileage: </label><input type="checkbox" ng-model="leg.isReimbursementRequested"></td>
          </tr>
          </tbody>
        </table>
      </div>
    </ess-travel-inner-container>

    <div class="travel-button-container">
      <input type="button" class="neutral-button" ng-value="::negativeLabel || 'Cancel'"
             ng-click="cancel()">
      <input type="button" class="travel-neutral-button" value="Back"
             title="Back"
             ng-click="back()">
      <input type="submit" class="submit-button"
             title="Continue to next step" value="Next">
    </div>
  </form>
</div>