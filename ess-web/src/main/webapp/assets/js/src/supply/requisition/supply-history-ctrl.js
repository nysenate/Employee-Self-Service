essSupply = angular.module('essSupply').controller('SupplyHistoryController',
['$scope', 'SupplyGetTodaysCompletedOrdersApi', 'LocationService', supplyHistoryController]);

function supplyHistoryController($scope, getTodaysCompletedOrdersApi, locationService) {

    $scope.locations = [];
    $scope.selectedLocation = null;
    $scope.orders = null;
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
            $scope.locations.push("All");
            angular.forEach($scope.orders, function (order) {
                if ($scope.locations.indexOf(order.location.code + "-" + order.location.locationTypeCode) === -1) {
                    $scope.locations.push(order.location.code + "-" + order.location.locationTypeCode);
                }
            });
        }
    };

    $scope.shouldShowOrder = function(order) {
        if ($scope.selectedLocation === "All") {
            return true;
        }
        else {
            if (order.location === $scope.selectedLocation) {
                return true;
            }
        }
        return false;
    };

    $scope.init = function() {
        getCompletedOrders();
    };

    function getCompletedOrders() {
        getTodaysCompletedOrdersApi.get(2015, function(response) {
            $scope.orders = response.result;
            $scope.filteredOrders = $scope.orders;
            $scope.initLocations();
            $scope.selectedLocation = $scope.locations[0];
        }, function(response) {

        })
    }

    $scope.init();
}