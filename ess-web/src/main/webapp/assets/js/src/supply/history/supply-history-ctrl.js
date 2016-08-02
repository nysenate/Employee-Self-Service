essSupply = angular.module('essSupply').controller('SupplyHistoryController',
    ['$scope', 'EmployeesByRoleApi', 'SupplyRequisitionApi', 'LocationService', 'LocationApi', 'PaginationModel', supplyHistoryController]);

function supplyHistoryController($scope, employeesByRoleApi, requisitionApi, locationService, locationApi, paginationModel) {
    $scope.paginate = angular.extend({}, paginationModel);
    $scope.loading = true;
    $scope.locations = [];
    $scope.issuers = [];
    $scope.issuerNameToID = [];
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
        $scope.paginate.itemsPerPage = 12;
        getCompletedOrders();
    };

    $scope.init();

    function getCompletedOrders() {
        var params = {
            status: ["APPROVED", "REJECTED"],
            // Only filtering by day so round dates to start/end of day.
            from: moment($scope.filter.date.from).startOf('day').format(),
            to: moment($scope.filter.date.to).endOf('day').format(),
            limit: $scope.paginate.itemsPerPage,
            offset: $scope.paginate.getOffset()
        };
        requisitionApi.get(params, function (response) {
            $scope.shipments = response.result;
            $scope.initFilters();
            $scope.paginate.setTotalItems(response.total);
        }, function (response) {

        })
    }
    function getUpdatedOrders() {
        var params = {
            status: ["APPROVED", "REJECTED"],
            // Only filtering by day so round dates to start/end of day.
            from: moment($scope.filter.date.from).startOf('day').format(),
            to: moment($scope.filter.date.to).endOf('day').format(),
            limit: $scope.paginate.itemsPerPage,
            offset: $scope.paginate.getOffset(),
            location: $scope.selectedLocation,
            issuerId: $scope.issuerNameToID[$scope.selectedIssuer]
        };
        requisitionApi.get(params, function (response) {
            $scope.shipments = response.result;
            $scope.paginate.setTotalItems(response.total);
        }, function (response) {

        })
    }

    function getFilteredOrders() {
        var params = {
            status: ["APPROVED", "REJECTED"],
            // Only filtering by day so round dates to start/end of day.
            from: moment($scope.filter.date.from).startOf('day').format(),
            to: moment($scope.filter.date.to).endOf('day').format(),
            limit: $scope.paginate.itemsPerPage,
            offset: 1,
            location: $scope.selectedLocation,
            issuerId: $scope.issuerNameToID[$scope.selectedIssuer]
        };
        requisitionApi.get(params, function (response) {
            $scope.shipments = response.result;
            $scope.paginate.setTotalItems(response.total);
        }, function (response) {

        })
    }

    $scope.reloadShipments = function () {
        $scope.loading = true;
        getFilteredOrders();
    };
    Array.prototype.insert = function (index, item) {
        this.splice(index, 0, item);
    };

    // TODO: can't create filters by looping over shipments. Will NOT work once pagination in use.
    // TODO: will need to add location & issuer API with status and date range filters.
    $scope.initFilters = function () {
        locationApi.get().$promise
            .then(setLocations);
        employeesByRoleApi.get({role: "SUPPLY_EMPLOYEE"}).$promise
            .then(setIssuers);
    };
    var setLocations = function (response) {
        response.result.forEach(function (e) {
            $scope.locations.push(e.locId);
        });
        sortCodes($scope.locations);
        $scope.locations.insert(0, "All");
        $scope.selectedLocation = $scope.locations[0];
    };
    var setIssuers = function (response) {
        response.result.forEach(function (e) {
            $scope.issuers.push(e.fullName);
            $scope.issuerNameToID[e.fullName] = e.employeeId;
        });
        $scope.issuers.insert(0, "All");
        $scope.selectedIssuer = $scope.issuers[0];
    };
    var sortCodes = function (codes) {
        codes.sort(function (a, b) {
            if (a < b) return -1;
            if (a > b) return 1;
            return 0;
        })
    };

    /** --- Filter --- */

    $scope.isInFilter = function (shipment) {
        var inLocFilter = isInLocationFilter(shipment);
        var inIssuerFilter = isInIssuerFilter(shipment);
        return inLocFilter && inIssuerFilter;
    };

    function isInLocationFilter(shipment) {
        return $scope.selectedLocation === "All" || shipment.destination.locId === $scope.selectedLocation;
    }

    function isInIssuerFilter(shipment) {
        return $scope.selectedIssuer === "All" || shipment.issuer.firstName + " " + shipment.issuer.lastName === $scope.selectedIssuer;
    }

    /** --- Util methods --- */

    // TODO: do we want quantity of items orderd or number of distinct items ordered?
    $scope.getOrderQuantity = function (shipment) {
        var size = 0;
        angular.forEach(shipment.lineItems, function (lineItem) {
            size += lineItem.quantity;
        });
        return size;
    };

    /** Updates the displayed requisitions whenever filters or page is changed. */
    $scope.updateRequisitions = function () {
        $scope.loading = true;
        getUpdatedOrders();
    };

    function doneLoading() {
        $scope.loading = false;
    }

    $scope.viewRequisition = function (shipment) {
        locationService.go("/supply/requisition/requisition-view", false, "requisition=" + shipment.requisitionId);
    };
}
