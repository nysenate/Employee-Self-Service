var essSupply = angular.module('essSupply');

essSupply.service('SupplyCategoryService', ['$rootScope', 'SupplyItemsApi',
    function($rootScope, supplyItemsApi) {

    function Category(name) {
        this.name = name;
        this.selected = false;
    }

    var categories = [];

    function initCategories(items) {
        angular.forEach(items, function(item) {
            if (isDistinctCategory(item.category)) {
                categories.push(new Category(item.category));
            }
        });
    }

    /** Returns true if a category is not yet in the category array, false otherwise. */
    function isDistinctCategory(name) {
        var cats = $.grep(categories, function(cat){ return cat.name === name; });
        return !cats.length > 0;
    }

    var promise = supplyItemsApi.get(function(response) {
        initCategories(response.result);
    });

    return {
        promise: promise,

        getCategories: function() {
            return categories;
        },
        getSelectedCategoryNames: function() {
            var selected = [];
            angular.forEach(categories, function(cat) {
                if (cat.selected === true) {
                    selected.push(cat.name);
                }
            });
            return selected;
        }
    }
}]);