var essSupply = angular.module('essSupply');

essSupply.service('SupplyCartService', ['SupplyLocationAllowanceService', 'SupplyCookieService', function (allowanceService, cookies) {

    /** The cart is map of item id's to line items, saved in the users cookies. */
    var cart = cookies.getCart() || new Map();

    function newQuantity(quantity, lineItem) {
        return lineItem ? lineItem.quantity + quantity : quantity;
    }

    return {
        /**
         * Add a line item to the cart.
         * The added line item is a copy so changes to the original don't effect the cart.
         * If this item is already in the cart, do nothing.
         * Returns the updated cart.
         */
        addToCart: function (lineItem) {
            if (this.isItemInCart(lineItem.item.id)) {
                return;
            }
            var li = angular.copy(lineItem);
            cart.set(li.item.id, li);
            return cart;
        },

        /**
         * Add or remove a line item from the cart.
         * Any added line item is copied first so changes to the original don't effect the cart.
         * If the line item's quantity is zero remove it, otherwise add it.
         * Return the updated cart object.
         */
        updateCartLineItem: function(lineItem) {
            var li = angular.copy(lineItem);
            if (li.quantity === 0) {
                cart.delete(li.item.id);
            }
            else {
                cart.set(li.item.id, li);
            }
            return cart;
        },

        getCart: function () {
            return cart;
        },

        isItemInCart: function (itemId) {
            var item = cart.get(itemId);
            return item != undefined;
        },

        /** Get an item in the cart by its id. returns null if no match is found. */
        getCartLineItem: function (itemId) {
            if (!this.isItemInCart(itemId)) {
                return null;
            }
            return cart.get(itemId);
        },

        getTotalItems: function () {
            var size = 0;
            angular.forEach(cart, function (lineItem) {
                size += lineItem.quantity || 0;
            });
            return size;
        },

        removeFromCart: function (itemId) {
            $.grep(cart, function (lineItem, index) {
                if (lineItem && lineItem.item.id === itemId) {
                    cart.splice(index, 1);
                }
            });
            cookies.saveCartCookie(cart);
        },

        reset: function () {
            cart = [];
            cookies.saveCartCookie(cart);
        },

        save: function () {
            // cookies.saveCartCookie(cart);
            // TODO:
        }
    }
}]);
