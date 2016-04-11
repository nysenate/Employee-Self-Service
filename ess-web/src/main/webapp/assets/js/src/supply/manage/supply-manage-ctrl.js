essSupply = angular.module('essSupply').controller('SupplyManageController', ['$scope', 'SupplyInventoryService',
    'SupplyShipmentsApi', 'modals', '$interval', supplyManageController]);

function supplyManageController($scope, supplyInventoryService, supplyShipmentsApi, modals, $interval) {

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
    
    $scope.selected = null;

    $scope.init = function() {
        getPendingShipments();
        getProcessingShipments();
        getCompletedShipments();
    };

    $scope.init();

    // Refresh data every minute.
    var intervalPromise = $interval(function() {$scope.init()}, 60000);
    // Stop refreshing when we leave this page.
    $scope.$on('$destroy', function () {$interval.cancel(intervalPromise)});
    
    /** 
     * --- Api Calls ---
     */

    /** Get all pending shipments */
    function getPendingShipments() {
        var params = {
            status: "PENDING",
            from: moment.unix(1).format()
        };
        $scope.pendingSearch.response = supplyShipmentsApi.get(params, function(response) {
            $scope.pendingSearch.matches = response.result;
            $scope.pendingSearch.error = false;
        }, function(errorResponse) {
            $scope.pendingSearch.matches = [];
            $scope.pendingSearch.error = true;
        })
    }

    function getProcessingShipments() {
        var params = {
            status: "PROCESSING",
            from: moment.unix(1).format()
        };
        $scope.processingSearch.response = supplyShipmentsApi.get(params, function(response) {
            $scope.processingSearch.matches = response.result;
            $scope.processingSearch.error = false;
        }, function(errorResponse) {
            $scope.processingSearch.matches = [];
            $scope.processingSearch.error = true;
        })
    }

    function getCompletedShipments() {
        var params = {
            status: "COMPLETED",
            from: moment().startOf('day').format()
        };
        $scope.completedSearch.response = supplyShipmentsApi.get(params, function(response) {
            $scope.completedSearch.matches = response.result;
            $scope.completedSearch.error = false;
        }, function(errorResponse) {
            $scope.completedSearch.matches = [];
            $scope.completedSearch.error = true;
        })
    }

    /** --- Util methods --- */

    /* Return the number of distinct items in an shipments order */
    $scope.getOrderQuantity = function(shipment) {
        var size = 0;
        angular.forEach(shipment.order.activeVersion.lineItems, function(item) {
            size++;
        });
        return size;
    };

    $scope.getItemCommodityCode = function(itemId) {
        var item = supplyInventoryService.getItemById(itemId);
        return item.commodityCode;
    };

    $scope.getItemName = function(itemId) {
        var item = supplyInventoryService.getItemById(itemId);
        return item.name;
    };

    /** --- Highlighting --- */

    $scope.highlightShipment = function(order) {
        var highlight = false;
        angular.forEach(order.items, function(lineItem) {
            var item = supplyInventoryService.getItemById(lineItem.itemId);
            if (lineItem.quantity > item.suggestedMaxQty) {
                highlight = true;
            }
        });
        return highlight;
    };

    $scope.highlightLineItem = function(lineItem) {
        var item = supplyInventoryService.getItemById(lineItem.itemId);
        return lineItem.quantity > item.suggestedMaxQty
    };

    /** --- Modals --- */

    $scope.showEditingDetails = function(shipment) {
        console.log(shipment);
        modals.open('manage-editing-modal', shipment);
    };

    $scope.showCompletedDetails = function(shipment) {
        console.log(shipment);
        modals.open('manage-completed-modal', shipment);
    };
}