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
                 style="width: 80px;"
                 capitalize
          />
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
          <span class="supply-text">Destination:
            <div style="display: inline; font-size: small;">
            </div>
          </span>
        </div>
        <div style="display: inline-block;">
          <form>
            <input type="text"
                   ng-model="filter.searchTerm">
            <input class="submit-button" ng-click="search()" type="submit" value="Search">
            <input class="reset-button" ng-click="reset()" type="reset" value="Reset">
          </form>
        </div>
        <div style="display: inline-block;">
          Sort By: <select ng-init="sortBy = displaySorting[0]" ng-model="sortBy" ng-change="updateSort()"
                           ng-options="o as o for o in displaySorting" style="width: 100px;"></select>
        </div>
      </div>
        <div class="padding-10" style="margin-top: -30px;    margin-bottom: -10px;">
          {{destinationCode}} ({{destinationDescription}}) <a ng-click="resetDestination()">[edit]</a>
        </div>
        <dir-pagination-controls class="text-align-center" on-page-change="onPageChange()"
                                 pagination-id="item-pagination"
                                 boundary-links="true" max-size="10"></dir-pagination-controls>
    </div>

    <div class="grid">
      <div class="col-3-12 text-align-center"
           dir-paginate="allowance in displayAllowances | itemsPerPage: paginate.itemsPerPage"
           current-page="paginate.currPage"
           pagination-id="item-pagination">
        <div class="content-container"
             ng-class="{'supply-special-item': allowance.visibility === 'SPECIAL'}">
          <img class="supply-item-image"
               ng-src="${imageUrl}/{{allowance.item.commodityCode}}.jpg"
               err-src="${ctxPath}/assets/img/supply/no_photo_available.png">
          <p class="dark-gray margin-5 bold" style="height: 40px; overflow: hidden;">{{allowance.item.description}}</p>

          <div>
            <p class="dark-gray margin-0">{{allowance.item.standardQuantity}}/Pack</p>
            <div ng-show="!isInCart(allowance.item)">
              <input class="add-to-cart-btn" ng-click="addToCart(allowance)"
                     type="button" value="Add to Cart">
            </div>
            <div ng-show="isInCart(allowance.item)">
              <input class="qty-adjust-button" ng-click="decrementQuantity(allowance.item)"
                     type="button" value="-">
              <input order-quantity-validator
                     class="qty-input"
                     type="text"
                     ng-change="onUpdate()"
                     ng-model="SOMETHING"
                     min="0"
                     step="1">
              <input class="qty-adjust-button" ng-click="incrementQuantity(allowance.item)"
                     type="button" value="+">
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="content-container">
      <dir-pagination-controls class="text-align-center" pagination-id="item-pagination"
                               boundary-links="true" max-size="10"></dir-pagination-controls>
    </div>

  </div>
  <div modal-container>
    <modal modal-id="order-more-prompt-modal">
      <div order-more-prompt-modal></div>
    </modal>
    <modal modal-id="order-canceling-modal">
      <div order-canceling-modal></div>
    </modal>
    <modal modal-id="order-custom-quantity-modal">
      <div order-custom-quantity-modal></div>
    </modal>
    <modal modal-id="special-order-item-modal">
      <div special-order-item-modal></div>
    </modal>
  </div>
</div>
