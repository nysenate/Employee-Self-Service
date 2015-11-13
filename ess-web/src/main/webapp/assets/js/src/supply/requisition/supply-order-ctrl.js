var essSupply = angular.module('essSupply').controller('SupplyOrderController',
    ['$scope', 'SupplyInventoryService', 'SupplyCategoryService',
        'SupplyCart', supplyOrderController]);

function supplyOrderController($scope, SupplyInventoryService,
                               SupplyCategoryService, SupplyCart) {

    $scope.products = null;
    $scope.quantity = 1;

    $scope.init = function() {
        $scope.products = SupplyInventoryService.getCopyOfProducts();
    };

    // Called by ng-hide in the view. Returns true if a product does not belong to the selected categories.
    $scope.hideProduct = function(product) {
        var ids = SupplyCategoryService.getSelectedCategoryIds();
        // If no filters selected, show all products.
        if (ids.length === 0) {
            return false;
        }
        return ids.indexOf(product.categoryId) === -1;
    };

    $scope.addToCart = function(product, qty) {
        SupplyCart.addToCart(product, qty);
    };

    $scope.isInCart = function(product) {
        return SupplyCart.getItemById(product.id) !== false
    };

    $scope.orderQuantityRange = function(product) {
        return SupplyInventoryService.orderQuantityRange(product);
    };

    $scope.init();
}
