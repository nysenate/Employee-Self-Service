<div ng-controller="SupplyReconciliationController">
  <div class="supply-order-hero">
    <h2>Reconciliation</h2>
  </div>

  <div class="content-container">
    <p class="content-info">
      All items shipped today.
    </p>

    <%--Header--%>
    <div class="">
      <div class="grid expandable-div-header">
        <div class="col-4-12">
          Commodity Code
        </div>
        <div class="col-4-12">
          Item Name
        </div>
        <div class="col-4-12">
          Quantity On Hand
        </div>
      </div>
      <%--Item rows--%>
      <div ng-repeat="item in reconcilableItems">
        <div class="grid expandable-div-rows" ng-class-even="'expandable-dark-background'" ng-click="setSelected(item)">
          <div class="col-4-12">
            {{item.commodityCode}}
          </div>
          <div class="col-4-12">
            {{item.name}}
          </div>
          <div class="col-4-12">
            &nbsp;
          </div>
        </div>

        <%--Details--%>
        <div ng-show="isItemSelected(item)" style="padding-left: 40px; padding-top: 0px; padding-bottom: 20px;">
          <%--Detail header--%>
          <div class="grid expandable-div-header">
            <div class="col-3-12">
              Location
            </div>
            <div class="col-3-12">
              Quantity
            </div>
            <div class="col-3-12">
              Issued By
            </div>
            <div class="col-3-12">
              Completed Date
            </div>
          </div>
          <%--Detail rows--%>
          <div class="grid expandable-div-rows" ng-class-even="'expandable-dark-background'"
               ng-repeat="order in getOrdersForItem(item)" ng-click="viewOrder(order)">
            <div class="col-3-12">
              {{order.location.code + '-' + order.location.locationTypeCode}}
            </div>
            <div class="col-3-12">
              {{getOrderedQuantity(order, item)}}
            </div>
            <div class="col-3-12">
              {{order.issuingEmployee.lastName}}
            </div>
            <div class="col-3-12">
              {{order.completedDateTime | date:'MM/dd/yyyy h:mm a'}}
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</div>
