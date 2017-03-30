/**
 * Supply Quantity Selector is collection of HTML elements which allow
 * users to add items to their cart and adjust quantities by pressing buttons
 * or editing in an input box.
 *
 * Used on the Order and Cart pages.
 */
var essSupply = angular.module('essSupply')
    .directive('supplyQuantitySelector', ['appProps', function (appProps) {
        return {
            templateUrl: appProps.ctxPath + '/template/supply/shopping/supply-quantity-selector',
            scope: {
                'lineItem': '='
            },
            controller: 'SupplyQuantitySelectorCtrl',
            controllerAs: 'ctrl'
        };
    }])

    .controller('SupplyQuantitySelectorCtrl', ['$scope', 'modals', 'SupplyCartService', quantitySelectorCtrl]);

function quantitySelectorCtrl($scope, modals, supplyCart) {

    $scope.addToCart = function (lineItem) {
        // first time adding special item, display modal.
        if (!supplyCart.isItemIdOrdered(lineItem.item.id) && lineItem.item.specialRequest) {
            modals.open('special-order-item-modal', {lineItem: lineItem})
                .then(function () {
                    lineItem.increment();
                    updateAndSaveCart(lineItem);
                })
        }
        else {
            lineItem.increment();
            updateAndSaveCart(lineItem);
        }
    };

    $scope.decrementQuantity = function (lineItem) {
        lineItem.decrement();
        updateAndSaveCart(lineItem)
    };

    $scope.incrementQuantity = function (lineItem) {
        if ($scope.isAtMaxQty(lineItem)) {
            lineItem.increment();
            displayOrderMoreModal(lineItem);
        }
        else {
            lineItem.increment();
            updateAndSaveCart(lineItem);
        }
    };

    $scope.onCustomQtyEntered = function (lineItem) {
        // Convert entered string to int. Input has to be text for maxlength to work.
        lineItem.quantity = Number(lineItem.quantity);
        if ($scope.isOverMaxQty(lineItem) && !previousValueOverMaxQty(lineItem)) {
            displayOrderMoreModal(lineItem);
        }
        else {
            updateAndSaveCart(lineItem);
        }
    };

    /**
     * Displays the order more prompt modal warning users they are about to order
     * over the recommended maximum.
     * If the user accepts the modal, any updates made to lineItem are saved.
     * If the user cancels the modal, the lineItem is reset to its previous state.
     */
    function displayOrderMoreModal(lineItem) {
        modals.open('order-more-prompt-modal', {lineItem: lineItem})
            .then(updateAndSaveCart)
            .catch(resetToOriginalQuantity)
    }

    function updateAndSaveCart(lineItem) {
        supplyCart.updateCartLineItem(lineItem);
        supplyCart.save();
    }

    function resetToOriginalQuantity(lineItem) {
        lineItem.quantity = supplyCart.getCartLineItem(lineItem.item.id).quantity;
    }

    $scope.isInCart = function (item) {
        return supplyCart.isItemIdOrdered(item.id);
    };

    $scope.isAtMaxQty = function (lineItem) {
        return lineItem.quantity === lineItem.item.perOrderAllowance;
    };

    $scope.isOverMaxQty = function (lineItem) {
        return lineItem.quantity > lineItem.item.perOrderAllowance
    };

    function previousValueOverMaxQty(lineItem) {
        var savedLineItem = supplyCart.getCartLineItem(lineItem.item.id);
        return savedLineItem.quantity > savedLineItem.item.perOrderAllowance;
    }
}
