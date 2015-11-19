var essSupply = angular.module('essSupply');

// Most of this is temporary until a real backend gets created.
essSupply.service('SupplyOrderService', ['SupplyInventoryService', function(supplyInventoryService) {

    var status = {
        PENDING: 1,
        INPROCESS: 2,
        COMPLETED: 3
    };

    function SupplyOrder(id, locCode, locType, dateTime, purchaser, items, status) {
        this.id = id;
        this.locCode = locCode;
        this.locType = locType;
        this.dateTime = dateTime;
        this.purchaser = purchaser;
        this.items = items;
        this.status = status
    }

    function Item(productId, quantity) {
        this.product = supplyInventoryService.getProductById(productId);
        this.quantity = quantity;
    }

    var orders = [];

    function initOrders() {
        if (orders.length === 0) {
            orders.push(new SupplyOrder(10, 'D56004', 'W', moment("2015-11-17 13:54"), 'CASEIRAS',
                [new Item(1, 1), new Item(4, 1), new Item(9, 2)], status.PENDING));
            orders.push(new SupplyOrder(11, 'CM330', 'W', moment("2015-11-17 12:43"), 'SMITH',
                [new Item(7, 4), new Item(6, 4), new Item(8, 4)], status.PENDING));
            orders.push(new SupplyOrder(12, 'A42FB', 'W', moment("2015-11-17 11:11"), 'JOHNSON',
                [new Item(2, 2), new Item(3, 1), new Item(4, 1), new Item(6, 1), new Item(7, 2), new Item(8, 4), new Item(9, 12),
                    new Item(10, 5), new Item(12, 4), new Item(11, 2), new Item(1, 1), new Item(5, 2)], status.INPROCESS));
            orders.push(new SupplyOrder(9, 'LC144', 'W', moment("2015-11-16 15:41"), 'STEVEN',
                [new Item(2, 2), new Item(3, 1), new Item(6, 1), new Item(7, 2), new Item(11, 2), new Item(1, 1), new Item(5, 2)], status.COMPLETED));
            orders.push(new SupplyOrder(8, 'D5001', 'W', moment("2015-11-16 16:04"), 'MATT',
                [new Item(2, 2), new Item(3, 1), new Item(4, 1), new Item(6, 1), new Item(7, 2)], status.COMPLETED));
            orders.push(new SupplyOrder(1, 'D8001', 'W', moment("2015-11-14 15:34"), 'CASEIRAS',
                [new Item(2, 1), new Item(3, 1), new Item(6, 1), new Item(7, 2),
                    new Item(10, 2), new Item(12, 4), new Item(11, 2), new Item(1, 1), new Item(5, 2)], status.COMPLETED));
            orders.push(new SupplyOrder(2, 'CL100', 'W', moment("2015-11-14 10:34"), 'GEORGE',
                [new Item(2, 2), new Item(3, 1), new Item(4, 1), new Item(6, 1), new Item(7, 2)], status.COMPLETED));
            orders.push(new SupplyOrder(3, 'D9011', 'W', moment("2015-11-17 10:58"), 'BRENCHEN',
                [new Item(2, 2), new Item(3, 1), new Item(4, 1), new Item(6, 1), new Item(7, 2)], status.COMPLETED));
            orders.push(new SupplyOrder(4, 'D4501', 'W', moment("2015-11-17 09:04"), 'STEVEN',
                [new Item(6, 1), new Item(7, 2)], status.COMPLETED));
            orders.push(new SupplyOrder(5, 'A9889', 'W', moment("2015-11-17 08:32"), 'SHEILA',
                [new Item(2, 2), new Item(3, 1), new Item(4, 1), new Item(7, 2)], status.COMPLETED));
        }
    }

    return {
        getPendingOrders: function() {
            var pending = [];
            initOrders();
            angular.forEach(orders, function(order) {
                if (order.status === status.PENDING) {
                    pending.push(order);
                }
            });
            return pending;
        },
        getInprocessOrders: function() {
            var inprocess = [];
            initOrders();
            angular.forEach(orders, function(order) {
                if (order.status === status.INPROCESS) {
                    inprocess.push(order);
                }
            });
            return inprocess;
        },
        getTodaysCompletedOrders: function() {
            var todaysCompleted = [];
            var startOfToday = moment("2015-11-17").hour(0).minute(0).second(0);
            initOrders();
            angular.forEach(orders, function(order) {
                if (order.status === status.COMPLETED && order.dateTime.isAfter(startOfToday)) {
                    todaysCompleted.push(order);
                }
            });
            return todaysCompleted;
        },
        getHistoricalOrders: function() {
            var historical = [];
            initOrders();
            angular.forEach(orders, function(order) {
                if (order.status === status.COMPLETED) {
                    historical.push(order);
                }
            });
            return historical;
        },
        getOrderById: function(id) {
            initOrders();
            var search = $.grep(orders, function(order){ return order.id == id; });
            if (search.length > 0) {
                return search[0];
            }
        }
    }
}]);