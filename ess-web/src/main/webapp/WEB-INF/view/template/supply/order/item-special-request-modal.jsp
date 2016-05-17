<div class="padding-10">
  <div>
    <h3 class="content-info">Special Request</h3>
  </div>
  <div class="bold">
    You are submitting a special request. This will require management approval.
  </div>
  <div class="padding-10 text-align-center">
    <form name="specialRequestForm" novalidate>
      <label>Requesting quantity: </label>
      <input name="requestedQuantity"
             whole-number-validator
             ng-model="quantity"
             type="number" min="1" step="1"
             style="width: 80px">
      <input class="neutral-button" type="button" value="Cancel" ng-click="cancel()">
      <input class="submit-button" type="button" value="Add to Cart" ng-click="addToCart()"
             ng-disabled="specialRequestForm.requestedQuantity.$error.wholeNumber">
    </form>
  </div>
</div>