essSupply = angular.module('essSupply').controller('SupplyCartController', [
'$scope', 'SupplyCart', 'SupplyInventoryService', supplyCartController]);

function supplyCartController($scope, supplyCart, supplyInventoryService) {

    $scope.myCartItems = function() {
        return supplyCart.getItems();
    };

    $scope.orderQuantityRange = function(item) {
        return supplyInventoryService.orderQuantityRange(item);
    };

    $scope.cartHasItems = function() {
        return supplyCart.getItems().length > 0
    };

    $scope.removeFromCart = function(item) {
        supplyCart.removeFromCart(item);
    };
}