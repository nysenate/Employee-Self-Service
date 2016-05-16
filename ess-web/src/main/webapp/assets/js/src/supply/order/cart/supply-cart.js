var essSupply = angular.module('essSupply');

essSupply.service('SupplyCart', ['SupplyLocationAllowanceService', function (allowanceService) {

    function LineItem(item, quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    /** Array of LineItem's in the cart. */
    var lineItems = [];

    /** Add a new item to the cart, returns the quantity of the item added. */
    function addNewItemToCart(item, quantity) {
        lineItems.push(new LineItem(item, quantity));
        return quantity;
    }

    /** Add more of an item that is already in the cart. We need to verify this quantity is allowed. */
    function addQuantityToCartItem(lineItem, quantity) {
        var itemAllowance = allowanceService.getAllowanceByItemId(lineItem.item.id);
        var totalQuantity = lineItem.quantity + quantity;
        if (totalQuantity > itemAllowance.perOrderAllowance) {
            // Trying to order over the per order max. Set to max allowed but not higher.
            var maxAllowed = itemAllowance.perOrderAllowance - lineItem.quantity;
            lineItem.quantity = itemAllowance.perOrderAllowance;
            return maxAllowed;
        }
        lineItem.quantity += quantity;
        return quantity;
    }

    return {
        /** Add an item to the cart, return the quantity added. */
        addToCart: function (item, quantity) {
            return this.itemInCart(item.id) ? addQuantityToCartItem(this.getItemById(item.id), quantity) : addNewItemToCart(item, quantity);
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
