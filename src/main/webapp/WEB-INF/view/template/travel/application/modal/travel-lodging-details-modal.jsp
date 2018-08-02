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
      <tr ng-repeat="lodgingAllowance in app.lodgingAllowance.lodgingAllowances"
          ng-if="lodgingAllowance.isLodgingRequested">
        <td>{{lodgingAllowance.date | date: 'shortDate'}}</td>
        <td>{{lodgingAllowance.address.formattedAddress}}</td>
        <td>{{lodgingAllowance.allowance | currency}}</td>
      </tr>
      </tbody>
      <tbody>
      <tr>
        <td></td>
        <td class="bold">Total:</td>
        <td class="bold">{{app.lodgingAllowance.totalLodgingAllowance | currency}}</td>
      </tr>
      </tbody>
    </table>
  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Exit"
           ng-click="closeModal()">
  </div>
</div>