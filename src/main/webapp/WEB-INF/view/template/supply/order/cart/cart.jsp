<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>

<div ng-controller="SupplyCartController">
  <div class="supply-order-hero inline-block width-100">
    <h2>Shopping Cart</h2>
  </div>
  <%--Empty cart--%>
  <div class="content-container" ng-show="!cartHasItems()">
    <div class="content-info">
      <h2 class="dark-gray">Your cart is empty.</h2>
    </div>
    <div class="cart-checkout-container">
      <div class="float-right">
        <a href="${ctxPath}/supply/order">
          <input class="neutral-button" type="button" value="Continue Browsing">
        </a>
      </div>
      <div class="clearfix"></div>
    </div>
  </div>

  <div class="content-container" ng-show="cartHasItems()">
    <div class="content-info">
      <div class="padding-10" style="display: flex; justify-content: space-between;">
        <div style="display: inline-block;">
          <span class="supply-text">Destination: </span>{{destinationCode}} ({{destinationDescription}})
        </div>
      </div>
    </div>

    <div class="grid" ng-class="{'padding-top-10': $first}" ng-repeat="cartItem in myCartItems()">
      <hr ng-if="!$first"/>
      <div class="col-4-12 text-align-center">
        <div class="content">
          <img ng-src="${imageUrl}/{{cartItem.item.commodityCode}}.jpg"
               err-src="${ctxPath}/assets/img/supply/no_photo_available.png"
               class="supply-item-image-big">
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

          <div ng-show="!orderedOverPerOrderMax(cartItem)">
            <label class="custom-select">Qty:
              <select ng-model="cartItem.quantity"
                      ng-options="qty for qty in orderQuantityRange(cartItem.item)"></select>
            </label>
          </div>

          <div ng-show="orderedOverPerOrderMax(cartItem)">
            <input name="specialOrderQuantity"
                   whole-number-validator
                   ng-model="cartItem.quantity"
                   type="number" min="1" step="1"
                   style="width: 80px">
          </div>

        </div>
      </div>
    </div>
    <hr/>

    <div class="cart-checkout-container">
      <div class="special-instructions-container">
        <label for="special-instructions-area">Special Instructions</label>
        <textarea id="special-instructions-area" class="special-instructions-text-area"
                  ng-model="specialInstructions">
        </textarea>
      </div>
      <%--<div class="col-8-12 force-min-height"></div>--%>
      <div class="float-right">
        <input class="neutral-button" type="button" value="Empty Cart" ng-click="emptyCart()">
        <a href="${ctxPath}/supply/order">
          <input class="neutral-button" type="button" value="Continue Browsing">
        </a>
        <input ng-click="submitOrder()" class="submit-button" type="button" value="Checkout">
      </div>
      <div class="clearfix"></div>
    </div>
  </div>

  <div modal-container>
    <modal modal-id="supply-cart-checkout-modal">
      <cart-checkout-modal></cart-checkout-modal>
    </modal>
  </div>

</div>