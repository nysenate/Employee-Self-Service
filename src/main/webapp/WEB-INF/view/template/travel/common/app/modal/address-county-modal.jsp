<div class="content-container no-top-margin text-align-center">
  <h3 class="content-info">Enter County</h3>
  <form name="countyForm" ng-submit="countyForm.$valid && submit()" novalidate>
  <div class="margin-20">
    <p>
      Unable to determine the county for: {{address.formattedAddress}}.
    </p>
    <p>
      Please enter the county: <input id="countyInput" type="text" ng-model="address.county" required>
    </p>
  </div>
  <div class="travel-button-container">
    <input type="submit" class="submit-button" value="Save County">
    <input type="button" class="neutral-button" value="Cancel" ng-click="cancel()">
  </div>
  </form>
</div>