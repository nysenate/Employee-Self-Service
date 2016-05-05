essSupply = angular.module('essSupply').controller('SupplyViewController', ['$scope', 'SupplyShipmentByIdApi',
    'LocationService', '$window', '$timeout', 'RequisitionHistory', supplyViewController]);

function supplyViewController($scope, shipmentApi, locationService, $window, $timeout, RequisitionHistory) {

    $scope.shipmentResource = {};
    $scope.requisitionHistory = {};
    $scope.selectedVersion = {};

    $scope.init = function () {
        var id = locationService.getSearchParam('shipment');
        $scope.shipmentResource = shipmentApi.get({id: id});
        $scope.shipmentResource.$promise
            .then(extractShipment)
            .then(printIfRequested)
            .then(generateHistory)
            .then(selectCurrentVersion)
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
    };

    var selectCurrentVersion = function () {
        console.log($scope.requisitionHistory);
        $scope.selectedVersion = $scope.requisitionHistory.current();
        console.log("Setting current version");
        console.log($scope.selectedVersion);
    };

    var shipmentResourceErrorHandler = function (response) {
        console.log("Error");
        // TODO;
    };

    $scope.versionSelected = function () {
        console.log("selected");
        console.log($scope.selectedVersion);
    };

    $scope.init();
}
