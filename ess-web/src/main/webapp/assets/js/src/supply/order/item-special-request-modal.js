angular.module('essSupply').directive('itemSpecialRequestModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/order/item-special-request-modal',
        controller: 'ItemSpecialRequestCtrl',
        controllerAs: 'ctrl'
    }
}])
    .controller('ItemSpecialRequestCtrl', ['$scope', 'modals', 'SupplyCartService', function ($scope, modals, supplyCart) {

        var params = {};
        $scope.quantity = 1;

        function init() {
            params = modals.params();
            // Initialize to the current quantity in cart or 1 if not in cart.
            var lineItem = supplyCart.getCartLineItem(params.item.id);
            $scope.quantity = lineItem ? lineItem.quantity : 1;
        }

        init();

        $scope.addToCart = function () {
            // Input form allows 'e' characters and does not call validation when they are entered.
            // Check here to make sure an 'e' has not been entered.
            if (isNaN($scope.quantity)) {
                return;
            }
            // User is entering total requested quantity. We don't want to add this quantity to the 
            // quantity already in the cart, so remove from cart first.
            if (supplyCart.isItemInCart(params.item.id)) {
                supplyCart.removeFromCart(params.item.id);
            }
            supplyCart.addToCart(params.item, $scope.quantity);
            modals.resolve();
        };

        $scope.cancel = function () {
            modals.resolve();
        };
    }]);
