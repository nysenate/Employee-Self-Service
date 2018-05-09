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
      <tbody ng-repeat="accommodation in app.accommodations">
      <tr ng-repeat="day in accommodation.days"
          ng-if="day.isMealsRequested">
        <td>{{day.date | date: 'shortDate'}}</td>
        <td>{{accommodation.address.formattedAddress}}</td>
        <td>{{day.mealAllowance | currency}}</td>
      </tr>
      </tbody>
      <tbody>
      <tr>
        <td></td>
        <td class="bold">Total:</td>
        <td class="bold">{{app.mealAllowance | currency}}</td>
      </tr>
      </tbody>
    </table>
  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Exit"
           ng-click="closeModal()">
  </div>
</div>