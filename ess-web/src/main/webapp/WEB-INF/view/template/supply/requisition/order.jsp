<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>

<div ng-controller="SupplyOrderController">
  <section class="left-nav" ess-navigation>
    <ess-component-nav:nav-header topicTitle="Categories" colorClass="blue-purple"/>
    <%--<h3 class="main-topic">Categories</h3>--%>
    <ul class="sub-topic-list" style="padding-top: 5px">
      <li ng-repeat="cat in categories">
        <input type="checkbox" ng-model="cat.selected" ng-change="filterByCategories()">
        <label>{{cat.categoryName}}</label>
      </li>
    </ul>
  </section>
  <div class="view-animate-container">
    <div class="supply-order-hero" style="display: inline-block; width: 100%">
      <%--Padding in h2 is to offset being 'pushed' left by cart image... TODO definately a better way--%>
      <h2 style="display: inline-block; padding-left: 100px">Supply Requisition Form</h2>
      <a href="${ctxPath}/supply/cart/contents">
      <cart-summary style="display: inline-block; float: right;"></cart-summary>
      </a>
    </div>
    <div style="margin-top: 20px;" class="content-container">
      <ul style="list-style: none; padding-left: 0px;
      -webkit-columns: 3 180px; -moz-columns: 3 180px; columns: 3 180px;
      -webkit-column-gap: 0px; -moz-column-gap: 0px; column-gap: 0px;">
        <li ng-repeat="product in products" style="text-align: center; padding-bottom: 15px; padding-top: 15px; border-bottom: 1px solid #ddd;
        -webkit-column-break-inside: avoid; page-break-inside: avoid; break-inside: avoid; ">
          <img ng-src="{{product.img}}" style="height: 140px;">
          <div style="">
            <h2 class="dark-gray" style="font-weight: bold">{{product.name}}</h2>
            <p class="dark-gray">{{product.description}}</p>
            <p class="dark-gray bold">{{product.unitSize}}/Pack</p>
          </div>
          <div style="">
            <label class="custom-select">Qty:
              <select requisition-quantity-selector ng-model="quantity" ng-options="qty for qty in orderSizeRange(product)"></select>
            </label>
            <input ng-click="addToCart(product, quantity)" class="submit-button" type="button" value="Add to Cart">
          </div>
          <div ng-class="{'visibility-hidden': !isInCart(product)}" class="green padding-top-5 bold">
            &#x2713; Added to cart.
          </div>
        </li>
      </ul>
    </div>
  </div>
</div>
