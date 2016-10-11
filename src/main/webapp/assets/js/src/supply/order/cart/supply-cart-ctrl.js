essSupply = angular.module('essSupply').controller('SupplyCartController', [
    '$scope', 'SupplyCookieService', 'SupplyCartService', 'SupplyLocationAllowanceService', 'SupplyRequisitionApi',
    'SupplyOrderDestinationService', 'appProps', 'modals', 'LocationService', supplyCartController]);

function supplyCartController($scope, cookies, supplyCart, allowanceService, requisitionApi,
                              destinationService, appProps, modals, locationService) {

    $scope.destinationCode = null;
    $scope.destinationDescription = "";
    $scope.specialInstructions = null;

    $scope.init = function () {
        var destination = destinationService.getDestination();
        if (destination != null) {
            $scope.destinationCode = destination.code;
            $scope.destinationDescription = destinationService.getDestination().locationDescription || "";
        }
    };

    $scope.init();

    $scope.myCartItems = function () {
        return supplyCart.getCart();
    };

    $scope.orderQuantityRange = function (item) {
        return allowanceService.getAllowedQuantities(item)
    };

    $scope.cartHasItems = function () {
        return supplyCart.getCart().length > 0
    };

    $scope.removeFromCart = function (item) {
        supplyCart.removeFromCart(item.id);
    };

    $scope.submitOrder = function () {
        var params = {
            customerId: appProps.user.employeeId,
            lineItems: supplyCart.getCart(),
            destinationId: cookies.getDestination().locId,
            specialInstructions: $scope.specialInstructions
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

    $scope.orderedOverRecommended = function (cartItem) {
        return cartItem.quantity > cartItem.item.maxQtyPerOrder;
    };

    $scope.closeModal = function () {
        modals.resolve();
    };

    $scope.viewOrder = function () {
        modals.resolve();
        // locationService.go("/supply/history/location-history", false);
    };
    $scope.resetDestination = function () {
        supplyCart.reset();
        destinationService.reset();
        locationService.go("/supply/order", true);
    }
}
