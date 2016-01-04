<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="SupplyOrderController">
  <div class="supply-order-hero inline-block width-100">
    <h2 class="requisition-title">Supply Requisition Form</h2>
    <a href="${ctxPath}/supply/order/cart/cart">
      <cart-summary class="cart-widget"></cart-summary>
    </a>
  </div>

  <div class="content-container">
    <div class="grid grid-pad">
      <div class="col-3-12 requisition-form-item" ng-repeat="item in items track by $index" ng-hide="hideItem(item)">
        <img ng-src="${ctxPath}/assets/img/supply/{{item.id}}.jpg" class="supply-item-image">
        <div>
          <h3 class="dark-gray bold">{{item.name}}</h3>
          <p class="dark-gray">{{item.description}}</p>
          <p class="dark-gray bold">{{item.unitSize}}/Pack</p>
        </div>
        <div style="">
          <label class="custom-select">
            <select requisition-quantity-selector item="item" warn-qty="item.suggestedMaxQty + 1"
                    ng-model="quantity" ng-options="qty for qty in orderQuantityRange(item)"></select>
          </label>
          <input class="submit-button add-to-cart-btn" ng-click="addToCart(item, quantity)" type="button" value="Add to Cart">
        </div>
        <div ng-class="{'visibility-hidden': !isInCart(item)}" class="green padding-top-5 bold">
          &#x2713; Added to cart.
        </div>
      </div>
    </div>
  </div>
</div>
