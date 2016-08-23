essSupply = angular.module('essSupply').controller('SupplyFulfillmentController', ['$scope',
    'SupplyRequisitionApi', 'SupplyEmployeesApi', 'SupplyItemsApi', 'modals', '$interval',
    'LocationService', 'SupplyLocationStatisticsService', supplyFulfillmentController]);

function supplyFulfillmentController($scope, requisitionApi, supplyEmployeesApi,
                                     itemsApi, modals, $interval, locationService,
                                     locationStatisticsService) {

    $scope.pendingSearch = {
        matches: [],
        response: {},
        error: false
    };

    $scope.processingSearch = {
        matches: [],
        response: {},
        error: false
    };

    $scope.completedSearch = {
        matches: [],
        response: {},
        error: false
    };

    $scope.approvedSearch = {
        matches: [],
        response: {},
        error: false
    };

    $scope.canceledSearch = {
        matches: [],
        response: {},
        error: false
    };

    /** The response received from saving a requisition in the edit modal. */
    $scope.saveResponse = {
        response: {},
        error: false
    };

    /** Used in edit modals to assign an issuer. */
    $scope.supplyEmployees = [];

    $scope.locationStatistics = null;

    $scope.init = function () {
        updateShipments();
        getSupplyEmployees();
        getLocationStatistics();
    };

    $scope.init();

    function updateShipments() {
        getPendingShipments();
        getProcessingShipments();
        getCompletedShipments();
        getApprovedShipments();
        getCanceledShipments();
    }

    // Refresh data every minute.
    var intervalPromise = $interval(function () {
        updateShipments()
    }, 60000);
    // Stop refreshing when we leave this page.
    $scope.$on('$destroy', function () {
        $interval.cancel(intervalPromise)
    });

    /**
     * --- Api Calls ---
     */

    function getSupplyEmployees() {
        supplyEmployeesApi.get(function (response) {
            $scope.supplyEmployees = response.result;
        }, function (errorResponse) {
        })
    }

    function getLocationStatistics() {
        var year = moment().year();
        var month = moment().month() + 1; // Moment is 0 indexed, API is not.
        locationStatisticsService.calculateLocationStatisticsFor(year, month)
            .then(function (result) {
                      $scope.locationStatistics = result;
                  }
            );
    }

    /** Get all pending shipments */
    function getPendingShipments() {
        var params = {
            status: "PENDING",
            from: moment.unix(1).format()
        };
        $scope.pendingSearch.response = requisitionApi.get(params, function (response) {
            $scope.pendingSearch.matches = response.result;
            $scope.pendingSearch.error = false;
        }, function (errorResponse) {
            $scope.pendingSearch.matches = [];
            $scope.pendingSearch.error = true;
        })
    }

    function getProcessingShipments() {
        var params = {
            status: "PROCESSING",
            from: moment.unix(1).format()
        };
        $scope.processingSearch.response = requisitionApi.get(params, function (response) {
            $scope.processingSearch.matches = response.result;
            $scope.processingSearch.error = false;
        }, function (errorResponse) {
            $scope.processingSearch.matches = [];
            $scope.processingSearch.error = true;
        })
    }

    function getCompletedShipments() {
        var params = {
            status: "COMPLETED",
            from: moment.unix(1).format()
        };
        $scope.completedSearch.response = requisitionApi.get(params, function (response) {
            $scope.completedSearch.matches = response.result;
            $scope.completedSearch.error = false;
        }, function (errorResponse) {
            $scope.completedSearch.matches = [];
            $scope.completedSearch.error = true;
        })
    }

    function getApprovedShipments() {
        var params = {
            status: "APPROVED",
            from: moment().startOf('day').format(),
            dateField: "approved_date_time"
        };
        $scope.approvedSearch.response = requisitionApi.get(params, function (response) {
            $scope.approvedSearch.matches = response.result;
            $scope.approvedSearch.error = false;
        }, function (errorResponse) {
            $scope.approvedSearch.matches = [];
            $scope.approvedSearch.error = true;
        })
    }

    /** Get shipments that have been canceled today. A shipment is canceled when its order is rejected. */
    function getCanceledShipments() {
        var params = {
            status: "REJECTED",
            from: moment().startOf('day').format(),
            dateField: "rejected_date_time"
        };
        $scope.canceledSearch.response = requisitionApi.get(params, function (response) {
            $scope.canceledSearch.matches = response.result;
            $scope.canceledSearch.error = false;
        }, function (errorResponse) {
            $scope.canceledSearch.matches = [];
            $scope.canceledSearch.error = true;
        })
    }

    /** --- Util methods --- */

    /* Return the number of distinct items ordered in a requisition */
    $scope.getOrderQuantity = function (requisition) {
        var size = 0;
        angular.forEach(requisition.lineItems, function (item) {
            size++;
        });
        return size;
    };

    /** --- Highlighting --- */

    $scope.calculateHighlighting = function (requisition) {
        return {
            warn: isOverPerOrderMax(requisition) || isOverPerMonthMax(requisition) || containsSpecialItems(requisition),
            bold: isOverPerMonthMax(requisition)
        }
    };

    function isOverPerOrderMax(requisition) {
        var isOverPerOrderMax = false;
        angular.forEach(requisition.lineItems, function (lineItem) {
            if (lineItem.quantity > lineItem.item.maxQtyPerOrder) {
                isOverPerOrderMax = true;
            }
        });
        return isOverPerOrderMax;
    }

    function containsSpecialItems(requisition) {
        var containsSpecialItems = false;
        angular.forEach(requisition.lineItems, function (lineItem) {
            if (lineItem.item.visibility === 'SPECIAL') {
                containsSpecialItems = true;
            }
        });
        return containsSpecialItems;
    }

    function isOverPerMonthMax(requisition) {
        if ($scope.locationStatistics == null) {
            return false;
        }
        var isOverPerMonthMax = false;
        angular.forEach(requisition.lineItems, function (lineItem) {
            var monthToDateQty = $scope.locationStatistics.getQuantityForLocationAndItem(requisition.destination.locId,
                                                                                         lineItem.item.commodityCode);
            if (monthToDateQty > lineItem.item.suggestedMaxQty) {
                isOverPerMonthMax = true;
            }
        });
        return isOverPerMonthMax;
    }

    /** --- Modals --- */

    $scope.showEditingModal = function (requisition) {
        /** Editing modal returns a promise containing the save requisition api request
         * if the user saved their changes, undefined otherwise.*/
        $scope.saveResponse.response = modals.open('fulfillment-editing-modal', requisition)
            .then(successfulSave)
            .catch(errorSaving);
    };

    function successfulSave(response) {
        locationService.go("/supply/manage/fulfillment", true);
    }

    function errorSaving(response) {
        if (response === undefined || response.status !== 409) {
            // modal was rejected for a reason besides a failed update. E.g. clicking outside the modal will reject it.
            return;
        }
        $scope.saveResponse = response;
        $scope.saveResponse.error = true;
        modals.open('500', {details: response});
        console.log(response);
    }

    $scope.showImmutableModal = function (requisition) {
        modals.open('fulfillment-immutable-modal', requisition);
    };
}
