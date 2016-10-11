<div ng-controller="SupplyViewController">

  <%--Non printing requisition view. See bottom half for the version used when printing.--%>
  <div class="no-print">
    <div class="supply-order-hero">
      <h2>Requisition Order: {{selectedVersion.requisitionId}}</h2>
    </div>

    <div loader-indicator class="loader"
         ng-show="!requisitionResponse.$resolved"></div>

    <%--Version selection--%>
    <div ng-show="requisitionResponse.$resolved">
      <div class="content-container">
        <div class="content-info">
          <label>Select Version:</label>
          <select ng-model="selectedVersion"
                  ng-options="version.name for version in requisitionHistory.versions">
          </select>
          <a class="float-right" style="padding: 5px 20px 0px 0px;" href="javascript:if(window.print)window.print()">
            Print Page
          </a>
        </div>
      </div>

      <%--General information--%>
      <div class="content-container">
        <div class="content-info">
          <div class="grid padding-10">
            <div class="col-4-12"
                 ns-popover ns-popover-template="customer-details" ns-popover-theme="ns-popover-tooltip-theme"
                 ns-popover-placement="bottom"
                 ns-popover-trigger="mouseenter" ns-popover-timeout=".2">
              <b>Requested By:</b> {{selectedVersion.customer.fullName}}
              <script type="text/ng-template" id="customer-details">
                <div class="triangle"></div>
                <div class="ns-popover-tooltip">
                  <ul>
                    <li><b>Phone Number:</b> {{selectedVersion.customer.workPhone}}</li>
                    <li><b>Email:</b> {{selectedVersion.customer.email}}</li>
                  </ul>
                </div>
              </script>
            </div>
            <div class="col-4-12"
                 ns-popover ns-popover-template="location-details" ns-popover-theme="ns-popover-tooltip-theme"
                 ns-popover-placement="bottom"
                 ns-popover-trigger="mouseenter" ns-popover-timeout="0.2">
              <b>Requesting Office:</b> {{selectedVersion.destination.locId}}
              <script type="text/ng-template" id="location-details">
                <div class="triangle"></div>
                <div class="ns-popover-tooltip">
                  <ul>
                    <li><b>Office Name:</b> {{selectedVersion.destination.respCenterHead.name}}</li>
                    <li><b>Address:</b> {{selectedVersion.destination.address.addr1}}
                      {{selectedVersion.destination.address.city}} {{selectedVersion.destination.address.state}}
                      {{selectedVersion.destination.address.zip5}}
                    </li>
                  </ul>
                </div>
              </script>
            </div>
            <div class="col-4-12">
              <b>Requested Date:</b> {{selectedVersion.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}
            </div>
          </div>
          <div class="grid padding-10">
            <div class="col-4-12">
              <b>Status:</b> {{selectedVersion.status}}
            </div>
            <div class="col-4-12">
              <b ng-if="selectedVersion.status === 'PENDING' || selectedVersion.status === 'PROCESSING'">Issuer: </b>
              <b ng-if="selectedVersion.status !== 'PENDING' && selectedVersion.status !== 'PROCESSING'">Issued By:</b>
              {{selectedVersion.issuer.lastName}}
            </div>
            <div class="col-4-12">
              <b>Issued Date:</b>
              <span ng-show="displayIssuedDate(selectedVersion)">
                {{selectedVersion.completedDateTime | date:'MM/dd/yyyy h:mm a'}}
              </span>
            </div>
          </div>
          <div class="grid padding-10">
            <div class="col-4-12">
              <b>Modified By:</b> {{selectedVersion.modifiedBy.lastName}}
            </div>
          </div>
        </div>
      </div>


      <%--Notes--%>
      <div class="content-container" ng-show="selectedVersion.note || selectedVersion.specialInstructions">
        <div class="content-info">
          <div class="grid padding-10" ng-show="selectedVersion.note">
            <div class="col-4-12 bold">
              Supply Note:
            </div>
            <div class="col-8-12">
              {{selectedVersion.note}}
            </div>
          </div>
          <div ng-show="selectedVersion.note && selectedVersion.specialInstructions"
               style="border-bottom: black 1px solid;"></div>
          <div class="grid padding-10" ng-show="selectedVersion.specialInstructions">
            <div class="col-4-12 bold">
              Special Instructions:
            </div>
            <div class="col-8-12">
              {{selectedVersion.specialInstructions}}
            </div>
          </div>
        </div>
      </div>

      <%--Order Items--%>
      <div class="content-container">
        <div class="padding-10">
          <table class="ess-table supply-listing-table">
            <thead>
            <tr>
              <th>Commodity Code</th>
              <th>Item</th>
              <th>Quantity</th>
            </tr>
            </thead>
            <tbody>
            <tr ng-repeat="lineItem in sortSelectedVersionLineItems() | orderBy: 'item.commodityCode'">
              <td>{{lineItem.item.commodityCode}}</td>
              <td>{{lineItem.item.description}}</td>
              <td>{{lineItem.quantity}}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

  <%----------------------------------------------%>
  <%--     Printing version of requisition      --%>
  <%----------------------------------------------%>

  <%--The following is a custom representation of the above page to be displayed when printing.--%>
  <div class="print-only">
    <style type="text/css" media="print"></style>
    <div class="supply-order-hero">
      <h2>Requisition Order: {{selectedVersion.requisitionId}}</h2>
    </div>

    <%--General information--%>
    <div class="content-container large-print-font-size">

      <div class="content-info">
        <div class="grid padding-10">
          <b>Requesting Office:</b>
          <span style="padding-left: 10px;">{{selectedVersion.destination.locId}} </span>
          <span style="padding-left: 10px;">{{selectedVersion.destination.respCenterHead.shortName}}</span>
          <span style="padding-left: 10px;">{{selectedVersion.destination.address.addr1}}{{selectedVersion.destination.address.city}} {{selectedVersion.destination.address.state}}
            {{selectedVersion.destination.address.zip5}}
           </span>
        </div>
      </div>

      <div class="content-info">
        <div class="grid padding-10">
          <div class="col-4-12">
            <b>Requested By:</b> {{selectedVersion.customer.fullName}}
          </div>
          <div class="col-4-12">
            <b>Requested Date:</b> {{selectedVersion.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}
          </div>
          <div class="col-4-12">
            <b>Status:</b> {{selectedVersion.status}}
          </div>
        </div>
      </div>

      <div class="content-info">
        <div class="grid padding-10">
          <div class="col-4-12">
            <b ng-if="selectedVersion.status === 'PENDING' || selectedVersion.status === 'PROCESSING'">Issuer: </b>
            <b ng-if="selectedVersion.status !== 'PENDING' && selectedVersion.status !== 'PROCESSING'">Issued By:</b>
            {{selectedVersion.issuer.lastName}}
          </div>
          <div class="col-4-12">
            <b>Issued Date:</b>
            <span ng-show="displayIssuedDate(selectedVersion)">
              {{selectedVersion.completedDateTime | date:'MM/dd/yyyy h:mm a'}}
            </span>
          </div>
          <div class="col-4-12">
            <b>Modified By:</b> {{selectedVersion.modifiedBy.lastName}}
          </div>
        </div>
      </div>
    </div>

    <%--Notes--%>
    <div class="content-container large-print-font-size"
         ng-show="selectedVersion.note || selectedVersion.specialInstructions">
      <div class="content-info">
        <div class="grid padding-10" ng-show="selectedVersion.note">
          <div class="col-2-12 bold">
            Supply Note:
          </div>
          <div class="col-10-12">
            {{selectedVersion.note}}
          </div>
        </div>
        <div class="grid padding-10" ng-show="selectedVersion.specialInstructions">
          <div class="col-4-12 bold">
            Special Instructions:
          </div>
          <div class="col-8-12">
            {{selectedVersion.specialInstructions}}
          </div>
        </div>
      </div>
    </div>

    <%--Order Items--%>
    <div class="content-container large-print-font-size padding-top-10">
      <div class="padding-10">
        <table class="ess-table supply-listing-table">
          <thead>
          <tr>
            <th>Commodity Code</th>
            <th>Item</th>
            <th>Quantity</th>
          </tr>
          </thead>
          <tbody>
          <tr ng-repeat="lineItem in sortSelectedVersionLineItems() | orderBy: 'item.commodityCode'">
            <td>{{lineItem.item.commodityCode}}</td>
            <td>{{lineItem.item.description}}</td>
            <td>{{lineItem.quantity}}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="large-print-font-size" style="margin-top: 60px; padding: 20px;">
      Received By: ________________________________
    </div>
  </div>
</div>
