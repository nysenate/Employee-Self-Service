var essSupply = angular.module('essSupply');

essSupply.service('SupplyInventoryService', ['SupplyItemApi', function(supplyItemApi) {

    // Canonical source of available items.
    var items = [];

    var promise = false;
    supplyItemApi.get(function(response) {
        items = response.result;
        console.log("inventory service items : " + items);
        promise = true;
    });

    return {
        promise:promise,

        /** Return copy of items so this.items never gets altered by a 3rd party. */
        getCopyOfItems: function() {
            return angular.copy(items);
        },

        /** Return an array of all int's from 1 to item's max quantity.
         * This represents allowable order quantities. */
        orderQuantityRange: function(item) {
            var range = [];
            for (var i = 1; i <= item.suggestedMaxQty * 2; i++) {
                range.push(i);
            }
            return range;
        },

        getItemById: function(id) {
            var item = false;
            var search = $.grep(items, function(prod){ return prod.id === id; });
            if (search.length > 0) {
                item = search[0];
            }
            return item;
        }

    }
}]);
