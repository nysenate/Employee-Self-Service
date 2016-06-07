essSupply = angular.module('essSupply').controller('SupplyHistoryController',
    ['$scope', 'SupplyRequisitionApi', 'LocationService', supplyHistoryController]);

function supplyHistoryController($scope, requisitionApi, locationService) {

    $scope.filter = {
        date: {
            from: moment().subtract(1, 'month').format("MM/DD/YYYY"),
            to: moment().format("MM/DD/YYYY"),
            min: new Date(2016, 1, 1, 0, 0, 0),
            max: moment().format() //TODO: max and min not working
        },
        // A LocationView object
        location: {
            locations: null,
            selectedLocations: null
        },
        // An EmployeeView object
        issuer: {
            issuers: null,
            selectedIssuer: null
        }
    };

    $scope.shipments = null;
    $scope.filteredShipments = [];

    // TODO: Temp to get this working for demo.
    $scope.filteredLocations = null;
    $scope.locations = [];
    $scope.selectedIssuer = null;
    $scope.issuers = [];
    // TODO: ---------------------------------

    $scope.init = function () {
        getCompletedOrders();
    };

    $scope.init();

    function getCompletedOrders() {
        var params = {
            status: "APPROVED",
            // Only filtering by day so round dates to start/end of day.
            from: moment($scope.filter.date.from).startOf('day').format(),
            to: moment($scope.filter.date.to).endOf('day').format()
        };
        requisitionApi.get(params, function (response) {
            $scope.shipments = response.result;
            $scope.filteredShipments = $scope.shipments;
            $scope.initFilters();
            $scope.selectedLocation = $scope.locations[0];
            $scope.selectedIssuer = $scope.issuers[0];
        }, function (response) {

        })
    }

    $scope.reloadShipments = function () {
        getCompletedOrders();
    };

    // TODO: can't create filters by looping over shipments. Will NOT work once pagination in use.
    // TODO: will need to add location & issuer API with status and date range filters.
    $scope.initFilters = function () {
        $scope.locations.push("All");
        $scope.issuers.push("All");
        angular.forEach($scope.shipments, function (shipment) {
            if ($scope.locations.indexOf(shipment.activeVersion.destination.locId) === -1) {
                $scope.locations.push(shipment.activeVersion.destination.locId);
            }
            if ($scope.issuers.indexOf(shipment.activeVersion.issuer.firstName + " " + shipment.activeVersion.issuer.lastName) === -1) {
                $scope.issuers.push(shipment.activeVersion.issuer.firstName + " " + shipment.activeVersion.issuer.lastName);
            }
        });
    };

    /** --- Filter --- */

    $scope.isInFilter = function (shipment) {
        var inLocFilter = isInLocationFilter(shipment);
        var inIssuerFilter = isInIssuerFilter(shipment);
        return inLocFilter && inIssuerFilter;
    };

    function isInLocationFilter(shipment) {
        return $scope.selectedLocation === "All" || shipment.activeVersion.destination.locId === $scope.selectedLocation;
    }

    function isInIssuerFilter(shipment) {
        return $scope.selectedIssuer === 'All' || shipment.activeVersion.issuer.firstName + " " + shipment.activeVersion.issuer.lastName === $scope.selectedIssuer;
    }

    /** --- Util methods --- */

    // TODO: do we want quantity of items orderd or number of distinct items ordered?
    $scope.getOrderQuantity = function (shipment) {
        var size = 0;
        angular.forEach(shipment.activeVersion.lineItems, function (lineItem) {
            size += lineItem.quantity;
        });
        return size;
    };

    $scope.viewOrder = function (shipment) {
        locationService.go("/supply/requisition/requisition-view", false, "requisition=" + shipment.id);
    };
}
