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
        <td>Allowance</td>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="leg in app.route.mileagePerDiems.requestedLegs">
        <td>{{leg.from.address.formattedAddress}}</td>
        <td>{{leg.to.address.formattedAddress}}</td>
        <td>{{leg.miles}}</td>
        <td>{{leg.mileageRate}}</td>
        <td ng-class="{'line-through': isOverridden}">{{leg.requestedPerDiem | currency}}</td>
      </tr>
      <tr ng-class="{'line-through': isOverridden}">
        <td></td>
        <td class="bold">Total:</td>
        <td></td>
        <td></td>
        <td class="bold">{{app.route.mileagePerDiems.requestedPerDiem | currency}}</td>
      </tr>
      <tr ng-show="isOverridden">
        <td></td>
        <td class="disapproved-text">Mileage Overridden to:</td>
        <td></td>
        <td></td>
        <td class="disapproved-text" ng-bind="::app.perDiemOverrides.mileageOverride | currency"></td>
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