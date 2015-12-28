essSupply = angular.module('essSupply').controller('SupplyReconciliationController',
['$scope', 'SupplyInventoryService', 'SupplyGetTodaysCompletedOrdersApi', supplyReconciliationController]);

function supplyReconciliationController($scope, inventoryService, getTodaysCompletedOrdersApi) {

    $scope.selectedItem = null;
    $scope.reconcilableItems = [];
    /** Maps item id's to ReconciliableItem objects. */
    $scope.reconcilableItemMap= {};

    function initItems() {
        getTodaysCompletedOrdersApi.get(function(response) {
            var orders = response.result;
            angular.forEach(orders, function(order) {
                angular.forEach(order.items, function(item) {
                    if ($scope.reconcilableItemMap.hasOwnProperty(item.itemId)) {
                        $scope.reconcilableItemMap[item.itemId].push(order);
                    }
                    else {
                        $scope.reconcilableItemMap[item.itemId] = [];
                        $scope.reconcilableItemMap[item.itemId].push(order);
                        $scope.reconcilableItems.push(inventoryService.getItemById(item.itemId));
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

    $scope.getOrdersForItem = function(item) {
        return $scope.reconcilableItemMap[item.id];
    };

    $scope.getOrderedQuantity = function(order, item) {
        for(var i = 0; i < order.items.length; i++) {
            if (order.items[i].itemId === item.id) {
                return order.items[i].quantity;
            }
        }
    };

    $scope.init = function() {
        initItems();
    };

    $scope.init();

}
