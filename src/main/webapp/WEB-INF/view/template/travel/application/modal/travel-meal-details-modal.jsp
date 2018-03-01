<div class="content-container no-top-margin text-align-center">
  <h3 class="content-info">Meal Details</h3>
  <div class="margin-20">
    <table class="travel-table">
      <thead>
      <tr>
        <td>Date</td>
        <td>Address</td>
        <td>Breakfast</td>
        <td>Dinner</td>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="mealDay in mealAllowance.mealDays">
        <td>{{mealDay.date | date: 'shortDate'}}</td>
        <td>{{mealDay.address.formattedAddress}}</td>
        <td>{{mealDay.mealTier.breakfast | currency}}</td>
        <td>{{mealDay.mealTier.dinner | currency}}</td>
      </tr>
      <tr>
        <td></td>
        <td class="bold">Total:</td>
        <td></td>
        <td class="bold">{{mealAllowance.total | currency}}</td>
      </tr>
      </tbody>
    </table>
  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Exit"
           ng-click="closeModal()">
  </div>
</div>