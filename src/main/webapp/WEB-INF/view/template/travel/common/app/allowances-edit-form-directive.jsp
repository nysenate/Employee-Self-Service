<div>
  <div ng-if="allowancesForm.$submitted && !allowancesForm.$valid">
    <ess-notification level="error" message="">
      <ul>
        <li ng-show="allowancesForm.$error.step">Expenses must be in increments of 0.01</li>
        <li ng-show="allowancesForm.$error.min">Expenses cannot be negative</li>
        <li ng-show="allowancesForm.$error.number">Expenses must be a number</li>
      </ul>
    </ess-notification>
  </div>
  <div class="travel-card">
    <form novalidate name="allowancesForm" id="allowancesForm"
          ng-submit="allowancesForm.$valid && next()">

      <div class="travel-card-item">
        <h1 class="">Miscellaneous Expenses</h1>
        <div class="padding-10">
          <div style="padding: 0px 10px 10px 10px;">Enter your estimated expenses for the following categories.</div>
          <div class="expenses-flex-grid">
            <div class="left">
              <div class="row">
                <label class="">Tolls: $</label>
                <input ng-model="dirtyDraft.amendment.allowances.tolls" type="number" step="0.01" min="0"
                       style="width: 5em;">
              </div>
              <div class="row">
                <label class="">Parking: $</label>
                <input ng-model="dirtyDraft.amendment.allowances.parking" type="number" step="0.01" min="0"
                       style="width: 5em;">
              </div>
              <div class="row">
                <label class="">Taxi/Bus/Subway: $</label>
                <input ng-model="dirtyDraft.amendment.allowances.alternateTransportation" type="number" step="0.01"
                       min="0" style="width: 5em;">
              </div>
              <div class="row">
                <label class="">Train/Airplane: $</label>
                <input ng-model="dirtyDraft.amendment.allowances.trainAndPlane" type="number" step="0.01" min="0"
                       style="width: 5em;">
              </div>
              <div class="row">
                <label class="">Registration Fee: $</label>
                <input ng-model="dirtyDraft.amendment.allowances.registration" type="number" step="0.01" min="0"
                       style="width: 5em;">
              </div>
            </div>
            <div class="travel-note">
              <p>Meals, lodging, and mileage expenses will be calculated automatically.</p>
            </div>
          </div>
        </div>
      </div>

      <div class="travel-card-item" ng-show="tripHasMeals()">
        <h1 class="">Meals Adjustment <em class="optional">(Optional)</em></h1>
        <div class="padding-10">
          <span class="left padding-left-10" style="vertical-align: top; width: 45%; display: inline-block;">
            You may<i class="asterisk">*</i> qualify for the following meal reimbursements. Uncheck anything you would
            <span class="bold">not</span> like to be reimbursed for.
          </span>
          <div class="travel-note" style="margin-left: 56px;">
            <p>
              * Meal reimbursement eligibility will depend on your arrival and departure times.
              See <a href="https://my.nysenate.gov/sites/default/files/2020-12/meal-rates-and-guidelines.pdf"
                     target="_blank">
              Senate Meal Rates and Guidelines</a> for details.
              </a>
            </p>
          </div>
          <div class="padding-top-10">
            <table class="travel-table">
              <thead>
              <tr>
                <td>Address</td>
                <td>Date</td>
                <td>Request Reimbursement?</td>
              </tr>
              </thead>
              <tbody>
              <tr ng-repeat="perDiem in dirtyDraft.amendment.mealPerDiems.allMealPerDiems">
                <td>{{perDiem.address.formattedAddressWithCounty}}</td>
                <td>{{perDiem.date | date: 'shortDate'}}</td>
                <td><label>Request Meals: </label><input type="checkbox" ng-model="perDiem.isReimbursementRequested">
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div class="travel-card-item" ng-show="tripHasLodging()">
        <h1 class="">Lodging Adjustment <em class="optional">(Optional)</em></h1>
        <div class="padding-10">
          <span class="padding-left-10">You qualify for the following lodging reimbursements. Uncheck anything you would <span
              class="bold">not</span> like to be reimbursed for.</span>
          <div class="padding-top-10">
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
                <tr ng-repeat="perDiem in dirtyDraft.amendment.lodgingPerDiems.allLodgingPerDiems">
                  <td>{{perDiem.address.formattedAddressWithCounty}}</td>
                  <td>{{previousDay(perDiem.date) | date: 'shortDate'}} - {{perDiem.date | date: 'shortDate'}}</td>
                  <td><label>Request Lodging: </label><input type="checkbox"
                                                             ng-model="perDiem.isReimbursementRequested">
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <div class="travel-card-item"
           ng-show="dirtyDraft.amendment.route.mileagePerDiems.doesTripQualifyForReimbursement">
        <h1 class="">Mileage Adjustment <em class="optional">(Optional)</em></h1>
        <div class="padding-10">
        <span class=""> You qualify for the following mileage reimbursements. Uncheck anything you would <span
            class="bold">not</span> like to be reimbursed for.</span>
          <div class="padding-top-10">
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
                <tr ng-repeat="leg in dirtyDraft.amendment.route.mileagePerDiems.allLegs"
                    ng-if="leg.qualifiesForReimbursement">
                  <td>{{leg.from.address.formattedAddressWithCounty}}</td>
                  <td>{{leg.to.address.formattedAddressWithCounty}}</td>
                  <td><label>Request Mileage: </label><input type="checkbox" ng-model="leg.isReimbursementRequested">
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <div class="travel-button-container">
        <button class="travel-primary-btn"
                ng-click="back()">
          Back
        </button>
        <button class="travel-neutral-btn"
                ng-click="cancel()">
          {{::negativeLabel || 'Cancel'}}
        </button>
        <button type="submit" class="travel-submit-btn">
          Next
        </button>
      </div>
    </form>
  </div>
</div>