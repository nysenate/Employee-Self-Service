var essSupply = angular.module('essSupply');

essSupply.service('supplyCart', [function() {
    function CartItem(product, quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    var items = [];

    return {
        addToCart: function(product, quantity) {
            var cartItem = this.getItemById(product.id);
            if (!cartItem) {
                items.push(new CartItem(product, quantity));
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
            var search = $.grep(items, function(cartItem) {return cartItem.product.id === id});
            if (search.length > 0) {
                item = search[0];
            }
            return item;
        },
        getSize: function() {
            var size = 0;
            angular.forEach(items, function(item) {
                size += item.quantity;
            });
            return size;
        }
    }
}]);
