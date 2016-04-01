essSupply = angular.module('essSupply').controller('SupplyReconciliationController',
['$scope', 'SupplyInventoryService', 'SupplyShipmentsApi', 'LocationService', supplyReconciliationController]);

function supplyReconciliationController($scope, inventoryService, shipmentsApi, locationService) {

    $scope.selectedItem = null;
    $scope.reconcilableItems = [];
    /** Map of unique item id's to array of all shipments containing that item objects. */
    $scope.reconcilableItemMap= {};

    function initItems() {
        // Get shipments completed today
        var params = {
            status: "COMPLETED",
            from: moment().startOf('day').format(),
            to: moment().format()
        };
        shipmentsApi.get(params, function(response) {
            var shipments = response.result;
            console.log(shipments);
            angular.forEach(shipments, function(shipment) {
                angular.forEach(shipment.order.activeVersion.lineItems, function(lineItem) {
                    if ($scope.reconcilableItemMap.hasOwnProperty(lineItem.item.id)) {
                        $scope.reconcilableItemMap[lineItem.item.id].push(shipment);
                    }
                    else {
                        $scope.reconcilableItemMap[lineItem.item.id] = [];
                        $scope.reconcilableItemMap[lineItem.item.id].push(shipment);
                        $scope.reconcilableItems.push(lineItem.item);
                    }
                })
            });
        }, function(response) {

        });
    }

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
        var lineItems = shipment.order.activeVersion.lineItems;
        for(var i = 0; i < lineItems.length; i++) {
            if (lineItems[i].item.id === item.id) {
                return lineItems[i].quantity;
            }
        }
    };

    $scope.viewShipment = function(shipment){
        locationService.go("/supply/requisition/view", false, "order=" + shipment.order.id);
    };

    $scope.init = function() {
        initItems();
    };

    $scope.init();

}
