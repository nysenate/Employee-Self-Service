var essSupply = angular.module('essSupply');

essSupply.service('SupplyCart', [function() {
    
    function LineItem(item, quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    /** Array of LineItem's in the cart. */
    var lineItems = [];

    return {
        addToCart: function(item, quantity) {
            if (!this.itemInCart(item.id)) {
                lineItems.push(new LineItem(item, quantity));
            }
            else {
                var lineItem = this.getItemById(item.id);
                lineItem.quantity += quantity;
            }
        },
        
        getItems: function() {
            return lineItems;
        },
        
        itemInCart: function(itemId) {
            var results = $.grep(lineItems, function(cartItem) {return cartItem.item.id === itemId});
            return results.length > 0;
        },
        
        /** Get an item in the cart by its id. returns null if no match is found. */
        getItemById: function(itemId) {
            var search = $.grep(lineItems, function(cartItem) {return cartItem.item.id === itemId});
            if (search.length > 0) {
                return search[0];
            }
            return null;
        },
        
        getTotalItems: function() {
            var size = 0;
            angular.forEach(lineItems, function(item) {
                size += item.quantity;
            });
            return size;
        },
        
        removeFromCart: function(item) {
            $.grep(lineItems, function(lineItem, index) {
                if (lineItem && lineItem.item.id === item.id) {
                    lineItems.splice(index, 1);
                }
            });
        },
        
        reset: function() {
            lineItems = [];
        }
    }
}]);
