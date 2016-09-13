essSupply = angular.module('essSupply').controller('SupplyReconciliationController',
    ['$scope', 'SupplyReconciliationApi', 'SupplyRequisitionApi', 'LocationService', '$window', '$timeout', supplyReconciliationController]);

function supplyReconciliationController($scope, supplyReconciliationApi, requisitionApi, locationService, $window, $timeout) {

    /** If a particular item is selected, displays information on all orders containing that item. */
    $scope.selectedItem = null;

    $scope.viewItems = [];
    $scope.reconcilableSearch = {
        matches: [],
        items: [],
        response: {},
        error: false
    };
    $scope.currentPage = 1;
    $scope.reconciliations = {};
    
    /** Map of unique item id's to array of all shipments containing that item objects. */
    $scope.reconcilableItemMap= {};


    /** Print*/
    $scope.print = function () {
        $timeout($window.print, 0);
    };

    function initItems() {
        //Get reconciliation page
        supplyReconciliationApi.get().$promise.then(function (response) {
            response.result.forEach(function (item) {
                $scope.reconciliations[item.item_category] = item.page;
            });

        // Get shipments completed today
        var params = {
            status: "APPROVED",
            from: moment().startOf('day').format(),
            to: moment().format(),
            dateField: "approved_date_time"
        };
        $scope.reconcilableSearch.response = requisitionApi.get(params, function(response) {
            $scope.reconcilableSearch.matches = response.result;
            $scope.reconcilableSearch.error = false;
            angular.forEach($scope.reconcilableSearch.matches, function(shipment) {
                angular.forEach(shipment.lineItems, function(lineItem) {
                    if ($scope.reconcilableItemMap.hasOwnProperty(lineItem.item.id)) {
                        $scope.reconcilableItemMap[lineItem.item.id].push(shipment);
                    }
                    else {
                        $scope.reconcilableItemMap[lineItem.item.id] = [];
                        $scope.reconcilableItemMap[lineItem.item.id].push(shipment);
                        lineItem.item["page"] = $scope.reconciliations[lineItem.item.category.name];
                        $scope.reconcilableSearch.items.push(lineItem.item);
                    }
                })
            });
        }, function(response) {
            $scope.reconcilableSearch.matches = [];
            $scope.reconcilableSearch.items = [];
            $scope.reconcilableSearch.error = true;
        });
        });
    }

    /**
     * Selecting an item will display a table containing information for all shipments that contain that item.
     * Clicking on an already selected item will hide the table.
     */
    $scope.setSelected = function(item) {
        // Clicking an expanded item should collapse it.
        if($scope.selectedItem == item) {
            $scope.selectedItem = null;
        }
        else {
            $scope.selectedItem = item;
        }
    };

    $scope.isItemSelected = function(item) {
        return $scope.selectedItem == item;
    };

    $scope.getShipmentsWithItem = function(item) {
        return $scope.reconcilableItemMap[item.id];
    };

    $scope.getOrderedQuantity = function(shipment, item) {
        var lineItems = shipment.lineItems;
        for(var i = 0; i < lineItems.length; i++) {
            if (lineItems[i].item.id === item.id) {
                return lineItems[i].quantity;
            }
        }
    };

    $scope.viewShipment = function(shipment){
        locationService.go("/supply/requisition/requisition-view", false, "requisition=" + shipment.requisitionId + "&fromPage=reconciliation");
    };

    $scope.init = function() {
        initItems();
    };

    $scope.setCurrentPage = function (e) {
        $("#page" + $scope.currentPage).css("text-decoration", "");
        $scope.currentPage = e;
        $("#page" + $scope.currentPage).css("text-decoration", "underline");
    };

    $scope.init();

}
