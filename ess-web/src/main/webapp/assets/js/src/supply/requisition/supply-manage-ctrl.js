essSupply = angular.module('essSupply').controller('SupplyManageController', ['$scope', 'appProps', 'SupplyInventoryService',
    'SupplyGetPendingOrdersApi', 'SupplyGetProcessingOrdersApi', 'SupplyGetTodaysCompletedOrdersApi', 'SupplyProcessOrderApi',
    'SupplyCompleteOrderApi', 'SupplyRejectOrderApi', supplyManageController]);

function supplyManageController($scope, appProps, supplyInventoryService, getPendingOrdersApi,
                                getProcessingOrdersApi, getTodaysCompletedOrdersApi, processOrderApi,
                                completeOrderApi, rejectOrderApi) {

    $scope.selected = null;
    $scope.pendingOrders = null;
    $scope.processingOrders = null;
    $scope.completedOrders = null;

    $scope.init = function() {
        getPendingOrders();
        getProcessingOrders();
        getCompletedOrders();
    };

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

    $scope.getOrderQuantity = function(supplyOrder) {
        var size = 0;
        angular.forEach(supplyOrder.items, function(item) {
            size++;
        });
        return size;
    };

    $scope.processOrder = function(order) {
        order.issuingEmployee.employeeId = appProps.user.employeeId;
        processOrderApi.save(order);
        $scope.selected = null;
    };

    $scope.completeOrder = function(order) {
        completeOrderApi.save(order);
        $scope.selected = null;
    };

    $scope.rejectOrder = function(order) {
        rejectOrderApi.save(appProps.user.employeeId, order.id);
        $scope.selected = null;
    };

    $scope.getItemCommodityCode = function(itemId) {
        var item = supplyInventoryService.getItemById(itemId);
        return item.commodityCode;
    };

    $scope.getItemName = function(itemId) {
        var item = supplyInventoryService.getItemById(itemId);
        return item.name;
    };

    $scope.getItemUnitSize = function(itemId) {
        var item = supplyInventoryService.getItemById(itemId);
        return item.unitSize;
    };

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

    $scope.init();


    $scope.highlightLineItem = function(lineItem) {
        var item = supplyInventoryService.getItemById(lineItem.itemId);
        return lineItem.quantity > item.suggestedMaxQty
    };

    $scope.setSelected = function(order) {
        if ($scope.selected && order.id === $scope.selected.id) {
            $scope.selected = null;
        }
        else {
            $scope.selected = order;
        }
    };

    $scope.selectedOrder = function(order) {
        return $scope.selected && order.id === $scope.selected.id;
    };
}