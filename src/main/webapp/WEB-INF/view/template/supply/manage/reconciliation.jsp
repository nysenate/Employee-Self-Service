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
    <div style="display:inline-block; width:100%">
      <ul class="reconciliation-tab-links">
        <li ng-class="{'active-reconciliation-tab': currentPage === 1}"><a href="#" ng-click="setCurrentPage(1)">Page One</a></li>
        <li ng-class="{'active-reconciliation-tab': currentPage === 2}"><a href="#" ng-click="setCurrentPage(2)">Page Two</a></li>
      </ul>
      <a id="printPage" class="no-print" style="margin: 10px; float: right" ng-click="print()">Print</a>
    </div>
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
      <div ng-repeat="item in reconcilableSearch.items | filter : {'reconciliationPage' : currentPage}">
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
            <div class="col-1-12">
              Id
            </div>
            <div class="col-3-12">
              Location
            </div>
            <div class="col-2-12">
              Quantity
            </div>
            <div class="col-3-12">
              Issued By
            </div>
            <div class="col-3-12">
              Approved Date
            </div>
          </div>
          <%--Detail rows--%>
          <div class="grid expandable-div-rows" ng-class-even="'expandable-dark-background'"
               ng-repeat="shipment in getShipmentsWithItem(item)" ng-click="viewShipment(shipment)">
            <div class="col-1-12">
              {{shipment.requisitionId}}
            </div>
            <div class="col-3-12">
              {{shipment.destination.locId}}
            </div>
            <div class="col-2-12">
              {{getOrderedQuantity(shipment, item)}}
            </div>
            <div class="col-3-12">
              {{shipment.issuer.lastName}}
            </div>
            <div class="col-3-12">
              {{shipment.approvedDateTime | date:'MM/dd/yyyy h:mm a'}}
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
  <div modal-container></div>
</div>
