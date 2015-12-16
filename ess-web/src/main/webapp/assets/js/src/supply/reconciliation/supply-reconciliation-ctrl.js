essSupply = angular.module('essSupply').controller('SupplyReconciliationController',
['$scope', 'SupplyInventoryService', 'SupplyGetTodaysCompletedOrdersApi', supplyReconciliationController]);

function supplyReconciliationController($scope, inventoryService, getTodaysCompletedOrdersApi) {

    $scope.reconcilableItems = [];

    function initItems() {
        getTodaysCompletedOrdersApi.get(function(response) {
            var orders = response.result;
            angular.forEach(orders, function(order) {
                angular.forEach(order.items, function(item) {
                    var search = $.grep($scope.reconcilableItems, function(i){ return i.id === item.itemId; });
                    if (search.length === 0) {
                        $scope.reconcilableItems.push(inventoryService.getItemById(item.itemId));
                    }
                })
            });
        }, function(response) {

        });
    }

    $scope.init = function() {
        initItems();
    };

    $scope.init();
}
