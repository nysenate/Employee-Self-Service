<div class="content-container no-top-margin padding-top-5">
  <h3 class="content-info">Expense Summary</h3>
  <ess-travel-inner-container title="Miscellaneous Expenses">
    <div>
      <div class="grid" style="padding-left: 300px; padding-right: 200px;">
        <div class="col-6-12 margin-bottom-5">
          Tolls:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{app.tollsAllowance | currency}}
        </div>
        <div class="col-6-12 margin-bottom-5">
          Parking:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{app.parkingAllowance | currency}}
        </div>
        <div class="col-6-12 margin-bottom-5">
          Taxi/Bus/Subway:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{app.alternateTransportationAllowance | currency}}
        </div>
        <div class="col-6-12 margin-bottom-5">
          Train/Airplane:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{app.trainAndPlaneAllowance | currency}}
        </div>
        <div class="col-6-12 margin-bottom-5">
          Registration Fee:
        </div>
        <div class="col-6-12 margin-bottom-5">
          {{app.registrationAllowance | currency}}
        </div>
      </div>
    </div>
  </ess-travel-inner-container>

  <ess-travel-inner-container title="Meal Expenses">
    <div class="margin-20">
      <table class="travel-table">
        <thead>
        <tr>
          <td>Date</td>
          <td>Address</td>
          <td>Breakfast</td>
          <td>Dinner</td>
          <td>Total</td>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="perDiem in app.mealPerDiems.requestedMealPerDiems">
          <td>{{perDiem.date | date: 'shortDate'}}</td>
          <td>{{perDiem.address.formattedAddressWithCounty}}</td>
          <td>{{::(perDiem.mie.breakfast | currency) || NOT_AVAILABLE }}</td>
          <td>{{::(perDiem.mie.dinner | currency) || NOT_AVAILABLE }}</td>
          <td>{{perDiem.rate | currency}}</td>
        </tr>
        <tr>
          <td></td>
          <td class="bold">Total:</td>
          <td></td>
          <td></td>
          <td class="bold">{{app.mealPerDiems.totalPerDiem | currency}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </ess-travel-inner-container>

  <ess-travel-inner-container title="Lodging Expenses">
    <div class="margin-20">
      <table class="travel-table">
        <thead>
        <tr>
          <td>Date</td>
          <td>Address</td>
          <td>Lodging Per Diem</td>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="perDiem in app.lodgingPerDiems.requestedLodgingPerDiems">
          <td>{{perDiem.date | date: 'shortDate'}}</td>
          <td>{{perDiem.address.formattedAddressWithCounty}}</td>
          <td ng-class="{'line-through': isOverridden}">{{perDiem.rate | currency}}</td>
        </tr>
        <tr ng-class="{'line-through': isOverridden}">
          <td></td>
          <td class="bold">Total:</td>
          <td class="bold">{{app.lodgingPerDiems.totalPerDiem | currency}}</td>
        </tr>
        <tr ng-show="isOverridden">
          <td></td>
          <td class="disapproved-text">Lodging Overridden:</td>
          <td class="disapproved-text" ng-bind="::app.perDiemOverrides.lodgingOverride | currency"></td>
        </tr>
        </tbody>
      </table>
    </div>
  </ess-travel-inner-container>

  <ess-travel-inner-container title="Mileage Expenses">
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
          <td>{{leg.from.address.formattedAddressWithCounty}}</td>
          <td>{{leg.to.address.formattedAddressWithCounty}}</td>
          <td>{{leg.miles}}</td>
          <td>{{leg.mileageRate}}</td>
          <td >{{leg.requestedPerDiem | currency}}</td>
        </tr>
        <tr>
          <td></td>
          <td class="bold">Total:</td>
          <td></td>
          <td></td>
          <td class="bold">{{app.route.mileagePerDiems.totalPerDiem | currency}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </ess-travel-inner-container>

  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Exit" ng-click="closeModal()">
  </div>
</div>

