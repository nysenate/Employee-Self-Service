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

        addToCart: function (item, quantity, special) {
            if (this.isItemInCart(item.id)) {
                this.getCartLineItem(item.id).quantity += quantity;
            }
            else {
                cart.push(new LineItem(item, quantity));
            }
            cookies.addCart(cart);
            return true;
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
                size += lineItem.quantity;
            });
            return size;
        },

        removeFromCart: function (itemId) {
            $.grep(cart, function (lineItem, index) {
                if (lineItem && lineItem.item.id === itemId) {
                    cart.splice(index, 1);
                }
            });
            cookies.addCart(cart);
        },

        reset: function () {
            cart = [];
            cookies.addCart(cart);
        }
    }
}]);
