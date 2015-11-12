var essSupply = angular.module('essSupply');

essSupply.service('SupplyCategoryService', ['SupplyInventoryService', function(supplyInventoryService) {

    function Category(id, name) {
        this.id = id;
        this.name = name;
        this.selected = false;
    }

    var categories = [];

    function initCategories() {
        var products = supplyInventoryService.getProducts();
        angular.forEach(products, function(product) {
            if ($.inArray(product, categories) ===  -1) {
                categories.push(new Category(product.categoryId, product.categoryName));
            }
        });
    }

    function findById(id) {
        // TODO: handle errors if a bad call is made.
        return $.grep(categories, function(cat){ return cat.id === id; })[0];
    }

    return {
        getCategories: function() {
            if (categories.length === 0) {
                initCategories();
            }
            return categories;
        },
        markAsSelected: function(id) {
            var cat = findById(id);
            cat.selected = true;
        },
        removeSelection: function(id) {
            var cat = findById(id);
            cat.selected = false;
        },
        getSelected: function() {
            var selected = [];
            angular.forEach(categories, function(cat) {
                if (cat.selected === true) {
                    selected.push(cat);
                }
            });
            return selected;
        }

    }
}]);