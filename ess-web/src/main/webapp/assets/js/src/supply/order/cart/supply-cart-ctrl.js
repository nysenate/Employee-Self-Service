essSupply = angular.module('essSupply').controller('SupplyCartController', [
    '$scope', 'SupplyCartService', 'SupplyLocationAllowanceService', 'SupplySubmitOrderApi', 'appProps', 'modals', supplyCartController]);

function supplyCartController($scope, supplyCart, allowanceService, supplySubmitOrderApi, appProps, modals) {

    $scope.myCartItems = function () {
        return supplyCart.getItems();
    };

    $scope.orderQuantityRange = function (item) {
        return allowanceService.getAllowedQuantities(allowanceService.getAllowanceByItemId(item.id));
    };

    $scope.cartHasItems = function () {
        return supplyCart.getItems().length > 0
    };

    $scope.removeFromCart = function (item) {
        supplyCart.removeFromCart(item);
    };

    $scope.submitOrder = function () {
        var params = {customerId: appProps.user.employeeId, lineItems: supplyCart.getItems()}; // TODO: add location object to params
        supplySubmitOrderApi.save(params, function (response) {
            supplyCart.reset();
            modals.open('supply-cart-checkout-modal');
        }, function (response) {
            console.log("Error submitting order")
        });
    };

    $scope.closeModal = function () {
        modals.resolve();
    };

    $scope.viewOrder = function () {
        modals.resolve();
        // locationService.go("/supply/history/location-history", false);
    }
}