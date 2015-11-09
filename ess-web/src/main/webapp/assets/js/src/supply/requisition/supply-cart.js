var essSupply = angular.module('essSupply');

essSupply.service('supplyCart', [function() {
    function CartItem(product, quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    var items = [];

    return {
        addToCart: function(product, quantity) {
            // TODO: if already in cart, add to quantity
            console.log("Adding " + quantity + " " + product.name + " to cart.");
            items.push(new CartItem(product, quantity));
            console.log("There are " + this.getSize() + " items in your cart.");
        },
        getItems: function() {
            return items;
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
