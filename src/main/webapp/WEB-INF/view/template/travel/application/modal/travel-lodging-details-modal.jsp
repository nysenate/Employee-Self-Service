<div class="content-container no-top-margin text-align-center">
  <h3 class="content-info">Calculated Lodging Expenses</h3>
  <div class="margin-20">
    <table class="travel-table">
      <thead>
      <tr>
        <td>Date</td>
        <td>Address</td>
        <td>Lodging PerDiem</td>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="expense in lodgingExpenses">
        <td>{{expense.date | date: 'shortDate'}}</td>
        <td>{{expense.address.formattedAddress}}</td>
        <td>{{expense.lodgingExpense | currency}}</td>
      </tr>
      </tbody>
      <tbody>
      <tr>
        <td></td>
        <td class="bold">Total:</td>
        <td class="bold">{{sumLodgingExpenses() | currency}}</td>
      </tr>
      </tbody>
    </table>
  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Exit"
           ng-click="closeModal()">
  </div>
</div>