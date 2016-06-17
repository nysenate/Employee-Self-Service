essSupply = angular.module('essSupply').controller('SupplyViewController', ['$scope', 'SupplyRequisitionByIdApi',
    'LocationService', '$window', '$timeout', 'SupplyUtils', supplyViewController]);

function supplyViewController($scope, requisitionApi, locationService, $window, $timeout, supplyUtils) {

    $scope.requisitionResponse = {};
    $scope.requisitionHistory = {
        versions: []
    };
    $scope.selectedVersion = {};
    $scope.shipemnt = {};

    $scope.init = function () {
        var id = locationService.getSearchParam('requisition');
        $scope.requisitionResponse = requisitionApi.get({id: id, detail: true});
        $scope.requisitionResponse.$promise
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
        generateVersions();
        generateVersionNames();
        // Reverse so versions are in descending order.
        $scope.requisitionHistory.versions.reverse();
    };

    function generateVersions() {
        for (var k in $scope.shipment.history.items) {
            if ($scope.shipment.history.items.hasOwnProperty(k)) {
                $scope.requisitionHistory.versions.push($scope.shipment.history.items[k]);
            }
        }
    }

    function generateVersionNames() {
        for (var i = 0; i < $scope.requisitionHistory.versions.length; i++) {
            if (i === 0) {
                $scope.requisitionHistory.versions[i].name = "Original";
            }
            else if (i === $scope.requisitionHistory.versions.length - 1) {
                $scope.requisitionHistory.versions[i].name = "Current";
            }
            else {
                $scope.requisitionHistory.versions[i].name = i + 1;
            }
        }
    }

    var selectCurrentVersion = function () {
        $scope.selectedVersion = $scope.requisitionHistory.versions[0];
    };

    var shipmentResourceErrorHandler = function (response) {
        console.log("Error");
        // TODO;
    };

    $scope.init();

    $scope.sortSelectedVersionLineItems = function () {
        if ($scope.selectedVersion && $scope.selectedVersion.lineItems) {
            return supplyUtils.alphabetizeLineItems($scope.selectedVersion.lineItems);
        }
    };
}
