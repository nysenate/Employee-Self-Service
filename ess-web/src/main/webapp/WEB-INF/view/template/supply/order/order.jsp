<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="SupplyOrderController">
  <div class="supply-order-hero inline-block width-100">
    <h2 class="requisition-title">Supply Requisition Form</h2>
    <a href="${ctxPath}/supply/order/cart">
      <cart-summary class="cart-widget"></cart-summary>
    </a>
  </div>

  <div loader-indicator class="loader" ng-show="state === states.LOADING"></div>

  <%--Location Selection--%>
  <div ng-show="state === states.SELECTING_DESTINATION">
    <div class="content-container">
      <div class="content-info">
        <form name="selectDestinationForm" novalidate>
          <h4 style="display: inline-block;">Please select a destination: </h4>
          <input name="destination"
                 type="text"
                 ng-model="destinationCode"
                 ui-autocomplete="getLocationAutocompleteOptions()"
                 destination-validator
                 ng-model-options="{debounce: 300}"
                 style="width: 80px;"/>
          <input type="button" value="Confirm" class="submit-button"
                 ng-disabled="selectDestinationForm.destination.$error.destination"
                 ng-click="confirmDestination()">
          <div ng-show="selectDestinationForm.destination.$error.destination"
               class="warning-text">
            Invalid location
          </div>
        </form>
      </div>
    </div>
  </div>

  <%--Ordering--%>
  <div class="content-container" ng-show="state == states.SHOPPING">
    <dir-pagination-controls class="text-align-center" on-page-change="onPageChange()" pagination-id="item-pagination"
                             boundary-links="true" max-size="10"></dir-pagination-controls>
    <div class="grid grid-pad">
      <div class="col-3-12 text-align-center"
           dir-paginate="allowance in displayAllowances | itemsPerPage: paginate.itemsPerPage"
           current-page="paginate.currPage"
           pagination-id="item-pagination">
        <img ng-src="${ctxPath}/assets/img/supply/{{allowance.item.id}}.png"
             err-src="${ctxPath}/assets/img/supply/no_photo_available.png"
             class="supply-item-image">
        <div>
          <p class="dark-gray bold" style="height: 40px;">{{allowance.item.description}}</p>
          <p class="dark-gray">{{allowance.item.standardQuantity}}/Pack</p>
        </div>
        <div style="">
          <label class="custom-select">
            <select ng-model="allowance.selectedQuantity"
                    ng-options="qty for qty in getAllowedQuantities(allowance)">
            </select>
          </label>
          <input class="submit-button add-to-cart-btn" ng-click="addToCart(allowance)"
                 type="button" value="Add to Cart">
        </div>
        <div ng-class="{'visibility-hidden': !isInCart(allowance.item)}" class="green padding-top-5 bold">
          &#x2713; Added to cart.
        </div>
      </div>
    </div>
    <dir-pagination-controls class="text-align-center" pagination-id="item-pagination"
                             boundary-links="true" max-size="10"></dir-pagination-controls>

    <div modal-container>
      <div over-allowed-quantity-modal ng-if="isOpen('over-allowed-quantity-modal')">

      </div>
      <div item-special-request-modal ng-if="isOpen('item-special-request-modal')">

      </div>
    </div>
  </div>
</div>
