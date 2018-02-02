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
      <tr ng-repeat="night in lodgingAllowance.lodgingNights">
        <td>{{night.date | date: 'shortDate'}}</td>
        <td>{{night.address.formattedAddress}}</td>
        <td>{{night.rate | currency}}</td>
      </tr>
      <tr>
        <td></td>
        <td class="bold">Total:</td>
        <td class="bold">{{lodgingAllowance.total | currency}}</td>
      </tr>
      </tbody>
    </table>
  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Exit"
           ng-click="closeModal()">
  </div>
</div>