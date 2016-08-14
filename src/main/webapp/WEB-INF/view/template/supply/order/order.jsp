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
  <div ng-show="state === states.SHOPPING">
    <div class="content-container">
      <%--Search--%>
      <div class="padding-10" style="display: flex; justify-content: space-between;">
        <div style="display: inline-block;">
          <span class="supply-text">Destination: </span>{{destinationCode}}
        </div>
        <div style="display: inline-block;">
          <form>
            <input type="text"
                   ng-model="filter.searchTerm">
            <input class="submit-button" ng-click="search()" type="submit" value="Search">
          </form>
        </div>
        <div style="display: inline-block;">
          <a ng-click="resetDestination()">Cancel Requisition</a>
        </div>
      </div>

      <dir-pagination-controls class="text-align-center" on-page-change="onPageChange()" pagination-id="item-pagination"
                               boundary-links="true" max-size="10"></dir-pagination-controls>
    </div>

    <div class="grid">
      <div class="col-3-12 text-align-center"
           dir-paginate="allowance in displayAllowances | itemsPerPage: paginate.itemsPerPage"
           current-page="paginate.currPage"
           pagination-id="item-pagination">
        <div class="content-container padding-10"
             ng-class="{'supply-special-item': allowance.visibility === 'SPECIAL'}">
          <img ng-src="${imageUrl}/{{allowance.item.commodityCode}}.jpg"
               err-src="${ctxPath}/assets/img/supply/no_photo_available.png"
               class="supply-item-image">
          <div>
            <p class="dark-gray bold" style="height: 40px;overflow: hidden;">{{allowance.item.description}}</p>
            <p class="dark-gray">{{allowance.item.standardQuantity}}/Pack</p>
          </div>
          <div style="">
            <label class="custom-select">
              <select ng-model="allowance.selectedQuantity"
                      ng-options="qty for qty in getAllowedQuantities(allowance.item)"
                      ng-change="quantityChanged(allowance)">
              </select>
            </label>
            <input class="submit-button add-to-cart-btn" ng-click="addToCart(allowance)"
                   type="button" value="Add to Cart" style="padding: 0px 5px;">
          </div>
          <div ng-class="{'visibility-hidden': !isInCart(allowance.item)}" class="green padding-top-5 bold">
            &#x2713; Added to cart.
          </div>
        </div>
      </div>
    </div>

    <div class="content-container">
      <dir-pagination-controls class="text-align-center" pagination-id="item-pagination"
                               boundary-links="true" max-size="10"></dir-pagination-controls>
    </div>

    <div modal-container>
      <div order-more-prompt-modal ng-if="isOpen('order-more-prompt-modal')"></div>
      <div order-custom-quantity-modal ng-if="isOpen('order-custom-quantity-modal')"></div>
      <div special-order-item-modal ng-if="isOpen('special-order-item-modal')"></div>
    </div>
  </div>
</div>
