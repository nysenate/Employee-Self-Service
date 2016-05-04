essSupply = angular.module('essSupply').controller('SupplyViewController', ['$scope', 'SupplyShipmentByIdApi',
    'LocationService', '$window', '$timeout', 'RequisitionHistory', supplyViewController]);

function supplyViewController($scope, shipmentApi, locationService, $window, $timeout, RequisitionHistory) {

    $scope.shipmentResource = {};
    $scope.requisitionHistory = {};

    $scope.init = function () {
        var id = locationService.getSearchParam('shipment');
        $scope.shipmentResource = shipmentApi.get({id: id});
        $scope.shipmentResource.$promise
            .then(extractShipment)
            .then(printIfRequested)
            .then(generateHistory)
            .catch(shipmentResourceErrorHandler);
    };

    var extractShipment = function (response) {
        $scope.shipment = response.result;
    };

    var printIfRequested = function () {
        var print = locationService.getSearchParam('print');
        if (print === 'true') {
            $timeout(function () {
                $window.print();
            })
        }
    };

    var generateHistory = function () {
        $scope.requisitionHistory = new RequisitionHistory($scope.shipment);
        console.log($scope.requisitionHistory);
    };

    var shipmentResourceErrorHandler = function (response) {
        console.log("Error");
        // TODO;
    };

    $scope.init();
}
