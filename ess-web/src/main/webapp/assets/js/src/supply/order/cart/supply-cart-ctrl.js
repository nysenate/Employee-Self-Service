essSupply = angular.module('essSupply').controller('SupplyCartController', [
    '$scope', 'SupplyCartService', 'SupplyLocationAllowanceService', 'SupplyRequisitionApi', 
    'SupplyOrderDestinationService', 'appProps', 'modals', supplyCartController]);

function supplyCartController($scope, supplyCart, allowanceService, requisitionApi, 
                              destinationService, appProps, modals) {

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
        supplyCart.removeFromCart(item.id);
    };

    $scope.submitOrder = function () {
        var params = {
            customerId: appProps.user.employeeId,
            lineItems: supplyCart.getItems(),
            destinationId: destinationService.getDestination().locId
        };
        requisitionApi.save(params, function (response) {
            supplyCart.reset();
            destinationService.reset();
            modals.open('supply-cart-checkout-modal', response);
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
