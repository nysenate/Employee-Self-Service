<div>
  <p class="dark-gray margin-0">{{lineItem.item.standardQuantity}}/Pack</p>
  <div ng-show="!isInCart(lineItem.item)">
    <input class="add-to-cart-btn" ng-click="addToCart(lineItem)"
           type="button" value="Add to Cart">
  </div>
  <div ng-show="isInCart(lineItem.item)">
    <input class="qty-adjust-button"
           ng-click="decrementQuantity(lineItem)"
           type="button" value="-">
    <input order-quantity-validator
           class="qty-input"
           type="text"
           ng-change="onCustomQtyEntered(lineItem)"
           ng-model="lineItem.quantity"
           ng-model-options="{updateOn: 'blur'}"
           min="0"
           step="1"
           maxlength="4">
    <input class="qty-adjust-button"
           ng-class="{'dark-warn': isAtMaxQty(lineItem) || isOverMaxQty(lineItem)}"
           ng-click="incrementQuantity(lineItem)"
           type="button" value="+">
  </div>
</div>

