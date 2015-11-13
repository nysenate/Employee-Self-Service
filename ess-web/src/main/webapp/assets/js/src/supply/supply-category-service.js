var essSupply = angular.module('essSupply');

essSupply.service('SupplyCategoryService', ['$rootScope', 'SupplyInventoryService', function($rootScope, SupplyInventoryService) {

    function Category(id, name) {
        this.id = id;
        this.name = name;
        this.selected = false;
    }

    var categories = [];

    function initCategories() {
        var products = SupplyInventoryService.getCopyOfProducts();
        angular.forEach(products, function(product) {
            if (!findById(product.categoryId)) {
                categories.push(new Category(product.categoryId, product.categoryName));
            }
        });
    }

    /** Searches for and returns a category by id. If no category found, returns false. */
    function findById(id) {
        var cats = $.grep(categories, function(cat){ return cat.id === id; });
        if (cats.length > 0) {
            return cats[0];
        }
        return false;
    }

    return {
        getCategories: function() {
            if (categories.length === 0) {
                initCategories();
            }
            return categories;
        },
        getSelectedCategoryIds: function() {
            var selected = [];
            angular.forEach(categories, function(cat) {
                if (cat.selected === true) {
                    selected.push(cat.id);
                }
            });
            return selected;
        }
    }
}]);