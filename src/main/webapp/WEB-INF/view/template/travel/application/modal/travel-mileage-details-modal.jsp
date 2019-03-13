<div class="content-container no-top-margin text-align-center">
  <h3 class="content-info">Calculated Mileage Expenses</h3>
  <div class="margin-20">
    <table class="travel-table">
      <thead>
      <tr>
        <td>From</td>
        <td>To</td>
        <td>Miles</td>
        <td>Rate</td>
        <td>Mode of Transportation</td>
        <td>Allowance</td>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="leg in legs">
        <td>{{leg.from.address.formattedAddress}}</td>
        <td>{{leg.to.address.formattedAddress}}</td>
        <td>{{leg.miles}}</td>
        <td>{{leg.mileageRate}}</td>
        <td>{{leg.modeOfTransportation.displayName}}</td>
        <td>{{leg.mileageExpense | currency}}</td>
      </tr>
      <tr>
        <td></td>
        <td class="bold">Total:</td>
        <td class="bold">{{app.route.totalMiles}}</td>
        <td></td>
        <td></td>
        <td class="bold">{{app.route.mileageExpense | currency}}<span ng-if="displayRequirements">*</span></td>
      </tr>
      </tbody>
    </table>
  </div>
  <div class="margin-top-20" ng-if="displayRequirements">
    <p>* The outbound leg of a trip must be greater than 35 miles to qualify for mileage reimbursement.</p>
  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Exit"
           ng-click="closeModal()">
  </div>
</div>