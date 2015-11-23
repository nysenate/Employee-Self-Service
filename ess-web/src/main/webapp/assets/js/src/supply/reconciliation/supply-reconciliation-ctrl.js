essSupply = angular.module('essSupply').controller('SupplyReconciliationController',
['$scope', 'SupplyOrderService', supplyReconciliationController]);

function supplyReconciliationController($scope, supplyOrderService) {

    $scope.reconcilableItems = [];

    function initItems() {
        var orders = supplyOrderService.getTodaysCompletedOrders();
        angular.forEach(orders, function(order) {
            angular.forEach(order.items, function(item) {
                var search = $.grep($scope.reconcilableItems, function(i){ return i.id === item.product.id; });
                if (search.length === 0) {
                    $scope.reconcilableItems.push(item.product);
                }
            })
        });
    }

    $scope.init = function() {
        initItems();
    };

    $scope.init();
}
