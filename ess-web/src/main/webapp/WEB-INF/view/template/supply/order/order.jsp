<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="SupplyOrderController">
  <div class="supply-order-hero inline-block width-100">
    <h2 class="requisition-title">Supply Requisition Form</h2>
    <a href="${ctxPath}/supply/order/cart/cart">
      <cart-summary class="cart-widget"></cart-summary>
    </a>
  </div>

  <div class="content-container">
    <dir-pagination-controls class="text-align-center" pagination-id="item-pagination" boundary-links="true" max-size="10"></dir-pagination-controls>
    <div class="grid grid-pad">
      <div class="col-3-12 text-align-center"
           dir-paginate="item in items | itemsPerPage: 16"
           pagination-id="item-pagination"
           ng-hide="hideItem(item)">
        <img ng-src="${ctxPath}/assets/img/supply/{{item.id}}.jpg" class="supply-item-image">
        <div>
          <h3 class="dark-gray bold">{{item.name}}</h3>
          <p class="dark-gray" style="height: 50px;">{{item.description}}</p>
          <p class="dark-gray bold">{{item.unit}}</p>
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
    <dir-pagination-controls class="text-align-center" pagination-id="item-pagination"  boundary-links="true" max-size="10"></dir-pagination-controls>
  </div>
</div>
