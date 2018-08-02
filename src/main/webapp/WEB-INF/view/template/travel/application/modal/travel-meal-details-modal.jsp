<div class="content-container no-top-margin text-align-center">
  <h3 class="content-info">Meal Details</h3>
  <div class="margin-20">
    <table class="travel-table">
      <thead>
      <tr>
        <td>Date</td>
        <td>Address</td>
        <td>Meal Allowance</td>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="mealAllowance in app.mealAllowance.mealAllowances"
          ng-if="mealAllowance.isMealsRequested">
        <td>{{mealAllowance.date | date: 'shortDate'}}</td>
        <td>{{mealAllowance.address.formattedAddress}}</td>
        <td>{{mealAllowance.allowance | currency}}</td>
      </tr>
      </tbody>
      <tbody>
      <tr>
        <td></td>
        <td class="bold">Total:</td>
        <td class="bold">{{app.mealAllowance.totalMealAllowance | currency}}</td>
      </tr>
      </tbody>
    </table>
  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Exit"
           ng-click="closeModal()">
  </div>
</div>