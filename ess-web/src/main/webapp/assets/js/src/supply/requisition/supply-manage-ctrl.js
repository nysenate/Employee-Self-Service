essSupply = angular.module('essSupply').controller('SupplyManageController', ['$scope', 'modals', 'SupplyInventoryService', supplyManageController]);

function supplyManageController($scope, modals, SupplyInventoryService) {

    function SupplyOrder(id, locCode, locType, dateTime, purchaser, items) {
        this.id = id;
        this.locCode = locCode;
        this.locType = locType;
        this.dateTime = dateTime;
        this.purchaser = purchaser;
        this.items = items;
    }

    function Item(productId, quantity) {
        this.id = productId;
        this.quantity = quantity;
    }

    $scope.pendingOrders = [];

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
            console.log(item.quantity + " : " +SupplyInventoryService.getProductById(item.id).warnQuantity);
            if (item.quantity >= SupplyInventoryService.getProductById(item.id).warnQuantity) {
                highlight = true;
            }
        });
        return highlight;
    };

    $scope.init = function() {
        $scope.pendingOrders.push(new SupplyOrder(10, 'D56004', 'W', moment("2015-11-17 07:54"), 'CASEIRAS',
            [new Item(1, 1), new Item(4, 1), new Item(9, 2)]));
        $scope.pendingOrders.push(new SupplyOrder(11, 'CM330', 'W', moment("2015-11-17 09:23"), 'SMITH',
            [new Item(7, 4), new Item(6, 4), new Item(8, 4)]))
    };

    $scope.init();
}