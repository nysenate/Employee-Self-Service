var essSupply = angular.module('essSupply');

essSupply.service('SupplyCartService', ['SupplyLocationAllowanceService', 'SupplyCookieService', function (allowanceService, cookies) {

    function LineItem(item, quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    /** The cart is an array of LineItem's, saved in the users cookies. */
    var cart = cookies.getCart();

    function newQuantity(quantity, lineItem) {
        return lineItem ? lineItem.quantity + quantity : quantity;
    }

    return {
        isOverOrderAllowance: function (item, quantity) {
            if (newQuantity(quantity, this.getCartLineItem(item.id)) > item.maxQtyPerOrder) {
                return true;
            }
            return false;
        },

        /**
         * Add a new item to the cart.
         * Does nothing if item is already in the cart.
         */
        addToCart: function (item) {
            if (this.isItemInCart(item.id)) {
                return;
            }
            cart.push(new LineItem(item, 1));
            cookies.saveCartCookie(cart);
            return true;
        },

        /**
         * Reduces the quantity of item by one.
         * Removes from cart if the new quantity is < 1.
         */
        decrementQuantity: function (item) {
            var lineItem = this.getCartLineItem(item.id);
            if (lineItem) {
                lineItem.quantity--;
                if (lineItem.quantity < 1) {
                    this.removeFromCart(item.id)
                }
            }
            cookies.saveCartCookie(cart);
        },

        /**
         * Increments the quantity of the given item in the cart by one.
         * Max item quantity is 9999.
         */
        incrementQuantity: function (item) {
            var lineItem = this.getCartLineItem(item.id);
            if (lineItem) {
                if (lineItem.quantity < 9999) {
                    lineItem.quantity++;
                }
            }
            cookies.saveCartCookie(cart);
        },

        getCart: function () {
            return cart;
        },

        isItemInCart: function (itemId) {
            var results = $.grep(cart, function (lineItem) {
                return lineItem.item.id === itemId
            });
            return results.length > 0;
        },

        /** Get an item in the cart by its id. returns null if no match is found. */
        getCartLineItem: function (itemId) {
            var search = $.grep(cart, function (lineItem) {
                return lineItem.item.id === itemId
            });
            if (search.length > 0) {
                return search[0];
            }
            return null;
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
        }
    }
}]);
