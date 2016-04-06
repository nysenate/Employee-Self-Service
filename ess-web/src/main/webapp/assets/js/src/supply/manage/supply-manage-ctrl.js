essSupply = angular.module('essSupply').controller('SupplyManageController', ['$scope', 'SupplyInventoryService',
    'SupplyShipmentsApi', 'modals', '$interval', supplyManageController]);

function supplyManageController($scope, supplyInventoryService, supplyShipmentsApi, modals, $interval) {

    $scope.selected = null;
    $scope.pendingShipments = null;
    $scope.processingShipments = null;
    $scope.completedShipments = null;

    $scope.init = function() {
        getPendingOrders();
        getProcessingOrders();
        getCompletedOrders();
    };

    $scope.init();

    // Refresh data every minute.
    var intervalPromise = $interval(function() {$scope.init()}, 60000);
    // Stop refreshing when we leave this page.
    $scope.$on('$destroy', function () {$interval.cancel(intervalPromise)});
    
    /** --- Api Calls --- */

    function getPendingOrders() {
        var params = {
            status: "PENDING",
            from: moment.unix(1).format(),
            to: moment().add(1, 'day').format() // Use time in the future, current time may be slightly behind server time.
        };
        supplyShipmentsApi.get(params, function(response) {
            $scope.pendingShipments = response.result;
        }, function(response) {
            // TODO error
        })
    }

    function getProcessingOrders() {
        var params = {
            status: "PROCESSING",
            from: moment.unix(1).format(),
            to: moment().add(1, 'day').format()
        };
        supplyShipmentsApi.get(params, function(response) {
            $scope.processingShipments = response.result;
        }, function(response) {

        })
    }

    function getCompletedOrders() {
        var params = {
            status: "COMPLETED",
            from: moment().startOf('day').format(),
            to: moment().add(1, 'day').format()
        };
        supplyShipmentsApi.get(params, function(response) {
            $scope.completedShipments = response.result;
        }, function(response) {

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