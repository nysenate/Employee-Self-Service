var essSupply = angular.module('essSupply').controller('SupplyOrderController',
    ['$scope', 'SupplyItemApi', 'SupplyCategoryService', 'modals',
        'SupplyCart', supplyOrderController]);

function supplyOrderController($scope, SupplyItemApi, SupplyCategoryService, modals, SupplyCart) {

    // TODO: add loading state and display?

    $scope.items = null;
    $scope.quantity = 1;

    $scope.init = function() {
        $scope.getItems();
    };

    $scope.getItems = function() {
        $scope.items = [];
        SupplyItemApi.get(function(response) {
            $scope.items = response.result;
        }, function(response) {
            modals.open('500', {details: response});
            console.log(response);
        })
    };

    // Called by ng-hide in the view. Returns true if a item does not belong to the selected categories.
    $scope.hideProduct = function(item) {
        var ids = SupplyCategoryService.getSelectedCategoryIds();
        // If no filters selected, show all items.
        if (ids.length === 0) {
            return false;
        }
        return ids.indexOf(item.categoryId) === -1;
    };

    $scope.addToCart = function(item, qty) {
        SupplyCart.addToCart(item, qty);
    };

    $scope.isInCart = function(item) {
        return SupplyCart.getItemById(item.id) !== false
    };

    $scope.orderQuantityRange = function(item) {
        var range = [];
        for (var i = 1; i <= item.suggestedMaxQty * 2; i++) {
            range.push(i);
        }
        return range;
    };

    $scope.init();
}
