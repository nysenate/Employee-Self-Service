essSupply = angular.module('essSupply').controller('SupplyFulfillmentController', ['$scope',
    'SupplyRequisitionApi', 'SupplyEmployeesApi', 'SupplyItemsApi', 'modals', '$interval', supplyFulfillmentController]);

function supplyFulfillmentController($scope, requisitionApi, supplyEmployeesApi,
                                     itemsApi, modals, $interval) {

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
    
    /** Items are sent to modals to allow adding of items to an order. */
    $scope.itemSearch = {
        matches: [],
        response: {},
        error: false
    };

    /** Used in edit modals to assign an issuer. */
    $scope.supplyEmployees = [];

    $scope.init = function () {
        updateShipments();
        getSupplyEmployees();
        getSupplyItems();
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
    
    function getSupplyItems() {
        $scope.itemSearch.response = itemsApi.get(function (response) {
            $scope.itemSearch.matches = response.result;
            $scope.itemSearch.error = false;
        }, function (errorResponse) {
            $scope.itemSearch.error = true;
            $scope.itemSearch.matches = [];
        })
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
            from: moment().startOf('day').format()
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
            from: moment().startOf('day').format()
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
        angular.forEach(requisition.activeVersion.lineItems, function (item) {
            size++;
        });
        return size;
    };

    /** --- Highlighting --- */

    $scope.highlightShipment = function (requisition) {
        var highlight = false;
        // TODO highlighting logic has changed, this needs to be updated.
        // angular.forEach(requisition.activeVersion.lineItems, function (lineItem) {
        //     if (lineItem.quantity > lineItem.item.suggestedMaxQty) {
        //         highlight = true;
        //     }
        // });
        return highlight;
    };

    $scope.highlightLineItem = function (lineItem) {
        // TODO highlighting logic has changed, this needs to be updated.
        // return lineItem.quantity > lineItem.item.suggestedMaxQty
        return false;
    };

    /** --- Modals --- */

    $scope.showEditingModal = function (shipment) {
        modals.open('fulfillment-editing-modal', shipment);
    };

    $scope.showImmutableModal = function (shipment) {
        modals.open('fulfillment-immutable-modal', shipment);
    };
}
