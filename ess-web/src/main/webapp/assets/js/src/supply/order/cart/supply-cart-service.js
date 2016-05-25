var essSupply = angular.module('essSupply');

essSupply.service('SupplyCartService', ['SupplyLocationAllowanceService', function (allowanceService) {

    function LineItem(item, quantity) {
        this.item = item;
        this.quantity = quantity;
        this.isSpecialRequest = undefined;
    }

    /** Array of LineItem's in the cart. */
    var lineItems = [];

    function calculateNewQuantity(quantity, lineItem) {
        return lineItem ? lineItem.quantity + quantity : quantity;
    }

    return {
        isOverOrderAllowance: function (item, quantity) {
            var allowance = allowanceService.getAllowanceByItemId(item.id);
            if (calculateNewQuantity(quantity, this.getItemById(item.id)) > allowance.perOrderAllowance) {
                return true;
            }
            return false;
        },

        isOverMonthlyAllowance: function (item, quantity) {
            var allowance = allowanceService.getAllowanceByItemId(item.id);
            if (calculateNewQuantity(quantity, this.getItemById(item.id)) > allowance.remainingMonthlyAllowance) {
                return true;
            }
            return false;
        },

        addToCart: function (item, quantity, isSpecialRequest) {
            if (this.itemInCart(item.id)) {
                this.getItemById(item.id).quantity += quantity;
            }
            else {
                lineItems.push(new LineItem(item, quantity));
            }
            if (isSpecialRequest) {
                this.getItemById(item.id).isSpecialRequest = true;
            }
            return true;
        },

        getItems: function () {
            return lineItems;
        },

        itemInCart: function (itemId) {
            var results = $.grep(lineItems, function (lineItem) {
                return lineItem.item.id === itemId
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

        removeFromCart: function (itemId) {
            $.grep(lineItems, function (lineItem, index) {
                if (lineItem && lineItem.item.id === itemId) {
                    lineItems.splice(index, 1);
                }
            });
        },

        reset: function () {
            lineItems = [];
        }
    }
}]);
