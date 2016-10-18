<div class="padding-10">
  <div>
    <h3 class="content-info">Enter Quantity</h3>
  </div>
  <div class="bold">
    Enter the quantity you would like to request.
  </div>
  <div class="padding-10 text-align-center">
    <form name="orderCustomQuantityForm" novalidate>
      <label>Requesting quantity: </label>
      <input name="requestedQuantity"
             custom-order-quantity-validator
             ng-class="{'warn-important':orderCustomQuantityForm.requestedQuantity.$error.customOrderQuantity}"
             ng-model="quantity"
             type="number" min="1" step="1"
             style="width: 80px">
      <input class="neutral-button" type="button" value="Cancel" ng-click="cancel()">
      <input class="submit-button" type="button" value="Add to Cart" ng-click="addToCart()"
             ng-disabled="orderCustomQuantityForm.requestedQuantity.$error.customOrderQuantity">
    </form>
  </div>
  <div class="redorange" ng-show="orderCustomQuantityForm.requestedQuantity.$error.customOrderQuantity">
    Please enter a number between 0 and 1000.
  </div>
</div>