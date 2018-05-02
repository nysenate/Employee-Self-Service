<div class="content-container text-align-center">
  <div>
    <h1>Meals</h1>
    <div ng-repeat="accommodation in app.accommodations">
      <%--<h4>{{acc.address.formattedAddress}}</h4>--%>
      <%--<div ng-repeat="stay in app.accommodations.stays">--%>
        <table class="travel-table">
          <thead>
          <tr>
            <td>Address</td>
            <td>Date</td>
            <td>Meal Allowance</td>
          </tr>
          </thead>
          <tbody>
          <tr ng-repeat="stay in accommodation.stays">
            <td>{{accommodation.address.formattedAddress}}</td>
            <td>{{stay.date | date: 'shortDate'}}</td>
            <td>{{stay.allowance | currency}}</td>
          </tr>
          </tbody>
        </table>

      </div>
    </div>
  </div>

  <div>
    <div>
      <h1>Miscellaneous Expenses (optional):</h1>
      <p class="margin-20">
        If you wish to request reimbursement for any of the following categories, enter your estimated expenses.
      </p>
      <div class="width-50 margin-top-20" style="margin: auto;">
        <div class="grid" style="min-width: 0;">
          <div class="col-6-12 padding-bottom-10">
            <label class="travel-allowance-label">Tolls: $</label>
            <input ng-model="allowances.tollsAllowance" type="number" step="0.01" min="0" style="width: 5em;">
          </div>
          <div class="col-6-12 padding-bottom-10">
            <label class="travel-allowance-label">Parking: $</label>
            <input ng-model="allowances.parkingAllowance" type="number" step="0.01" min="0" style="width: 5em;">
          </div>
          <div class="col-6-12">
            <label class="travel-allowance-label">Taxi/Bus/Subway: $</label>
            <input ng-model="allowances.alternateAllowance" type="number" step="0.01" min="0" style="width: 5em;">
          </div>
          <div class="col-6-12">
            <label class="travel-allowance-label">Registration Fee: $</label>
            <input ng-model="allowances.registrationAllowance" type="number" step="0.01" min="0" style="width: 5em;">
          </div>
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