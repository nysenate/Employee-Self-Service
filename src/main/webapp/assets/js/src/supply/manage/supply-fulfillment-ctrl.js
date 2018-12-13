essSupply = angular.module('essSupply').controller('SupplyFulfillmentController', ['$scope', '$timeout', 'appProps',
                                                                                   'SupplyRequisitionApi', 'SupplyEmployeesApi', 'SupplyItemApi', 'modals', '$interval',
                                                                                   'LocationService', 'SupplyLocationStatisticsService', 'SupplyUtils', '$q', supplyFulfillmentController]);

function supplyFulfillmentController($scope, $timeout, appProps, requisitionApi, supplyEmployeesApi,
                                     itemApi, modals, $interval, locationService,
                                     locationStatisticsService, supplyUtils, $q) {

    var REQ_ID_QUERY_PARAM = "requisitionId";

    $scope.data = {
        reqs: {             // Collections of requisitions
            map: {},            // Map of requisitionId to requisition.
            pending: [],        // Pending requisitions.
            processing: [],     // Processing requisitions.
            completed: [],      // Completed requisitions.
            approved: [],       // Approved requisitions.
            rejected: []        // Rejected requisitions.
        },
        reqRequest: {
            response: {},
            error: false
        },
        locationStatistics: undefined,
        supplyEmployees: []     // List of supply employees used in edit modal to assign issuer.
    };

    /** The response received from saving a requisition in the edit modal.
     * Used to display a requisition concurrent modification notification. */
    $scope.saveResponse = {
        response: {},
        error: false
    };

    this.$onInit = function () {
        initRequisitions()
            .then(processQueryParams);
        getSupplyEmployees();
        getLocationStatistics();
        $scope.connectToSocket();
        scheduleReload()
    };

    this.$onDestroy = function () {
        $scope.stompClient.disconnect();
    };

    function initRequisitions() {
        var mostPromise = initMostReqs()
            .then(addToMap);
        var rejectedPromise = initRejectedReqs()
            .then(addToMap);

        var promises = [mostPromise, rejectedPromise];
        return $q.all(promises).then(initReqCollections);

        function addToMap(reqs) {
            reqs.forEach(function (req) {
                $scope.data.reqs.map[req.requisitionId] = req;
            })
        }

        /**
         * Gets all requisitions which have not been rejected or reconciled.
         * @return A Promise which is resolved with an array of requisitions.
         */
        function initMostReqs() {
            var params = {
                status: ['PENDING', 'PROCESSING', 'COMPLETED', 'APPROVED'],
                reconciled: 'false',
                from: moment.unix(1).format(),
                limit: 'ALL',
                offset: 0
            };
            $scope.data.reqRequest.response = requisitionApi.get(params);
            return $scope.data.reqRequest.response.$promise
                .then(function (response) {
                    return response.result;
                })
                .catch($scope.handleErrorResponse);
        }

        /**
         * Gets requisitions which have been rejected today.
         * @return A Promise which is resolved with an array of rejected requisitions.
         */
        function initRejectedReqs() {
            var params = {
                status: "REJECTED",
                from: moment().startOf('day').format(),
                dateField: "rejected_date_time",
                limit: 'ALL',
                offset: 0
            };
            return requisitionApi.get(params).$promise
                .then(function (response) {
                    return response.result;
                })
                .catch($scope.handleErrorResponse);
        }
    }

    /**
     * Initialize all individual requisition collections from the requisitions in the data.reqs.map
     */
    function initReqCollections() {
        $scope.data.reqs.pending = [];
        $scope.data.reqs.processing = [];
        $scope.data.reqs.completed = [];
        $scope.data.reqs.approved = [];
        $scope.data.reqs.rejected = [];
        var map = $scope.data.reqs.map;
        angular.forEach(map, function (value, key) {
            switch (value.status) {
                case 'PENDING':
                    $scope.data.reqs.pending.push(value);
                    break;
                case 'PROCESSING':
                    $scope.data.reqs.processing.push(value);
                    break;
                case 'COMPLETED':
                    $scope.data.reqs.completed.push(value);
                    break;
                case 'APPROVED':
                    $scope.data.reqs.approved.push(value);
                    break;
                case 'REJECTED':
                    $scope.data.reqs.rejected.push(value);
                    break;
                default:
                    console.log("Unable to match status for requisition: " + value);
            }
        });
    }

    function processQueryParams() {
        var reqIdQueryParamValue = locationService.getSearchParam(REQ_ID_QUERY_PARAM);
        if (reqIdQueryParamValue) {
            displayRequisitionWithId(reqIdQueryParamValue);
        }
    }

    $scope.connectToSocket = function () {
        var socket = new SockJS(appProps.ctxPath + '/socket');
        $scope.stompClient = Stomp.over(socket);
        $scope.stompClient.connect({}, function (frame) {
            $scope.stompClient.subscribe('/event/requisition', function (event) {
                var updatedReq = JSON.parse(event.body);
                $scope.$apply(function () {
                    $scope.data.reqs.map[updatedReq.requisitionId] = updatedReq;
                    initReqCollections();
                });
            })
        })
    };

    /**
     * Reload once a day at 00:15.
     * Since web sockets are now used for requisition updates, a user could use this page
     * without ever refreshing it. However, the data listed below requires a daily refresh.
     * To ensure this data is updated daily, schedule a page reload at 00:15.
     *
     * Data benefiting from daily updates:
     *      - Rejected requisitions.
     *      - Location statistics.
     *      - Supply employee list.
     */
    function scheduleReload() {
        var now = moment().valueOf();
        var endOfDay = moment().endOf('day').add(15, 'minutes').valueOf();
        var delay = endOfDay - now;
        $timeout($scope.reload, delay);
    }

    $scope.reload = function () {
        locationService.go("/supply/manage/fulfillment", true);
    };

    /**
     * --- Api Calls ---
     */

    function getSupplyEmployees() {
        supplyEmployeesApi.get(function (response) {
            $scope.data.supplyEmployees = response.result;
        }, $scope.handleErrorResponse)
    }

    function getLocationStatistics() {
        var year = moment().year();
        var month = moment().month() + 1; // Moment is 0 indexed, API is not.
        locationStatisticsService.calculateLocationStatistics(year, month)
            .then(function (result) {
                $scope.data.locationStatistics = result;
            })
            .catch($scope.handleErrorResponse);
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
        if ($scope.data.locationStatistics == null) {
            return false;
        }
        var isOverPerMonthMax = false;
        angular.forEach(requisition.lineItems, function (lineItem) {
            var monthToDateQty = $scope.data.locationStatistics.getQuantityForLocationAndItem(requisition.destination.locId,
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

        function successfulSave(response) {
            modals.resolve();
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
                $scope.handleErrorResponse(response);
            }
            return response;
        }
    };


    /** --- Url Params --- */

    function resetSearchParams() {
        locationService.setSearchParam(REQ_ID_QUERY_PARAM, null);
    }

    /**
     * This gets called whenever a user clicks on a requisition on the fulfillment page.
     *
     * Setting the search param will trigger the '$locationChageSuccess' event, which will
     * then open a modal displaing this requisitions information.
     * @param requisitionId
     */
    $scope.setRequisitionSearchParam = function (requisitionId) {
        locationService.setSearchParam(REQ_ID_QUERY_PARAM, requisitionId);
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
        return $scope.data.reqs.map[requisitionId];
    }
}
