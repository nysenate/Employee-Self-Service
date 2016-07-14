essSupply = angular.module('essSupply').controller('SupplyReconciliationController',
['$scope', 'SupplyRequisitionApi', 'LocationService', supplyReconciliationController]);

function supplyReconciliationController($scope, requisitionApi, locationService) {

    /** If a particular item is selected, displays information on all orders containing that item. */
    $scope.selectedItem = null;
    
    $scope.reconcilableSearch = {
        matches: [],
        items: [],
        response: {},
        error: false
    };
    
    /** Map of unique item id's to array of all shipments containing that item objects. */
    $scope.reconcilableItemMap= {};

    function initItems() {
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
                        $scope.reconcilableSearch.items.push(lineItem.item);
                    }
                })
            });
        }, function(response) {
            $scope.reconcilableSearch.matches = [];
            $scope.reconcilableSearch.items = [];
            $scope.reconcilableSearch.error = true;
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
        locationService.go("/supply/requisition/requisition-view", false, "requisition=" + shipment.id);
    };

    $scope.init = function() {
        initItems();
    };

    $scope.init();

}
