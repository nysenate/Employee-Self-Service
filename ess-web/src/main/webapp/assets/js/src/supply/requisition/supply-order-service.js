var essSupply = angular.module('essSupply');

// Most of this is temporary until a real backend gets created.
essSupply.service('SupplyOrderService', ['SupplyInventoryService', function(supplyInventoryService) {
    function SupplyOrder(id, locCode, locType, dateTime, purchaser, items) {
        this.id = id;
        this.locCode = locCode;
        this.locType = locType;
        this.dateTime = dateTime;
        this.purchaser = purchaser;
        this.items = items;
    }

    function Item(productId, quantity) {
        this.product = supplyInventoryService.getProductById(productId);
        this.quantity = quantity;
    }

    var pendingOrders = [];
    var inprocessOrders = [];
    var completedOrders = [];

    function initPendingOrders() {
        pendingOrders.push(new SupplyOrder(10, 'D56004', 'W', moment("2015-11-17 07:54"), 'CASEIRAS',
            [new Item(1, 1), new Item(4, 1), new Item(9, 2)]));
        pendingOrders.push(new SupplyOrder(11, 'CM330', 'W', moment("2015-11-17 09:23"), 'SMITH',
            [new Item(7, 4), new Item(6, 4), new Item(8, 4)]));
        pendingOrders.push(new SupplyOrder(12, 'A42FB', 'W', moment("2015-11-17 13:11"), 'JOHNSON',
            [new Item(2, 2), new Item(3, 1), new Item(4, 1), new Item(6, 1), new Item(7, 2), new Item(8, 4), new Item(9, 12),
                new Item(10, 5), new Item(12, 4), new Item(11, 2), new Item(1, 1), new Item(5, 2)]));
    }

    function initInprocessOrders() {
         inprocessOrders.push(new SupplyOrder(9, 'LC144', 'W', moment("2015-11-16 15:41"), 'STEVEN',
            [new Item(2, 2), new Item(3, 1), new Item(6, 1), new Item(7, 2), new Item(11, 2), new Item(1, 1), new Item(5, 2)]));
         inprocessOrders.push(new SupplyOrder(8, 'D5001', 'W', moment("2015-11-16 16:04"), 'MATT',
            [new Item(2, 2), new Item(3, 1), new Item(4, 1), new Item(6, 1), new Item(7, 2)]));
    }

    function initCompletedOrders() {
        completedOrders.push(new SupplyOrder(1, 'D8001', 'W', moment("2015-11-14 15:34"), 'CASEIRAS',
            [new Item(2, 1), new Item(3, 1), new Item(6, 1), new Item(7, 2),
                new Item(10, 2), new Item(12, 4), new Item(11, 2), new Item(1, 1), new Item(5, 2)]));
        completedOrders.push(new SupplyOrder(2, 'CL100', 'W', moment("2015-11-14 10:34"), 'GEORGE',
            [new Item(2, 2), new Item(3, 1), new Item(4, 1), new Item(6, 1), new Item(7, 2)]));
    }

    return {
        getPendingOrders: function() {
            if (pendingOrders.length === 0) {
                initPendingOrders();
            }
            return pendingOrders;
        },
        getInprocessOrders: function() {
            if (inprocessOrders.length === 0) {
                initInprocessOrders();
            }
            return inprocessOrders;
        },
        getCompletedOrders: function() {
            if (completedOrders.length === 0) {
                initCompletedOrders();
            }
            return completedOrders;
        },
        getOrderById: function(id) {
            var allOrders = [];
            allOrders.push.apply(allOrders, this.getPendingOrders(), this.getInprocessOrders(), this.getCompletedOrders());
            var search = $.grep(allOrders, function(order){ return order.id == id; });
            if (search.length > 0) {
                return search[0];
            }
        }
    }
}]);