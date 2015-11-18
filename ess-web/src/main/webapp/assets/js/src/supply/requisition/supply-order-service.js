var essSupply = angular.module('essSupply');

// Most of this is temporary until a real backend gets created.
essSupply.service('SupplyOrderService', [function() {
    function SupplyOrder(id, locCode, locType, dateTime, purchaser, items) {
        this.id = id;
        this.locCode = locCode;
        this.locType = locType;
        this.dateTime = dateTime;
        this.purchaser = purchaser;
        this.items = items;
    }

    function Item(productId, quantity) {
        this.id = productId;
        this.quantity = quantity;
    }

    var pendingOrders = [];
    var inProcessOrders = [];
    var completedOrders = [];

    function initPendingOrders() {
        pendingOrders.push(new SupplyOrder(10, 'D56004', 'W', moment("2015-11-17 07:54"), 'CASEIRAS',
            [new Item(1, 1), new Item(4, 1), new Item(9, 2)]));
        pendingOrders.push(new SupplyOrder(11, 'CM330', 'W', moment("2015-11-17 09:23"), 'SMITH',
            [new Item(7, 4), new Item(6, 4), new Item(8, 4)]))
    }

    return {
        getPendingOrders: function() {
            if (pendingOrders.length === 0) {
                initPendingOrders();
            }
            return pendingOrders;
        },
        getOrderById: function(id) {
            var allOrders = [];
            allOrders.push.apply(allOrders, pendingOrders, inProcessOrders, completedOrders);
            var search = $.grep(allOrders, function(order){ return order.id === id; });
            if (search.length > 0) {
                return search[0];
            }
        }
    }
}]);