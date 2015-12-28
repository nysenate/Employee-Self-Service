var essSupply = angular.module('essSupply').controller('SupplyOrderController',
    ['$scope', 'SupplyInventoryService', 'SupplyCategoryService',
        'SupplyCart', supplyOrderController]);

function supplyOrderController($scope, supplyInventoryService, supplyCategoryService, supplyCart) {

    $scope.items = null;
    $scope.quantity = 1;

    $scope.init = function() {
        $scope.items = supplyInventoryService.getCopyOfItems();
    };

    // Called by ng-hide in the view. Returns true if a item does not belong to the selected categories.
    $scope.hideItem = function(item) {
        var names = supplyCategoryService.getSelectedCategoryNames();
        // If no filters selected, show all items.
        if (names.length === 0) {
            return false;
        }
        return names.indexOf(item.category) === -1;
    };

    $scope.addToCart = function(item, qty) {
        supplyCart.addToCart(item, qty);
    };

    $scope.isInCart = function(item) {
        return supplyCart.getItemById(item.id) !== false
    };

    $scope.orderQuantityRange = function(item) {
        return supplyInventoryService.orderQuantityRange(item);
    };

    $scope.init();
}
