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
        $scope.specialInstructions = supplyCart.getSpecialInstructions();
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

    /** Save special instructions whenever they are changed. */
    $scope.saveSpecialInstructions = function () {
        supplyCart.setSpecialInstructions($scope.specialInstructions);
        supplyCart.save();
    };

    var submitOrder = function (deliveryMethod) {
        var params = {
            customerId: appProps.user.employeeId,
            lineItems: supplyCart.getCartItems(),
            destinationId: $scope.destinationCode + "-W",
            deliveryMethod: deliveryMethod,
            specialInstructions: $scope.specialInstructions
        };
        requisitionApi.save(params, function (response) {
            supplyCart.reset();
            modals.open('supply-cart-checkout-modal', response);
        }, function (response) {
            modals.open('500', {action: 'checkout cart', details: response});
        });
    };

    /** --- Button's --- */

    $scope.checkout = function () {
        // User must select delivery or pickup before the order can be submitted.
        modals.open('delivery-method-modal', {}, true)
            .then(function (deliveryMethod) {
                submitOrder(deliveryMethod)
            })
            // Do nothing if modal is rejected/canceled (this prevents an error message in dev tools)
            .catch(function () {})
    };

    $scope.emptyCart = function () {
        modals.open('supply-cart-empty-modal').then(reset);

        function reset() {
            supplyCart.reset();
            locationService.go("/supply/shopping/order", false);
        }
    };

    /** --- Modal methods --- */

    $scope.displayLargeImage = function (item) {
        modals.open('large-item-image-modal', {item: item}, true)
    };

    $scope.closeModal = function () {
        modals.resolve();
    };

}
