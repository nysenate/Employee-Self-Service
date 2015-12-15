essSupply = angular.module('essSupply').controller('SupplyCartController', [
'$scope', 'SupplyCart', 'SupplyInventoryService', 'SupplySubmitOrderApi', 'appProps', '$http', supplyCartController]);

function supplyCartController($scope, supplyCart, supplyInventoryService, supplySubmitOrderApi, appProps, $http) {

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

    $scope.submitOrder = function() {
        var lineItems = [];
        angular.forEach(supplyCart.getItems(), function(item) {
            lineItems.push({itemId: item.product.id, quantity: item.quantity});
        });

        var params = {customerId: appProps.user.employeeId, items: lineItems};
        supplySubmitOrderApi.save(params);
    }
}