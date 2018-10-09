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

    <div id="reconciliation-error-messages" class="text-align-center padding-10">
      <div ess-notification ng-show="reconciliationStatus.attempted == true && !inventory.isComplete()"
           level="error"
           title="Missing item quantities"
           message="To reconcile, you must enter a quantity for all items on both pages.">
      </div>
    </div>

    <div style="display:inline-block; width:100%">
      <ul class="reconciliation-tab-links">
        <li ng-class="{'active-reconciliation-tab': currentPage === 1}"><a href="#" ng-click="setCurrentPage(1)">Page One</a></li>
        <li ng-class="{'active-reconciliation-tab': currentPage === 2}"><a href="#" ng-click="setCurrentPage(2)">Page Two</a></li>
      </ul>

      <a id="printPage" class="no-print" style="margin: 10px; float: right" ng-click="print()">Print</a>
      <input class="submit-button" style="float: right;" type="button" value="Reconcile" ng-click="reconcile()">

    </div>

    <%--Header--%>
    <div class="supply-div-table large-print-font-size">
      <div class="supply-div-table-header">
        <div class="col-2-12">
          Commodity Code
        </div>
        <div class="col-8-12">
          Item
        </div>
        <div class="col-2-12">
          Quantity On Hand
        </div>
      </div>
      <%--Item rows--%>
      <div class="supply-div-table-body print-gray-bottom-border"
           ng-repeat="item in reconcilableSearch.items | filter : {'reconciliationPage' : currentPage}" >

        <div class="supply-div-table-row"
             ng-class="{'supply-highlight-row': isItemSelected(item), 'warn-important': isReconciliationError(item)}"
             ng-class-even="'dark-background'">

          <div class="col-2-12" ng-click="setSelected(item)">
            {{item.commodityCode}}

          </div>
          <div class="col-8-12" style="overflow: hidden;" ng-click="setSelected(item)">
            {{item.description}}
          </div>
          <div class="col-2-12" >
            <input type="number" style="width: 10em" ng-model="inventory.itemQuantities[item.id]" placeholder="Quantity"
            ng-class="{'warn-important': reconciliationStatus.attempted === true && inventory.itemQuantities[item.id] === null}">
          </div>
        </div>

        <%--Details--%>
        <div class="supply-sub-table"
             ng-show="isItemSelected(item)">
          <%--Detail header--%>
          <table class="ess-table supply-listing-table">
            <thead>
            <tr>
              <th>Id</th>
              <th>Location</th>
              <th>Quantity</th>
              <th>Issued By</th>
              <th>Approved Date</th>
            </tr>
            </thead>
            <tbody>
            <tr ng-repeat="shipment in getShipmentsWithItem(item)"
                ng-click="viewShipment(shipment)">
              <td>{{shipment.requisitionId}}</td>
              <td>{{shipment.destination.locId}}</td>
              <td>{{getOrderedQuantity(shipment, item)}}</td>
              <td>{{shipment.issuer.lastName}}</td>
              <td>{{shipment.approvedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>

    </div>
  </div>
  <div modal-container>
    <modal modal-id="reconciliation-success">
      <div confirm-modal title="Successful reconciliation"
           resolve-button="Ok"
           confirm-class="approve-button">
      </div>
    </modal>

    <modal modal-id="reconciliation-error">
      <div confirm-modal title="Errors occurred in reconciliation"
           confirm-message="One or more of the quantities entered is incorrect. Errors will be highlighted red."
           resolve-button="Review Errors"
           confirm-class="approve-button">
      </div>
    </modal>
  </div>
</div>
