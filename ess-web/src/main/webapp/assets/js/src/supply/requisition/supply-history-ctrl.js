essSupply = angular.module('essSupply').controller('SupplyHistoryController',
['$scope', 'SupplyGetTodaysCompletedOrdersApi', 'LocationService', supplyHistoryController]);

function supplyHistoryController($scope, getTodaysCompletedOrdersApi, locationService) {

    var filterTypes = {
        NONE: 0,
        CUSTOMER: 1,
        ISSUER: 2,
        LOCATION: 3,
        DATE: 4
    };

    $scope.activeFilter = filterTypes.NONE;

    $scope.selectedLocation = null;
    $scope.locations = [];
    $scope.selectedCustomer = null;
    $scope.customers = [];
    $scope.selectedIssuer = null;
    $scope.issuers = [];

    $scope.orders = null;
    $scope.filteredOrders = [];


    $scope.init = function() {
        getCompletedOrders();
    };

    $scope.init();

    function getCompletedOrders() {
        getTodaysCompletedOrdersApi.get(2015, function(response) {
            $scope.orders = response.result;
            $scope.filteredOrders = $scope.orders;
            $scope.initFilters();
            $scope.selectedLocation = $scope.locations[0];
        }, function(response) {

        })
    }

    $scope.initFilters = function() {
        $scope.locations.push("All");
        $scope.customers.push("All");
        $scope.issuers.push("All");
        angular.forEach($scope.orders, function (order) {
            if ($scope.locations.indexOf(order.location.code + "-" + order.location.locationTypeCode) === -1) {
                $scope.locations.push(order.location.code + "-" + order.location.locationTypeCode);
            }
        });
    };

    /** --- Filter --- */

    $scope.isInFilter = function(order) {
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

    /** --- Util methods --- */

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
}