var essSupply = angular.module('essSupply');

essSupply.service('SupplyCategoryService', ['$rootScope', 'SupplyItemsApi', function($rootScope, supplyItemsApi) {

    function Category(name) {
        this.name = name;
        this.selected = false;
    }

    var categories = [];

    /** Returns true if a category is not yet in the category array, false otherwise. */
    function isDistinctCategory(name) {
        var cats = $.grep(categories, function(cat){ return cat.name === name; });
        return !cats.length > 0;
    }
    
    var initCategories = function () {
        supplyItemsApi.get(function (response) {
            var items = response.result;
            angular.forEach(items, function(item) {
                if (isDistinctCategory(item.category.name)) {
                    categories.push(new Category(item.category.name));
                }
            });

            // Alphabetize the categories.
            categories.sort(function(a, b){
                if(a.name < b.name) {return -1;}
                if(a.name > b.name) {return 1;}
                return 0;
            });
        });
    };

    return {
        getCategories: function() {
            if (categories.length === 0) {
                initCategories();
            }
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