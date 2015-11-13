var essSupply = angular.module('essSupply').controller('SupplyOrderController',
    ['$scope', 'appProps', 'modals', 'SupplyInventoryService', 'SupplyCategoryService',
        'SupplyCart', supplyOrderController]);

function supplyOrderController($scope, appProps, modals, SupplyInventoryService,
                               SupplyCategoryService, SupplyCart) {

    $scope.quantity = 1;

    /** ----- Controller Logic ----- */

    $scope.init = function() {
        //$scope.products = angular.extend({}, productsModel);
        $scope.products = SupplyInventoryService.getCopyOfProducts();
    };

    $scope.addToCart = function(product, qty) {
        SupplyCart.addToCart(product, qty);
    };

    $scope.isInCart = function(product) {
        return SupplyCart.getItemById(product.id) !== false
    };

    /** Return an array with range from 1 to products max quantity */
    $scope.orderSizeRange = function(product) {
        return Array.apply(null, Array(product.maxQuantity)).map(function (_, i) {return i + 1;})
    };

    /**
     * Called whenever a category is selected/deselected.
     * Updates displayed products to only display products belonging to selected category.
     * If no categories are selected, display all products.
     */
    $scope.filterByCategories = function() {
        $scope.products = [];
        angular.forEach($scope.categories, function(category) {
            if (category.selected === true) {
                angular.forEach(productsModel, function(product) {
                    if (category.id === product.categoryId) {
                        $scope.products.push(product);
                    }
                });
            }
        });
        // If no category selected, display all.
        if ($scope.products.length === 0) {
            $scope.products = angular.extend({}, productsModel);
        }
    };

    $scope.init();
}
