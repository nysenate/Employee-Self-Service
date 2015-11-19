essSupply = angular.module('essSupply').controller('SupplyManageController', ['$scope', 'modals', 'SupplyInventoryService',
    'SupplyOrderService', supplyManageController]);

function supplyManageController($scope, modals, supplyInventoryService, supplyOrderService) {

    $scope.selected = null;

    $scope.order = supplyOrderService.getPendingOrders()[0];

    $scope.pendingOrders = function() {
        return supplyOrderService.getPendingOrders();
    };

    $scope.inprocessOrders = function() {
        return supplyOrderService.getInprocessOrders();
    };

    $scope.completedOrders = function() {
        return supplyOrderService.getTodaysCompletedOrders();
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
            if (item.quantity >= item.product.warnQuantity) {
                highlight = true;
            }
        });
        return highlight;
    };

    $scope.setSelected = function(order) {
        if ($scope.selected && order.id === $scope.selected.id) {
            $scope.selected = null;
        }
        else {
            $scope.selected = order;
        }
    };

    $scope.selectedOrder = function(order) {
        return $scope.selected && order.id === $scope.selected.id;
    };

    $scope.processOrder = function(order) {
        supplyOrderService.setOrderToInprocess(order.id, "CASEIRAS");
    };

    $scope.completeOrder = function(order) {
        supplyOrderService.completeOrder(order.id);
    }
}