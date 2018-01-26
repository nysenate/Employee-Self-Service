essSupply = angular.module('essSupply').controller('SupplyFulfillmentController', ['$scope',
                                                                                   'SupplyRequisitionApi', 'SupplyEmployeesApi', 'SupplyItemApi', 'modals', '$interval',
                                                                                   'LocationService', 'SupplyLocationStatisticsService', 'SupplyUtils', '$q', supplyFulfillmentController]);

function supplyFulfillmentController($scope, requisitionApi, supplyEmployeesApi,
                                     itemApi, modals, $interval, locationService,
                                     locationStatisticsService, supplyUtils, $q) {

    const REQ_ID_SEARCH_PARAM = "requisitionId";

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

    $scope.syncFailedSearch = {
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

    function updateShipments() {
        getPendingShipments();
        getProcessingShipments();
        getCompletedShipments();
        getApprovedShipments();
        getSyncFailedShipments();
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
            modals.open('500', {details: errorResponse});
            console.error(errorResponse);
        })
    }

    function getLocationStatistics() {
        var year = moment().year();
        var month = moment().month() + 1; // Moment is 0 indexed, API is not.
        locationStatisticsService.calculateLocationStatistics(year, month)
            .then(function (result) {
                $scope.locationStatistics = result;
            })
            .catch(function (errorResponse) {
                modals.open('500', {details: errorResponse});
                console.error(errorResponse);
            });
    }

    /** Get all pending shipments */
    function getPendingShipments() {
        var params = {
            status: "PENDING",
            from: moment.unix(1).format(),
            limit: 'ALL',
            offset: 0
        };
        $scope.pendingSearch.response = requisitionApi.get(params);
        return $scope.pendingSearch.response.$promise
            .then(function (response) {
                $scope.pendingSearch.matches = response.result;
                $scope.pendingSearch.error = false;
            })
            .catch(function (errorResponse) {
                modals.open('500', {details: errorResponse});
                console.error(errorResponse);
            });
    }

    function getProcessingShipments() {
        var params = {
            status: "PROCESSING",
            from: moment.unix(1).format(),
            limit: 'ALL',
            offset: 0
        };
        $scope.processingSearch.response = requisitionApi.get(params);
        return $scope.processingSearch.response.$promise
            .then(function (response) {
                $scope.processingSearch.matches = response.result;
                $scope.processingSearch.error = false;
            })
            .catch(function (errorResponse) {
                modals.open('500', {details: errorResponse});
                console.error(errorResponse);
            });
    }

    function getCompletedShipments() {
        var params = {
            status: "COMPLETED",
            from: moment.unix(1).format(),
            limit: 'ALL',
            offset: 0
        };
        $scope.completedSearch.response = requisitionApi.get(params);
        return $scope.completedSearch.response.$promise
            .then(function (response) {
                $scope.completedSearch.matches = response.result;
                $scope.completedSearch.error = false;
            })
            .catch(function (errorResponse) {
                modals.open('500', {details: errorResponse});
                console.error(errorResponse);
            });
    }

    function getApprovedShipments() {
        var params = {
            status: "APPROVED",
            from: moment().startOf('day').format(),
            dateField: "approved_date_time",
            limit: 'ALL',
            offset: 0
        };
        $scope.approvedSearch.response = requisitionApi.get(params);
        return $scope.approvedSearch.response.$promise
            .then(function (response) {
                $scope.approvedSearch.matches = response.result;
                sortRequisitionsByIdDesc($scope.approvedSearch.matches);
                $scope.approvedSearch.error = false;
            })
            .catch(function (errorResponse) {
                modals.open('500', {details: errorResponse});
                console.error(errorResponse);
            });
    }

    function sortRequisitionsByIdDesc(reqs) {
        reqs.sort(function(a, b){
            if (a.requisitionId < b.requisitionId) return 1;
            if (a.requisitionId > b.requisitionId) return -1;
            return 0;
        });
        return reqs;
    }

    /** Get all sync failures prior to today. */
    function getSyncFailedShipments() {
        var params = {
            from: moment.unix(1).format(),
            to: moment().startOf('day').format(),
            status: "APPROVED",
            dateField: "approved_date_time",
            savedInSfms: false,
            limit: 'ALL',
            offset: 0
        };
        $scope.syncFailedSearch.response = requisitionApi.get(params);
        return $scope.syncFailedSearch.response.$promise
            .then(function (response) {
                $scope.syncFailedSearch.matches = response.result;
                $scope.syncFailedSearch.matches = removeNewPlaceReq($scope.syncFailedSearch.matches);
                $scope.syncFailedSearch.error = false;
            })
            .catch(function (errorResponse) {
                modals.open('500', {details: errorResponse});
                console.error(errorResponse);
            });
    }

    /*remove new place req from failed sync*/
    function removeNewPlaceReq(input) {
        var result = [];
        input.forEach(function (ele) {
            if (ele.lastSfmsSyncDateTime != null) // if current req have not been sync , then it is a new one
                result.push(ele);
        });
        return result;
    }

    /** Get shipments that have been canceled today. A shipment is canceled when its order is rejected. */
    function getCanceledShipments() {
        var params = {
            status: "REJECTED",
            from: moment().startOf('day').format(),
            dateField: "rejected_date_time",
            limit: 'ALL',
            offset: 0
        };
        $scope.canceledSearch.response = requisitionApi.get(params);
        return $scope.canceledSearch.response.$promise
            .then(function (response) {
                $scope.canceledSearch.matches = response.result;
                $scope.canceledSearch.error = false;
            })
            .catch(function (errorResponse) {
                modals.open('500', {details: errorResponse});
                console.error(errorResponse);
            });
    }

    /** --- Util methods --- */

    /* Return the number of distinct items ordered in a requisition */
    $scope.distinctItemQuantity = function (requisition) {
        return supplyUtils.countDistinctItemsInRequisition(requisition);
    };

    /** --- Highlighting --- */

    $scope.calculateHighlighting = function (requisition) {
        return {
            warn: supplyUtils.containsItemOverOrderMax(requisition) || isOverPerMonthMax(requisition) || supplyUtils.containsSpecialItem(requisition),
            bold: isOverPerMonthMax(requisition)
        }
    };

    function isOverPerMonthMax(requisition) {
        if ($scope.locationStatistics == null) {
            return false;
        }
        var isOverPerMonthMax = false;
        angular.forEach(requisition.lineItems, function (lineItem) {
            var monthToDateQty = $scope.locationStatistics.getQuantityForLocationAndItem(requisition.destination.locId,
                                                                                         lineItem.item.commodityCode);
            if (monthToDateQty > lineItem.item.perMonthAllowance) {
                isOverPerMonthMax = true;
            }
        });
        return isOverPerMonthMax;
    }

    /** --- Modals --- */

    /**
     * Display detailed requisition information for the provided requisition.
     * Uses either the editable or immutable dialog to display the requisition
     * depending on if the requisition is allowed to be edited in its current status.
     */
    $scope.openRequisitionModal = function (requisition) {
        if (requisition == null) {
            return;
        }
        var status = requisition.status;
        if (status === 'PENDING' || status === 'PROCESSING' || status === 'COMPLETED') {
            // Editing modal returns a promise containing the save requisition api request
            // if the user saved their changes, undefined otherwise.
            // This allows us to properly handle any errors that occurred while saving.
            $scope.saveResponse.response = modals.open('fulfillment-editing-modal', requisition)
                .then(successfulSave)
                .catch(errorSaving)
                .finally(resetSearchParams);
        }
        else {
            modals.open('fulfillment-immutable-modal', requisition)
                .finally(resetSearchParams);
        }
    };

    function successfulSave(response) {
        locationService.go("/supply/manage/fulfillment", true);
    }

    function errorSaving(response) {
        if (response === undefined) {
            // modal was rejected for a reason besides a failed update. E.g. clicking cancel button.
            return;
        }
        if (response.status === 409) {
            // Requisition Conflict error, display error notification.
            $scope.saveResponse.error = true;
        }
        else {
            // Any other server error, display internal error modal.
            modals.open('500', {details: response});
            console.log(response);
        }
        return response;
    }

    /** --- Url Params --- */

    function resetSearchParams() {
        locationService.setSearchParam(REQ_ID_SEARCH_PARAM, null);
    }

    /**
     * This gets called whenever a user clicks on a requisition on the fulfillment page.
     *
     * Setting the search param will trigger the '$locationChageSuccess' event, which will
     * then open a modal displaing this requisitions information.
     * @param requisitionId
     */
    $scope.setRequisitionSearchParam = function (requisitionId) {
        locationService.setSearchParam(REQ_ID_SEARCH_PARAM, requisitionId);
    };

    /**
     * Opens a modal displaying detailed information for a requisition.
     * @param requisitionId The id of the requisition to open in a modal.
     */
    $scope.$on('$locationChangeSuccess', function (event, newUrl) {
        if (newUrl.indexOf('supply/manage/fulfillment') !== -1) {
            displayRequisitionWithId(locationService.getSearchParam("requisitionId"))
        }
    });

    function displayRequisitionWithId(requisitionId) {
        if (requisitionId != null) {
            var requisition = findRequisitionById(requisitionId);
            $scope.openRequisitionModal(requisition);
        }
    }

    /**
     * Searches all requisitions loaded in this page for one matching the given requisitionId.
     * @returns The matching requisition or undefined if no match is found.
     */
    function findRequisitionById(requisitionId) {
        var allReqs = $scope.pendingSearch.matches.concat($scope.processingSearch.matches,
                                                          $scope.completedSearch.matches,
                                                          $scope.approvedSearch.matches,
                                                          $scope.syncFailedSearch.matches,
                                                          $scope.canceledSearch.matches);
        for (var i = 0; i < allReqs.length; i++) {
            if (allReqs[i].requisitionId == requisitionId) {
                return allReqs[i];
            }
        }
    }

    /** --- Init --- */

    $scope.init = function () {
        // Gather all requisition api call promises so we can check when they are all resolved.
        var reqApiPromises = [];
        reqApiPromises.push(getPendingShipments());
        reqApiPromises.push(getProcessingShipments());
        reqApiPromises.push(getCompletedShipments());
        reqApiPromises.push(getApprovedShipments());
        reqApiPromises.push(getSyncFailedShipments());
        reqApiPromises.push(getCanceledShipments());
        // This is executed when all promises in reqApiPromises are resolved.
        $q.all(reqApiPromises).then(
            function () {
                // If requisitionId set in search params, display the modal for that requisition.
                // We cannot do this until all requisitions are loaded.
                displayRequisitionWithId(locationService.getSearchParam(REQ_ID_SEARCH_PARAM));
            }
        );

        getSupplyEmployees();
        getLocationStatistics();
    };

    $scope.init();
}
