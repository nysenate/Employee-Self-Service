essSupply = angular.module('essSupply').controller('SupplyCartController', [
    '$scope', 'EssStorageService', 'SupplyCartService', 'SupplyRequisitionApi',
    'SupplyOrderDestinationService', 'appProps', 'modals', 'LocationService', 'SupplyUtils', supplyCartController]);

function supplyCartController($scope, storageService, supplyCart, requisitionApi,
                              destinationService, appProps, modals, locationService, supplyUtils) {

    /**
     * An array of line items in the cart with positive order quantities.
     */
    var displayedLineItems = [];
    $scope.destinationCode = null;
    $scope.destinationDescription = "";
    $scope.specialInstructions = null;

    $scope.init = function () {
        var destination = destinationService.getDestination();
        if (destination != null) {
            $scope.destinationCode = destination.code;
            $scope.destinationDescription = destination.locationDescription || "";
        }
        supplyCart.initializeCart();
        displayedLineItems = angular.copy(supplyCart.getLineItems());
        displayedLineItems = supplyUtils.alphabetizeLineItems(displayedLineItems);
    };

    $scope.init();

    /**
     * Returns all line items with positive quantities.
     */
    $scope.getLineItems = function() {
        var lineItems = [];
        displayedLineItems.forEach(function(li) {
            if (li.quantity > 0) {
                lineItems.push(li);
            }
        });
        return lineItems;
    };

    /** --- Button's --- */

    $scope.submitOrder = function () {
        var params = {
            customerId: appProps.user.employeeId,
            lineItems: supplyCart.getLineItems(),
            destinationId: $scope.destinationCode + "-W",
            specialInstructions: $scope.specialInstructions
        };
        requisitionApi.save(params, function (response) {
            supplyCart.reset();
            modals.open('supply-cart-checkout-modal', response);
        }, function (response) {
            modals.open('500', {action: 'checkout cart', details: response});
        });
    };

    $scope.emptyCart = function () {
        displayedLineItems = [];
        supplyCart.reset();
    };

    /** --- Modal methods --- */

    $scope.closeModal = function () {
        modals.resolve();
    };

    $scope.returnToSupply = function () {
        modals.resolve();
        locationService.go("/supply/order", false);
    };

    $scope.logout = function () {
        locationService.go('/logout', true);
    };
}
