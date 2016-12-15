<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="SupplyOrderController">
  <div class="supply-order-hero inline-block width-100">
    <h2 class="requisition-title">Requisition Form</h2>
    <a href="${ctxPath}/supply/order/cart">
      <cart-summary class="cart-widget"></cart-summary>
    </a>
  </div>

  <div loader-indicator class="loader" ng-show="state.isLoading()"></div>

  <div class="content-container content-controls"
       ng-show="state.isSelectingDestination()">
    <%--Location Selection--%>
    <div class="content-info">
      <form name="selectDestinationForm" novalidate>
        <h4 style="display: inline-block;">Please select a destination: </h4>
        <input name="destination"
               ng-model="destinationCode"
               ui-autocomplete="getLocationAutocompleteOptions()"
               destination-validator
               ng-model-options="{debounce: 300}"
               style="width: 80px;"
               capitalize/>
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

  <%-- Ordering/Shopping --%>
  <div ng-show="state.isShopping()">
    <div class="content-container content-controls">
      <%--Search--%>
      <div class="padding-10" style="display: flex; justify-content: space-between;">
        <div style="display: inline-block;">
          <span class="supply-text">Destination: &nbsp;&nbsp; <a ng-click="resetDestination()">[change]</a></span>
          <div>
            {{destinationCode}} ({{destinationDescription}})
          </div>
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
          Sort By: <select ng-init="sortBy = displaySorting[0]" ng-model="sortBy" ng-change="search()"
                           ng-options="o as o for o in displaySorting" style="width: 100px;"></select>
        </div>
      </div>
      <dir-pagination-controls class="text-align-center" on-page-change="onPageChange()"
                               pagination-id="item-pagination"
                               boundary-links="true" max-size="10"></dir-pagination-controls>
    </div>

    <div class="content-container"
         ng-show="displayedLineItems.length === 0">
      <div class="content-info">
        <h2 class="dark-gray">No results were found.
          <p>Enter new search criteria or click
            <button class="link-button" ng-click="resetAllFilters()">here</button>
            to clear your filters.
        </h2>
      </div>
    </div>

    <div class="grid">
      <div class="col-3-12 text-align-center"
           dir-paginate="lineItem in displayedLineItems | itemsPerPage: paginate.itemsPerPage"
           current-page="paginate.currPage"
           pagination-id="item-pagination">
        <div class="content-container"
             ng-class="{'supply-special-item': lineItem.item.visibility === 'SPECIAL'}">
          <div class="padding-top-5" style="overflow: hidden;position: relative">
            <div ng-class="{'corner-ribbon': lineItem.item.visibility === 'SPECIAL'}"
                 ng-hide="lineItem.item.visibility != 'SPECIAL'"><span>Special</span></div>

            <img class="supply-item-image"
                 ng-src="${imageUrl}/{{lineItem.item.commodityCode}}.jpg"
                 err-src="${ctxPath}/assets/img/supply/no_photo_available.png">
            <p class="dark-gray margin-5 bold" style="height: 40px; overflow: hidden;">
              {{lineItem.item.description}}
            </p>
            <supply-quantity-selector line-item="lineItem">
            </supply-quantity-selector>
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
    <modal modal-id="special-order-item-modal">
      <div special-order-item-modal></div>
    </modal>
  </div>
</div>
