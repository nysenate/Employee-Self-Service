<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>

<div ng-controller="SupplyCartController">
  <div class="supply-order-hero inline-block width-100">
    <h2 class="requisition-title">Shopping Cart</h2>
    <a href="${ctxPath}/supply/order/cart/cart">
      <cart-summary class="cart-widget"></cart-summary>
    </a>
  </div>
  <%--Empty cart--%>
  <div class="content-container" ng-show="!cartHasItems()">
    <div class="content-info">
      <h2 class="dark-gray">Your cart is empty.</h2>
    </div>
  </div>

  <div class="content-container">
    <div class="grid" ng-class="{'padding-top-10': $first}" ng-repeat="cartItem in myCartItems()">
      <hr ng-if="!$first"/>
      <div class="col-4-12 text-align-center">
        <div class="content">
          <img ng-src="${ctxPath}/assets/img/supply/no_photo_available.png" class="supply-item-image-big">
        </div>
      </div>
      <div class="col-6-12">
        <div class="content">
          <h3 class="dark-gray">{{cartItem.item.description}}</h3>
          <a ng-click="removeFromCart(cartItem.item)" href="#">delete</a>
        </div>
      </div>
      <div class="col-2-12">
        <div class="content">
          <p class="dark-gray bold cart-unit-size">{{cartItem.item.standardQuantity}}/Pack</p>
          <label class="custom-select">Qty:
            <select requisition-quantity-selector item="cartItem.item" warn-qty="cartItem.item.suggestedMaxQty + 1"
                    ng-model="cartItem.quantity" ng-options="qty for qty in orderQuantityRange(cartItem.item)"></select>
          </label>
        </div>
      </div>
    </div>

    <div ng-show="cartHasItems()" class="grid">
      <hr/>
      <div class="col-8-12 force-min-height"></div>
      <div class="col-2-12">
        <a href="${ctxPath}/supply/order/order">
          <input class="submit-button margin-10" type="button" value="Continue Browsing">
        </a>
      </div>
      <div class="col-2-12">
          <input ng-click="submitOrder()" class="submit-button margin-10" type="button" value="Checkout">
      </div>
    </div>
  </div>

  <div modal-container>
    <div ng-if="isOpen('supply-cart-checkout-modal')" class="save-progress-modal">
      <h3 class="content-info" style="margin-bottom:0;">Your Requisition Request has been submitted!</h3>
      <h4>What would you like to do next?</h4>
      <input ng-click="logout()" class="reject-button" type="button" value="Log out of ESS"/>
      <input ng-click="viewOrder()" class="submit-button" type="button" value="Back to ESS"/>
    </div>
  </div>

</div>