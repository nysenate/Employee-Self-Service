essSupply = angular.module('essSupply').controller('SupplyHistoryController',
['$scope', 'SupplyOrdersApi', 'LocationService', supplyHistoryController]);

function supplyHistoryController($scope, supplyOrdersApi, locationService) {

    $scope.filter = {
        date: {
            from: moment().subtract(1, 'month').toDate(),
            to: new Date(),
            min: new Date(2016, 1, 1),
            max: moment().format('YYYY-MM-DD') //TODO: max and min not working
        },
        location: {                                         // LocationView object
            locations: null,
            selectedLocations: null
        },
        issuer: {                                           // EmployeeView object
            issuers: null,
            selectedIssuer: null
        }
    };

    $scope.orders = null;
    $scope.filteredOrders = [];

    // TODO: Temp to get this working for demo.
    $scope.filteredLocations = null;
    $scope.locations = [];
    $scope.selectedIssuer = null;
    $scope.issuers = [];
    // TODO: ---------------------------------

    $scope.init = function() {
        getCompletedOrders();
    };

    $scope.init();

    function getCompletedOrders() {
        var params = {
            status: "COMPLETED",
            from: moment($scope.filter.date.from).format('YYYY-MM-DD'),
            to: moment($scope.filter.date.to).format('YYYY-MM-DD')
        };
        supplyOrdersApi.get(params, function(response) {
            $scope.orders = response.result;
            $scope.filteredOrders = $scope.orders;
            $scope.initFilters();
            $scope.selectedLocation = $scope.locations[0];
            $scope.selectedIssuer = $scope.issuers[0];
        }, function(response) {

        })
    }

    // TODO: can't create filters by looping over orders. Will NOT work once pagination in use.
    // TODO: will need to add location & issuer API with status and date range filters.
    $scope.initFilters = function() {
        $scope.locations.push("All");
        $scope.issuers.push("All");
        angular.forEach($scope.orders, function (order) {
            if ($scope.locations.indexOf(order.location.code + "-" + order.location.locationTypeCode) === -1) {
                $scope.locations.push(order.location.code + "-" + order.location.locationTypeCode);
            }
            if ($scope.issuers.indexOf(order.issuingEmployee.firstName + " " + order.issuingEmployee.lastName) === -1) {
                $scope.issuers.push(order.issuingEmployee.firstName + " " + order.issuingEmployee.lastName);
            }
        });
    };

    /** --- Filter --- */

    $scope.isInFilter = function(order) {
        if ($scope.selectedLocation === "All") {
            return true;
        }
        else {
            if (order.location.code + '-' + order.location.locationTypeCode === $scope.selectedLocation) {
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