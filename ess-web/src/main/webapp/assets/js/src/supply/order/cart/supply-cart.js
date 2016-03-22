var essSupply = angular.module('essSupply');

essSupply.service('SupplyCart', [function() {
    // TODO rename cartItem to lineitem
    function CartItem(item, quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    /** Array of CartItem's */
    var items = [];

    return {
        addToCart: function(item, quantity) {
            var cartItem = this.getItemById(item.id);
            if (!cartItem) {
                items.push(new CartItem(item, quantity));
            }
            else {
                cartItem.quantity += quantity;
            }
        },
        getItems: function() {
            return items;
        },
        getItemById: function(id) {
            var item = false;
            var search = $.grep(items, function(cartItem) {return cartItem.item.id === id});
            if (search.length > 0) {
                item = search[0];
            }
            return item;
        },
        getTotalItems: function() {
            var size = 0;
            angular.forEach(items, function(item) {
                size += item.quantity;
            });
            return size;
        },
        removeFromCart: function(item) {
            $.grep(items, function(cartItem, index) {
                if (cartItem && cartItem.item.id === item.id) {
                    items.splice(index, 1);
                }
            });
        },
        reset: function() {
            items = [];
        }

    }
}]);
