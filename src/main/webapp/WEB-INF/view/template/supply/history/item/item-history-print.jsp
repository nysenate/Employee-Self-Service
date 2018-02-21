<%--Printable version of item-history.jsp--%>
<div ng-controller="SupplyItemHistoryPrintCtrl">
  <div style="background: white;">
    <div class="supply-simple-container margin-bottom-20">
      <h3 class="supply-simple-item" style="width: 30%;">
        Report parameters:
      </h3>
      <div class="supply-simple-item" style="width:70%;">
        <div class="supply-simple-container">
          <h3 class="supply-simple-item" style="width: 50%;">
            Item: {{commodityCode}}
          </h3>
          <h3 class="supply-simple-item" style="width: 50%;">
            Location: {{params.locId}}
          </h3>
          <h3 class="supply-simple-item" style="width: 50%; margin-top: 0px;">
            From: {{params.from | date:'MM/dd/yyyy'}}
          </h3>
          <h3 class="supply-simple-item" style="width: 50%; margin-top: 0px;">
            To: {{params.to | date:'MM/dd/yyyy'}}
          </h3>
        </div>
      </div>
    </div>

    <div class="supply-item-print-header-row">
      <div class="supply-item-print-item" style="width: 100px;">
        Location
      </div>
      <div class="supply-item-print-item" style="width: 100px;">
        Commodity
      </div>
      <div class="supply-item-print-item" style="width: 115px;">
        Ordered By
      </div>
      <div class="supply-item-print-item" style="width: 160px;">
        Completed Date
      </div>
      <div class="supply-item-print-item" style="width: 115px;">
        Issued By
      </div>
      <div class="supply-item-print-item" style="width: 80px;">
        Quantity
      </div>
    </div>
    <div class="supply-item-print-row"
         ng-repeat="value in itemHistories">
      <div class="supply-item-print-item" style="width: 100px;">
        {{value.locationCode}}
      </div>
      <div class="supply-item-print-item" style="width: 100px;">
        {{value.commodityCode}}
      </div>

      <div class="supply-item-print-item" style="width: 470px;">
        <div class="supply-item-print-inner-row"
             ng-repeat="req in value.requisitions">
          <div class="supply-item-print-item" style="width: 115px;">
            {{req.customer.lastName}}
          </div>
          <div class="supply-item-print-item" style="width: 160px;">
            {{req.completedDateTime | date:'MM/dd/yyyy h:mm a'}}
          </div>
          <div class="supply-item-print-item" style="width: 115px;">
            {{req.issuer.lastName}}
          </div>
          <div class="supply-item-print-item" style="width: 80px;">
            {{getItemQuantity(req, value.commodityCode)}}
          </div>
        </div>

        <div class="supply-item-print-inner-row">
          <div class="supply-item-print-item" style="width: 275px;">
            &nbsp
          </div>
          <div class="supply-item-print-item bold" style="width: 115px;">
            Total:
          </div>
          <div class="supply-item-print-item bold" style="width: 80px;">
            {{value.quantity}}
          </div>
        </div>
      </div>
    </div>
    <div class="supply-item-print-total-row">
      <div class="supply-item-print-item" style="width: 475px;">
        &nbsp
      </div>
      <div class="supply-item-print-item bold" style="width: 115px; padding-left: 10px; border: black solid; border-width: 1px 0 1px 1px;">
        Total:
      </div>
      <div class="supply-item-print-item bold" style="width: 80px; border: black solid; border-width: 1px 1px 1px 0;">
        {{getTotalQuantity()}}
      </div>
    </div>
  </div>
</div>
