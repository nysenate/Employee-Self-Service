<div class="content-container no-top-margin text-align-center">
  <h3 class="content-info">Lodging Details</h3>
  <div class="margin-20">
    <table class="travel-table">
      <thead>
      <tr>
        <td>Date</td>
        <td>Address</td>
        <td>Nightly Rate</td>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="allowance in lodgingAllowances">
        <td>{{allowance.date | date: 'shortDate'}}</td>
        <td>{{allowance.address.formattedAddress}}</td>
        <td>{{allowance.allowance | currency}}</td>
      </tr>
      <tr>
        <td></td>
        <td class="bold">Total:</td>
        <td class="bold">{{total | currency}}</td>
      </tr>
      </tbody>
    </table>
  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Exit"
           ng-click="closeModal()">
  </div>
</div>