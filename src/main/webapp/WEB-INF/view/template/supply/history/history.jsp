<div ng-controller="SupplyHistoryController">
    <div class="content-container">
        <div class="supply-order-hero">
            <h2>Requisition History</h2>
        </div>

        <div>
            <p class="content-info supply-text large-print-font-size" style="margin-bottom: 0px;">Show
                Requisitions ordered during the following date range.</p>
            <div class="grid text-align-center">
                <div class="col-6-12 padding-10">
                    <label class="bold">Location:</label>
                    <select ng-model="selectedLocation" ng-options="location for location in locations"
                            ng-required="true"
                            ng-change="onFilterChange()"></select>
                </div>
                <div class="col-6-12 padding-10">
                    <label class="bold">Issuer:</label>
                    <select ng-model="selectedIssuer" ng-options="emp for emp in issuers"
                            ng-change="onFilterChange()"></select>
                </div>
                <div class="col-6-12" style="padding: 0 10px 10px 10px;">
                    <label class="bold">From:</label>
                    <input datepicker ng-model="filter.date.from" ng-change="onFilterChange()"/>
                </div>
                <div class="col-6-12" style="padding: 0 10px 10px 10px;">
                    <label class="bold">To:</label>
                    <input datepicker ng-model="filter.date.to" ng-change="onFilterChange()"/>
                </div>
            </div>
        </div>
    </div>

    <div class="content-container" ng-show="shipments.length == 0">
        <div class="content-info">
            <h2 class="dark-gray">No History</h2>
        </div>
    </div>

    <div class="content-container large-print-font-size" ng-show="shipments.length > 0">
        <div>
            <dir-pagination-controls class="text-align-center" on-page-change="onPageChange()"
                                     pagination-id="order-history-pagination"
                                     boundary-links="true" max-size="10"></dir-pagination-controls>
        </div>
        <div class="padding-10">
            <table class="ess-table supply-listing-table">
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Location</th>
                    <th>Ordered By</th>
                    <th>Quantity</th>
                    <th>Order Date</th>
                    <th>Complete Date</th>
                    <th>Issued By</th>
                </tr>
                </thead>
                <tbody>
                <tr dir-paginate="shipment in shipments | itemsPerPage : paginate.itemsPerPage"
                    current-page="paginate.currPage"
                    pagination-id="order-history-pagination"
                    total-items="paginate.totalItems"
                    ng-click="viewRequisition(shipment)">
                    <td>{{shipment.requisitionId}}</td>
                    <td>{{shipment.destination.locId}}</td>
                    <td>{{shipment.customer.lastName}}</td>
                    <td>{{getOrderQuantity(shipment)}}</td>
                    <td>{{shipment.orderedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
                    <td>{{shipment.completedDateTime | date:'MM/dd/yyyy h:mm a'}}</td>
                    <td>{{shipment.issuer.lastName}}</td>
                </tr>
                </tbody>
            </table>
        </div>
        <div>
            <dir-pagination-controls class="text-align-center" pagination-id="order-history-pagination"
                                     boundary-links="true" max-size="10"></dir-pagination-controls>
        </div>
    </div>
    <div>
    </div>
</div>