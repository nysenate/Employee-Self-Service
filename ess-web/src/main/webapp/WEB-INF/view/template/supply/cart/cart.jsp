<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>

<div ng-controller="SupplyCartController">
  <div class="supply-order-hero" style="display: inline-block; width: 100%">
    <%--Padding in h2 is to offset being 'pushed' left by cart image... TODO definately a better way--%>
    <h2 style="display: inline-block; padding-left: 100px">Shopping Cart</h2>
    <a href="${ctxPath}/supply/cart/cart">
      <cart-summary style="display: inline-block; float: right;"></cart-summary>
    </a>
  </div>
  <%--Empty cart--%>
  <div class="content-container" ng-show="!cartHasItems()">
    <div class="content-info">
      <h2 class="dark-gray">Your cart is empty.</h2>
    </div>
  </div>

  <div class="content-container">
    <div class="grid grid-pad" ng-repeat="cartItem in myCartItems()">
      <hr ng-if="!$first"/>
      <div class="col-3-12">
        <div class="content">
          <img ng-src="{{cartItem.product.img}}" style="height: 140px">
        </div>
      </div>
      <div class="col-7-12">
        <div class="content">
          <h2 class="dark-gray bold">{{cartItem.product.name}}</h2>
          <p class="dark-gray">{{cartItem.product.description}}</p>
          <a ng-click="removeFromCart(cartItem.product)" href="#">delete</a>
        </div>
      </div>
      <div class="col-2-12">
        <div class="content">
          <p class="dark-gray bold">{{cartItem.product.unitSize}}/Pack</p>
          <label class="custom-select">Qty:
            <select requisition-quantity-selector product="cartItem.product" warn-qty="cartItem.product.warnQuantity"
                    ng-model="cartItem.quantity" ng-options="qty for qty in orderQuantityRange(cartItem.product)"></select>
          </label>
        </div>
      </div>
    </div>

    <div ng-show="cartHasItems()" class="grid grid-pad">
      <hr/>
      <div class="col-8-12" style="min-height: 1px;"></div> <%--min height to prevent horizontal collapsing--%>
      <div class="col-2-12">
        <a href="${ctxPath}/supply/requisition/order">
          <input class="submit-button margin-10" type="button" value="Continue Browsing">
        </a>
      </div>
      <div class="col-2-12">
        <a href="${ctxPath}/supply/requisition/order">
          <input ng-click="" class="submit-button margin-10" type="button" value="Checkout">
        </a>
      </div>
    </div>
  </div>
</div>