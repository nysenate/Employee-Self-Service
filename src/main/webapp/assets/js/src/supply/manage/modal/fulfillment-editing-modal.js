var essSupply = angular.module('essSupply')
    .directive('fulfillmentEditingModal', ['appProps', function (appProps) {
        return {
            templateUrl: appProps.ctxPath + '/template/supply/manage/modal/fulfillment-editing-modal',
            scope: {
                'supplyEmployees': '=',
                'locationStatistics': '='
            },
            controller: 'FulfillmentEditingModal',
            controllerAs: 'ctrl'
        };
    }])
    .directive('editableOrderListing', ['appProps', function (appProps) {
        return {
            restrict: 'A',
            scope: false,
            templateUrl: appProps.ctxPath + '/template/supply/manage/modal/editable-order-listing'
        }
    }])
    .controller('FulfillmentEditingModal', ['$scope', 'appProps', 'modals', 'SupplyRequisitionByIdApi', 'SupplyRequisitionRejectApi',
                                            'SupplyRequisitionProcessApi', 'SupplyRequisitionUndoApi', 'SupplyItemApi',
                                            'SupplyDestinationApi', fulfillmentEditingModal]);

function fulfillmentEditingModal($scope, appProps, modals, reqSaveApi, reqRejectApi, reqProcessApi, reqUndoApi,
                                 itemApi, destinationApi) {
    $scope.dirty = false;
    $scope.originalRequisition = {};
    $scope.editableRequisition = {};
    $scope.deliveryMethods = ['DELIVERY', 'PICKUP'];
    $scope.displayRejectInstructions = false;
    $scope.selfApprove = false;

    $scope.items = {
        all: [],
        selected: undefined
    };

    $scope.destinations = {
        allowed: [],
        selected: undefined
    };

    $scope.init = function () {
        $scope.originalRequisition = modals.params();
        $scope.editableRequisition = angular.copy($scope.originalRequisition);

        destinationApi.get({empId: appProps.user.employeeId}, function (response) {
            $scope.destinations.allowed = response.result;
            $scope.destinations.allowed.forEach(function (dest) {
                if (dest.code === $scope.editableRequisition.destination.code) {
                    $scope.destinations.selected = dest;
                }
            });
        });

        itemApi.items()
            .then(function (result) {
                $scope.items.all = result;
            });

        if (appProps.user.employeeId === $scope.originalRequisition.customer.employeeId) {
            $scope.selfApprove = true;
        }
    };
    $scope.init();

    /** Close the modal and return the promise resulting from calling the save requisition api.
     * Errors are handled in the supply-fulfillment-ctrl. */
    $scope.saveChanges = function () {
        modals.resolve(reqSaveApi.save({id: $scope.originalRequisition.requisitionId}, $scope.editableRequisition).$promise);
    };

    $scope.processReq = function () {
        modals.resolve(reqProcessApi.save({id: $scope.originalRequisition.requisitionId}, $scope.editableRequisition).$promise);
    };

    $scope.undo = function () {
        modals.resolve(reqUndoApi.save({id: $scope.originalRequisition.requisitionId}, $scope.editableRequisition).$promise);
    }

    $scope.rejectReq = function () {
        modals.resolve(reqRejectApi.save({id: $scope.originalRequisition.requisitionId}, $scope.editableRequisition).$promise);
    };

    $scope.closeModal = function () {
        modals.reject();
    };

    $scope.approveShipment = function () {
        if ($scope.selfApprove) {
            return;
        }
        $scope.processReq();
    };

    /**
     * Attempts to reject the order.
     * A note must be given when rejecting an order. If no note is given,
     * display instructions informing the user to leave a note.
     */
    $scope.rejectOrder = function () {
        if ($scope.editableRequisition.note == null || $scope.editableRequisition.note.length === 0) {
            $scope.displayRejectInstructions = true;
        } else {
            $scope.rejectReq();
        }
    };

    /**
     * Determines if an order has been edited and updates $scope.dirty appropriately.
     */
    $scope.onUpdate = function () {
        $scope.dirty = angular.toJson($scope.originalRequisition) !== angular.toJson($scope.editableRequisition);
    };

    /** --- Destination Selection--- */

    /**
     * Updates editableRequisition's destination to the location selected if different.
     * If the selected destination is invalid, reset it to the original location instead.
     */
    $scope.refreshDestination = function () {
        if ($scope.destinations.selected) { // If destinations have been loaded and initialized.
            if ($scope.destinations.selected.code !== $scope.originalRequisition.destination.code) {
                $scope.editableRequisition.destination = $scope.destinations.selected;
            }
            $scope.onUpdate();
        }
    };

    /** -- Highlighting --- */

    $scope.calculateHighlighting = function (lineItem) {
        return {
            warn: isOverPerOrderMax(lineItem) || isOverPerMonthMax(lineItem) || containsSpecialItems(lineItem),
            bold: isOverPerMonthMax(lineItem)
        }
    };

    function isOverPerOrderMax(lineItem) {
        return lineItem.quantity > lineItem.item.perOrderAllowance;
    }

    function containsSpecialItems(lineItem) {
        return lineItem.item.specialRequest;
    }

    function isOverPerMonthMax(lineItem) {
        if ($scope.locationStatistics == null) {
            return false;
        }
        var monthToDateQty = $scope.locationStatistics.getQuantityForLocationAndItem($scope.originalRequisition.destination.locId,
                                                                                     lineItem.item.commodityCode);
        return monthToDateQty > lineItem.item.perMonthAllowance;
    }

    /** Determines if a line item should be highlighted in the editable-order-listing.jsp */
    $scope.highlightLineItem = function (lineItem) {
        return lineItem.quantity > lineItem.item.perMonthAllowance || lineItem.item.specialRequest;
    };
    $scope.warning = false;

    /** --- Add Item --- **/

    $scope.addItem = function () {
        $scope.warning = false;
        var newItem = $scope.items.selected;
        if (!newItem)
            return false;
        if (isItemADuplicate(newItem)) {
            $scope.warning = true;
            return;
        }
        $scope.warning = false;
        $scope.editableRequisition.lineItems.push({item: newItem, quantity: 1});
        $scope.onUpdate();
    };

    /**
     * Reset warning message on change to select
     */
    $scope.refreshItems = function () {
        $scope.warning = false;
    };

    function isItemADuplicate(newItem) {
        var duplicateItem = false;
        angular.forEach($scope.editableRequisition.lineItems, function (lineItem) {
            if (newItem.id === lineItem.item.id) {
                duplicateItem = true;
            }
        });
        return duplicateItem;
    }
}

