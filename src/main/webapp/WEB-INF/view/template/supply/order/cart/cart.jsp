<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>

<div ng-controller="SupplyCartController">
  <div class="supply-order-hero inline-block width-100">
    <a style="margin-top: 10px;margin-left: 10px;" class="float-left" ng-click="resetDestination(this)"
       ng-hide="backHidden()">
      <img width="30px" height="30px" src="/assets/img/cancel.png">
    </a>
    <small class="float-left" style="color: white;margin-top: 42px;margin-left: -33px;" ng-hide="backHidden()">Cancel
    </small>
    <h2 class="requisition-title">Shopping Cart</h2>
    <a href="${ctxPath}/supply/order/cart">
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
    <div class="content-info" ng-show="cartHasItems()">
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

          <div ng-show="!orderedOverRecommended(cartItem)">
            <label class="custom-select">Qty:
              <select ng-model="cartItem.quantity"
                      ng-options="qty for qty in orderQuantityRange(cartItem.item)"></select>
            </label>
          </div>

          <div ng-show="orderedOverRecommended(cartItem)">
            <label>Qty:</label> {{cartItem.quantity}}
          </div>

        </div>
      </div>
    </div>

    <div ng-show="cartHasItems()" class="grid">
      <hr/>
      <div class="col-8-12 force-min-height"></div>
      <div class="col-2-12">
        <a href="${ctxPath}/supply/order">
          <input class="submit-button margin-10" type="button" value="Continue Browsing">
        </a>
      </div>
      <div class="col-2-12">
        <input ng-click="submitOrder()" class="submit-button margin-10" type="button" value="Checkout">
      </div>
    </div>
  </div>

  <div modal-container>
    <cart-checkout-modal ng-if="isOpen('supply-cart-checkout-modal')">
    </cart-checkout-modal>
  </div>

</div>