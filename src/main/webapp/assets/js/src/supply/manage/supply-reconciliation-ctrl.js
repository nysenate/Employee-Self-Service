essSupply = angular.module('essSupply').controller('SupplyReconciliationController',
                                                   ['$scope', 'SupplyRequisitionApi', 'SupplyReconciliationApi', 'LocationService', 'SupplyUtils',
                                                    'modals', '$window', '$timeout', supplyReconciliationController]);


function supplyReconciliationController($scope, requisitionApi, reconcileApi, locationService, supplyUtils,
                                        modals, $window, $timeout) {

    /** If a particular item is selected, displays information on all orders containing that item. */
    $scope.selectedItem = null;

    $scope.viewItems = [];
    $scope.reconcilableSearch = {
        matches: [],
        items: [],
        response: {},
        error: false
    };
    $scope.currentPage = 1;

    /** Map of item id's to shipments containing that item. */
    $scope.reconcilableItemMap = {};

    $scope.reconciliationStatus = {
        attempted: false,
        result: {},
        resultErrorMap: new Map(), // A map of itemId to a reconciliation error. Use for quick lookup when checking if an item has an error.
        resetResults: function () {
            this.result = {};
            this.resultErrorMap = new Map();
        }
    };
    $scope.inventory = {
        locationCode: 'LC100S',
        locationType: 'P',
        itemQuantities: {},
        isComplete: function () {
            for (var itemId in this.itemQuantities) {
                if (this.itemQuantities.hasOwnProperty(itemId)) {
                    if (this.itemQuantities[itemId] === null || this.itemQuantities[itemId] === '') {
                        return false;
                    }
                }
            }
            return true;
        }
    };

    /** Print*/
    $scope.print = function () {
        $timeout($window.print, 0);
    };

    /**
     * Selecting an item will display a table containing information for all shipments that contain that item.
     * Clicking on an already selected item will hide the table.
     */
    $scope.setSelected = function (item) {
        // Clicking an expanded item should collapse it.
        if ($scope.selectedItem == item) {
            $scope.selectedItem = null;
        }
        else {
            $scope.selectedItem = item;
        }
    };

    $scope.isItemSelected = function (item) {
        return $scope.selectedItem == item;
    };

    $scope.isReconciliationError = function (item) {
        return $scope.reconciliationStatus.resultErrorMap.get(item.id) != null;
    };

    $scope.getShipmentsWithItem = function (item) {
        return $scope.reconcilableItemMap[item.id];
    };

    $scope.getOrderedQuantity = function (shipment, item) {
        var lineItems = shipment.lineItems;
        for (var i = 0; i < lineItems.length; i++) {
            if (lineItems[i].item.id === item.id) {
                return lineItems[i].quantity;
            }
        }
    };

    $scope.viewShipment = function (shipment) {
        locationService.go("/supply/requisition/requisition-view", false, "requisition=" + shipment.requisitionId + "&fromPage=reconciliation");
    };

    $scope.setCurrentPage = function (page) {
        $scope.currentPage = page;
    };

    $scope.reconcile = function () {
        $scope.reconciliationStatus.attempted = true;
        $scope.reconciliationStatus.resetResults();
        if (!$scope.inventory.isComplete()) {
            return;
        }

        reconcileApi.save($scope.inventory).$promise
            .then(saveResults)
            .then(displayReconciliationResults)
            .catch($scope.handleErrorResponse);

        function saveResults(response) {
            $scope.reconciliationStatus.result = response.result;
            angular.forEach($scope.reconciliationStatus.result.errors, function(error) {
                $scope.reconciliationStatus.resultErrorMap.set(error.itemId, error);
            })
        }

        function displayReconciliationResults() {
            console.log($scope.reconciliationStatus.result);
            if ($scope.reconciliationStatus.result.success) {
                modals.open('reconciliation-success')
                    .then(reload);
            }
            else {
                modals.open('reconciliation-error');
            }
        }
    };

    function reload() {
        locationService.go("/supply/manage/reconciliation", true);
    }

    $scope.init = function () {
        var params = {
            status: "APPROVED",
            reconciled: 'false',
            from: moment.unix(1).format(),
            offset: 0,
            limit: 'ALL'
        };
        $scope.reconcilableSearch.response = requisitionApi.get(params, function (response) {
            $scope.reconcilableSearch.matches = response.result;
            $scope.reconcilableSearch.error = false;
            initReconcilableItems();
            initInventory();
        }, function (response) {
            $scope.reconcilableSearch.matches = [];
            $scope.reconcilableSearch.items = [];
            $scope.reconcilableSearch.error = true;
            $scope.handleErrorResponse(response);
        });
    };

    function initReconcilableItems() {
        angular.forEach($scope.reconcilableSearch.matches, function (shipment) {
            angular.forEach(shipment.lineItems, function (lineItem) {
                if ($scope.reconcilableItemMap.hasOwnProperty(lineItem.item.id)) {
                    $scope.reconcilableItemMap[lineItem.item.id].push(shipment);
                }
                else {
                    $scope.reconcilableItemMap[lineItem.item.id] = [];
                    $scope.reconcilableItemMap[lineItem.item.id].push(shipment);
                    $scope.reconcilableSearch.items.push(lineItem.item);
                }
            })
        });
        $scope.reconcilableSearch.items = supplyUtils.alphabetizeItemsByCommodityCode($scope.reconcilableSearch.items);
    }

    function initInventory() {
        angular.forEach($scope.reconcilableSearch.items, function (item) {
            $scope.inventory.itemQuantities[item.id] = null;
        });
    }

    $scope.init();
}
