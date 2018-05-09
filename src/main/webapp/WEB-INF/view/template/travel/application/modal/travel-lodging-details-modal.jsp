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
      <tbody ng-repeat="accommodation in app.accommodations">
      <tr ng-repeat="night in accommodation.nights"
          ng-if="night.isLodgingRequested">
        <td>{{night.date | date: 'shortDate'}}</td>
        <td>{{accommodation.address.formattedAddress}}</td>
        <td>{{night.lodgingAllowance | currency}}</td>
      </tr>
      </tbody>
      <tbody>
      <tr>
        <td></td>
        <td class="bold">Total:</td>
        <td class="bold">{{app.lodgingAllowance | currency}}</td>
      </tr>
      </tbody>
    </table>
  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Exit"
           ng-click="closeModal()">
  </div>
</div>