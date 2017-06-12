<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>

<div ng-controller="SupplyCartController">
    <div class="supply-order-hero inline-block width-100">
      <h2>Shopping Cart</h2>
    </div>

  <div class="content-container content-controls">
    <%--Empty cart--%>
    <div ng-show="getLineItems().length == 0">
      <div class="content-info" style="border-bottom: none;">
        <h2 class="dark-gray">Your cart is empty.</h2>
      </div>
      <div class="cart-checkout-container">
        <div class="float-right">
          <a href="${ctxPath}/supply/shopping/order">
            <input class="neutral-button" type="button" value="Continue Browsing">
          </a>
        </div>
        <div class="clearfix"></div>
      </div>
    </div>

    <%--Non empty cart--%>
    <div ng-show="getLineItems().length > 0">
      <div class="content-info" style="border-bottom: none;">
        <div class="padding-10" style="display: flex; justify-content: space-between;">
          <div style="display: inline-block;">
            <span class="supply-text">Destination: </span>{{destinationCode}} ({{destinationDescription}})
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="content-container" ng-show="getLineItems().length > 0">
    <div class="grid" ng-class="{'padding-top-10': $first}" ng-repeat="lineItem in getLineItems()">
      <hr ng-if="!$first"/>
      <div class="col-3-12 text-align-center">
        <div class="content" style="padding-left: 5px">
          <img class="supply-item-image"
               ng-click="displayLargeImage(lineItem.item)"
               ng-src="${imageUrl}/{{lineItem.item.commodityCode}}.jpg"
               err-src="${imageUrl}/no_photo_available.png">
        </div>
      </div>
      <div class="col-6-12">
        <div class="content">
          <h3 class="dark-gray">{{lineItem.item.description}}</h3>
        </div>
      </div>
      <div class="col-3-12 margin-top-20">
        <supply-quantity-selector line-item="lineItem">
        </supply-quantity-selector>
      </div>
    </div>
    <hr/>

    <div class="cart-checkout-container">
      <div class="special-instructions-container">
        <label for="special-instructions-area">Special Instructions</label>
        <textarea id="special-instructions-area" class="special-instructions-text-area"
                  ng-model="specialInstructions" ng-change="saveSpecialInstructions()">
        </textarea>
      </div>
      <%--<div class="col-8-12 force-min-height"></div>--%>
      <div class="float-right">
        <input class="neutral-button" type="button" value="Empty Cart" ng-click="emptyCart()">
        <a href="${ctxPath}/supply/shopping/order">
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
    <modal modal-id="delivery-method-modal">
      <delivery-method-modal></delivery-method-modal>
    </modal>
    <modal modal-id="supply-cart-empty-modal">
      <supply-cart-empty-modal></supply-cart-empty-modal>
    </modal>
    <modal modal-id="order-more-prompt-modal">
      <div order-more-prompt-modal></div>
    </modal>
     <modal modal-id="large-item-image-modal">
      <div large-item-image-modal></div>
    </modal>
  </div>

</div>