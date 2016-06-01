<div ng-controller="SupplyReconciliationController">
  <div class="supply-order-hero">
    <h2>Reconciliation</h2>
  </div>

  <div loader-indicator class="loader" ng-show="!reconcilableSearch.response.$resolved"></div>

  <div class="content-container" ng-show="reconcilableSearch.response.$resolved && reconcilableSearch.items.length == 0">
    <div class="content-info">
      <h2 class="dark-gray">Reconciliation Not Required</h2>
    </div>
  </div>

  <div class="content-container" ng-show="reconcilableSearch.response.$resolved && reconcilableSearch.items.length > 0">
    <p class="content-info">
      Items shipped today.
    </p>

    <%--Header--%>
    <div class="large-print-font-size">
      <div class="grid expandable-div-header">
        <div class="col-3-12">
          Commodity Code
        </div>
        <div class="col-6-12">
          Item
        </div>
        <div class="col-3-12">
          Quantity On Hand
        </div>
      </div>
      <%--Item rows--%>
      <div ng-repeat="item in reconcilableSearch.items">
        <div class="grid expandable-div-rows" ng-class-even="'expandable-dark-background'" ng-click="setSelected(item)">
          <div class="col-3-12">
            {{item.commodityCode}}
          </div>
          <div class="col-6-12" style="overflow: hidden;">
            {{item.description}}
          </div>
          <div class="col-3-12">
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
               ng-repeat="shipment in getShipmentsWithItem(item)" ng-click="viewShipment(shipment)">
            <div class="col-3-12">
              {{shipment.activeVersion.destination.locId}}
            </div>
            <div class="col-3-12">
              {{getOrderedQuantity(shipment, item)}}
            </div>
            <div class="col-3-12">
              {{shipment.activeVersion.issuer.lastName}}
            </div>
            <div class="col-3-12">
              {{shipment.completedDateTime | date:'MM/dd/yyyy h:mm a'}}
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</div>
