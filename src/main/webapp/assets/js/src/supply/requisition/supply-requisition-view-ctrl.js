essSupply = angular.module('essSupply').controller('SupplyViewController', ['$scope', 'SupplyRequisitionHistoryApi',
    'LocationService', '$window', '$timeout', 'SupplyUtils', 'modals',supplyViewController]);

function supplyViewController($scope, historyApi, locationService, $window, $timeout, supplyUtils, modals) {

    $scope.requisitionResponse = {};
    $scope.requisitionHistory = {
        versions: []
    };
    $scope.selectedVersion = {};
    $scope.shipemnt = {};

    $scope.init = function () {
        // because this page is loaded from order history, we need to highlight the page where it is from
        var fromPage = locationService.getSearchParam('fromPage');
        highlightMenu(fromPage);
        var id = locationService.getSearchParam('requisition');
        $scope.requisitionResponse = historyApi.get({id: id});
        $scope.requisitionResponse.$promise
            .then(extractShipment)
            .then(generateHistory)
            .then(selectCurrentVersion)
            .then(printIfRequested)
            .catch($scope.handleErrorResponse);
    };

    var highlightMenu = function (fromPage) {
        $(".sub-topic").removeClass("active");
        $(".sub-topic").toArray().forEach(function (t) {
            if (t.textContent.toLowerCase().replace(/\s/g, '').indexOf(fromPage) != -1) {
                t.classList.add("active");
            }
        });
    };
    var extractShipment = function (response) {
        $scope.shipment = response.result;
    };

    var printIfRequested = function () {
        var print = locationService.getSearchParam('print');
        if (print === 'true') {
            $scope.print();
        }
    };

    var generateHistory = function () {
        generateVersions();
        generateVersionNames();
        // Reverse so versions are in descending order.
        $scope.requisitionHistory.versions.reverse();
    };

    function generateVersions() {
        for (var k in $scope.shipment) {
            if ($scope.shipment.hasOwnProperty(k)) {
                $scope.requisitionHistory.versions.push($scope.shipment[k]);
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

    $scope.init();

    $scope.sortSelectedVersionLineItems = function () {
        if ($scope.selectedVersion && $scope.selectedVersion.lineItems) {
            return supplyUtils.alphabetizeLineItems($scope.selectedVersion.lineItems);
        }
    };

    $scope.displayIssuedDate = function (selectedVersion) {
        return selectedVersion.status === 'COMPLETED' || selectedVersion.status === 'APPROVED';
    };

    $scope.print = function () {
        $timeout(function () {
            $window.print();
        });
    }
}
