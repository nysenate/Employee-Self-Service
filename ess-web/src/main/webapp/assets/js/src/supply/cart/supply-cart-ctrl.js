essSupply = angular.module('essSupply').controller('SupplyCartController', [
'$scope', 'SupplyCart', 'SupplyInventoryService', supplyCartController]);

function supplyCartController($scope, SupplyCart, SupplyInventoryService) {

    $scope.myCartItems = function() {
        return SupplyCart.getItems();
    };

    $scope.orderQuantityRange = function(product) {
        return SupplyInventoryService.orderQuantityRange(product);
    };

    $scope.cartHasItems = function() {
        return SupplyCart.getItems().length > 0
    };

    $scope.removeFromCart = function(product) {
        SupplyCart.removeFromCart(product);
    }
}