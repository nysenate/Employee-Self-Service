essSupply = angular.module('essSupply').controller('SupplyViewController', ['$scope', 'SupplyShipmentByIdApi',
    'LocationService', '$window', '$timeout', supplyViewController]);

function supplyViewController($scope, shipmentApi, locationService, $window, $timeout) {

    $scope.shipmentResource = {};
    $scope.shipment = {};
    $scope.versions = [];

    $scope.init = function () {
        var id = locationService.getSearchParam('shipment');
        $scope.shipmentResource = shipmentApi.get({id: id});
        $scope.shipmentResource.$promise
            .then(extractShipment)
            .then(printIfRequested)
            .then(assembleVersions)
            .catch(shipmentResourceErrorHandler);
    };

    var extractShipment = function (response) {
        $scope.shipment = response.result;
        console.log($scope.shipmentResource);
    };

    var printIfRequested = function () {
        var print = locationService.getSearchParam('print');
        if (print === 'true') {
            $timeout(function () {
                $window.print();
            })
        }
    };

    var assembleVersions = function () {
        var orderVersions = $scope.shipment.order.history.items;
        var shipmentVersions = $scope.shipment.history.items;
        $scope.versions = combineVersions(orderVersions, shipmentVersions);
        console.log($scope.versions);
    };

    // Create version object.. add append/join version functions to it?
    function combineVersions(orderVersions, shipmentVersions) {
        var versions = angular.copy(orderVersions);
        joinShipmentVersions(shipmentVersions, versions);
        return versions;
    }

    function joinShipmentVersions(shipmentVersions, versions) {
        for (var key in shipmentVersions) {
            if (shipmentVersions.hasOwnProperty(key)) {
                var matchedKey = findVersionKeyMatching(versions, key);
                if (matchedKey) {
                    versions[matchedKey].shipmentStatus = shipmentVersions[key].status;
                    versions[matchedKey].issuer = shipmentVersions[key].issuer;
                }
                else {
                    versions[key] = {
                        shipmentStatus: shipmentVersions[key].status,
                        issuer: shipmentVersions[key].issuer
                    };
                }
            }
        }
    }

    function findVersionKeyMatching(versions, shipmentKey) {
        for (var verDate in versions) {
            if (versions.hasOwnProperty(verDate)) {
                if (Math.abs(new Date(verDate) - new Date(shipmentKey)) < 1000) {
                    return verDate;
                }
            }
        }
    }

    var shipmentResourceErrorHandler = function (response) {
        console.log("Error");
        // TODO;
    };

    $scope.init();
}
