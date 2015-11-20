essSupply = angular.module('essSupply').controller('SupplyHistoryController',
['$scope', 'SupplyOrderService', 'LocationService', supplyHistoryController]);

function supplyHistoryController($scope, supplyOrderService, locationService) {

    $scope.locations = [];
    $scope.selectedLocation = null;
    $scope.orders = [];
    $scope.filteredOrders = [];

    $scope.getOrderQuantity = function(supplyOrder) {
        var size = 0;
        angular.forEach(supplyOrder.items, function(item) {
            size += item.quantity;
        });
        return size;
    };

    $scope.viewOrder = function(order) {
        locationService.go("/supply/requisition/view", false, "order=" + order.id);
    };

    $scope.initLocations = function() {
        if ($scope.locations.length === 0) {
            $scope.locations.push("");
            var orders = supplyOrderService.getCompletedOrders();
            angular.forEach(orders, function (order) {
                if ($scope.locations.indexOf(order.locCode) === -1) {
                    $scope.locations.push(order.locCode);
                }
            });
        }
    };

    $scope.shouldShowOrder = function(order) {
        if ($scope.selectedLocation === "") {
            return true;
        }
        else {
            if (order.locCode === $scope.selectedLocation) {
                return true;
            }
        }
        return false;
    };

    $scope.init = function() {
        $scope.orders = supplyOrderService.getCompletedOrders();
        $scope.filteredOrders = $scope.orders;
        $scope.initLocations();
        $scope.selectedLocation = $scope.locations[0];
    };

    $scope.init();
}