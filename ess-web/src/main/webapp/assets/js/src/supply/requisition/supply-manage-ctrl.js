essSupply = angular.module('essSupply').controller('SupplyManageController', ['$scope', 'SupplyInventoryService',
    'SupplyGetPendingOrdersApi', 'SupplyGetProcessingOrdersApi', 'SupplyGetTodaysCompletedOrdersApi',
    'SupplyCompleteOrderApi', 'modals', supplyManageController]);

function supplyManageController($scope, supplyInventoryService, getPendingOrdersApi, getProcessingOrdersApi,
                                getTodaysCompletedOrdersApi, completeOrderApi, modals) {

    $scope.selected = null;
    $scope.pendingOrders = null;
    $scope.processingOrders = null;
    $scope.completedOrders = null;

    $scope.init = function() {
        getPendingOrders();
        getProcessingOrders();
        getCompletedOrders();
    };

    $scope.init();

    /** --- Api Calls --- */

    function getPendingOrders() {
        getPendingOrdersApi.get(null, function(response) {
            $scope.pendingOrders = response.result;
        }, function(response) {
            // TODO error
        })
    }

    function getProcessingOrders() {
        getProcessingOrdersApi.get(null, function(response) {
            $scope.processingOrders = response.result;
        }, function(response) {

        })
    }

    function getCompletedOrders() {
        getTodaysCompletedOrdersApi.get(null, function(response) {
            $scope.completedOrders = response.result;
        }, function(response) {

        })
    }

    /** --- Util methods --- */

    /* Return the number of distinct items in an order */
    $scope.getOrderQuantity = function(supplyOrder) {
        var size = 0;
        angular.forEach(supplyOrder.items, function(item) {
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

    $scope.highlightOrder = function(order) {
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

    $scope.showEditingDetails = function(order) {
        modals.open('manage-editing-modal', order);
    };

    $scope.showCompletedDetails = function(order) {
        modals.open('manage-completed-modal', order);
    };
}