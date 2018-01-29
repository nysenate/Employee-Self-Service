<div class="content-container text-align-center">
  <div>
    <h1 class="content-info">Estimated Expenses</h1>
    <p class="margin-20">
      If you wish to be reimbursed for mileage, meal, or lodging expenses, select the appropriate boxes. <br>
    </p>
    <div class="margin-20">
      <div ng-repeat="dest in destinations" class="margin-10">
        <div class="inline-block" style="width: 550px;">
          <travel-destination-directive destination="dest"></travel-destination-directive>
        </div>

        <span class="margin-10">
        <label>Mileage <input type="checkbox" ng-model="dest.requestMileage"></label>
      </span>
        <span class="margin-10">
        <label>Meals <input type="checkbox" ng-model="dest.requestMeals"></label>
      </span>
        <span class="margin-10">
        <label>Lodging <input type="checkbox" ng-model="dest.requestLodging"></label>
      </span>
      </div>
    </div>

    <hr class="width-90 margin-20">

    <p>
      If you wish to request reimbursement for any of the following categories, enter your estimated expenses.
    </p>
    <div class="width-50 margin-top-20" style="margin: auto;">
      <div class="grid" style="min-width: 0;">
        <div class="col-6-12 padding-bottom-10">
          <label class="travel-allowance-label">Tolls:</label>
          <input ng-model="allowances.tolls" type="number" step="0.01" min="0" style="width: 5em;">
        </div>
        <div class="col-6-12 padding-bottom-10">
          <label class="travel-allowance-label">Parking:</label>
          <input ng-model="allowances.parking" type="number" step="0.01" min="0" style="width: 5em;">
        </div>
        <div class="col-6-12">
          <label class="travel-allowance-label">Taxi/Bus/Subway:</label>
          <input ng-model="allowances.alternate" type="number" step="0.01" min="0" style="width: 5em;">
        </div>
        <div class="col-6-12">
          <label class="travel-allowance-label">Registration Fee:</label>
          <input ng-model="allowances.registrationFee" type="number" step="0.01" min="0" style="width: 5em;">
        </div>
      </div>
    </div>

  </div>

  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Back"
           ng-click="allowancesCallback(destinations, allowances, ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Next"
           ng-click="allowancesCallback(destinations, allowances, ACTIONS.NEXT)">
  </div>
</div>