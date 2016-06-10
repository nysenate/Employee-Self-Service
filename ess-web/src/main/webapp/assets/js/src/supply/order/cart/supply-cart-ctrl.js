essSupply = angular.module('essSupply').controller('SupplyCartController', [
    '$scope', 'SupplyCookieService', 'SupplyCartService', 'SupplyLocationAllowanceService', 'SupplyRequisitionApi',
    'SupplyOrderDestinationService', 'appProps', 'modals', supplyCartController]);

function supplyCartController($scope,cookies, supplyCart, allowanceService, requisitionApi,
                              destinationService, appProps, modals) {

    $scope.myCartItems = function () {
        return supplyCart.getItems();
    };

    $scope.orderQuantityRange = function (item) {
       return allowanceService.getAllowedQuantities(allowanceService.getAllowanceByItemId(item.id))
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
            destinationId: cookies.getDestination().locId
            };
        requisitionApi.save(params, function (response) {
            supplyCart.reset();
            destinationService.reset();
            cookies.resetDestination();
            modals.open('supply-cart-checkout-modal', response);
        }, function (response) {
            console.log(response)
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
