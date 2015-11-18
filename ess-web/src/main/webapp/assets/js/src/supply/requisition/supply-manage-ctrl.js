essSupply = angular.module('essSupply').controller('SupplyManageController', ['$scope', 'modals', 'SupplyInventoryService',
    'SupplyOrderService', supplyManageController]);

function supplyManageController($scope, modals, supplyInventoryService, supplyOrderService) {

    $scope.pendingOrders = function() {
        return supplyOrderService.getPendingOrders();
    };

    $scope.getOrderQuantity = function(supplyOrder) {
        var size = 0;
        angular.forEach(supplyOrder.items, function(item) {
            size += item.quantity;
        });
        return size;
    };

    $scope.highlightOrder = function(order) {
        var highlight = false;
        angular.forEach(order.items, function(item) {
            if (item.quantity >= supplyInventoryService.getProductById(item.id).warnQuantity) {
                highlight = true;
            }
        });
        return highlight;
    };
}