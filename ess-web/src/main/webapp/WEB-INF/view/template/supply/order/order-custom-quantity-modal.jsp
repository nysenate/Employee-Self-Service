<div class="padding-10">
  <div>
    <h3 class="content-info">Enter Quantity</h3>
  </div>
  <div class="bold">
    Enter the quantity you would like to request.
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