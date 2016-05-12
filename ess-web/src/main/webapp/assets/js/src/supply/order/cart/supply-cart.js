var essSupply = angular.module('essSupply');

essSupply.service('SupplyCart', [function () {

    function LineItem(item, allowance, quantity) {
        this.item = item;
        this.allowance = allowance;
        this.quantity = quantity;
    }

    /** Array of LineItem's in the cart. */
    var lineItems = [];

    return {
        // Add an item to the cart, return the quantity added.
        addToCart: function (item, allowance, quantity) {
            var quantityAdded = 0;
            if (!this.itemInCart(item.id)) {
                lineItems.push(new LineItem(item, allowance, quantity));
                quantityAdded = quantity;
            }
            else {
                // Item is already in the cart
                var lineItem = this.getItemById(item.id);
                var newQuantity = lineItem.quantity + allowance.selectedQuantity;
                if (newQuantity <= allowance.perOrderAllowance) {
                    lineItem.quantity += allowance.selectedQuantity;
                    quantityAdded = quantity;
                }
                else {
                    // Trying to order over the per order max. Set to max allowed but not higher.
                    quantityAdded = allowance.perOrderAllowance - lineItem.quantity;
                    lineItem.quantity = allowance.perOrderAllowance;
                }
            }
            return quantityAdded;
        },

        getItems: function () {
            return lineItems;
        },

        itemInCart: function (itemId) {
            var results = $.grep(lineItems, function (cartItem) {
                return cartItem.item.id === itemId
            });
            return results.length > 0;
        },

        /** Get an item in the cart by its id. returns null if no match is found. */
        getItemById: function (itemId) {
            var search = $.grep(lineItems, function (lineItem) {
                return lineItem.item.id === itemId
            });
            if (search.length > 0) {
                return search[0];
            }
            return null;
        },

        getTotalItems: function () {
            var size = 0;
            angular.forEach(lineItems, function (lineItem) {
                size += lineItem.quantity;
            });
            return size;
        },

        removeFromCart: function (item) {
            $.grep(lineItems, function (lineItem, index) {
                if (lineItem && lineItem.item.id === item.id) {
                    lineItems.splice(index, 1);
                }
            });
        },

        reset: function () {
            lineItems = [];
        }
    }
}]);
