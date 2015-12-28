essSupply = angular.module('essSupply').controller('SupplyCartController', [
'$scope', 'SupplyCart', 'SupplyInventoryService', 'SupplySubmitOrderApi', 'appProps', 'LocationService', supplyCartController]);

function supplyCartController($scope, supplyCart, supplyInventoryService, supplySubmitOrderApi, appProps, locationService) {

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
        angular.forEach(supplyCart.getItems(), function(cartItem) {
            lineItems.push({itemId: cartItem.item.id, quantity: cartItem.quantity});
        });

        var params = {customerId: appProps.user.employeeId, items: lineItems};
        supplySubmitOrderApi.save(params, function(response) {
            supplyCart.reset();
            locationService.go("/supply/requisition/order", false);
        }, function(response) {
            console.log("Error submitting order")
        });
    }
}