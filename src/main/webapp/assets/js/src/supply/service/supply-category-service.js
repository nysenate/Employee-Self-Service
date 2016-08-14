var essSupply = angular.module('essSupply');

essSupply.service('SupplyCategoryService', ['SupplyLocationAllowanceService', 'SupplyOrderDestinationService', function (locationAllowanceService, destinationService) {

    function Category(name) {
        this.name = name;
        this.selected = false;
    }

    var categories = [];

    /** Returns true if a category is not yet in the category array, false otherwise. */
    function isDistinctCategory(name) {
        var cats = $.grep(categories, function (cat) {
            return cat.name === name;
        });
        return !cats.length > 0;
    }

    var initCategories = function () {
        // Get allowances that should be made into categories.
        var allowances = locationAllowanceService.getAllowances();
        if (allowances === null) {
            return;
        }
        // Create categories
        angular.forEach(allowances, function (allowance) {
            if (isDistinctCategory(allowance.item.category.name)) {
                categories.push(new Category(allowance.item.category.name));
            }
        });

        // Alphabetize the categories.
        categories.sort(function (a, b) {
            if (a.name < b.name) {
                return -1;
            }
            if (a.name > b.name) {
                return 1;
            }
            return 0;
        });
    };

    return {
        getCategories: function () {
            if (categories === null || categories.length === 0) {
                initCategories();
            }
            return categories;
        },

        getSelectedCategoryNames: function () {
            var selected = [];
            angular.forEach(categories, function (cat) {
                if (cat.selected === true) {
                    selected.push(cat.name);
                }
            });
            return selected;
        }
    }
}]);