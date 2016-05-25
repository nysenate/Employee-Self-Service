angular.module('essSupply').directive('specialOrderItemModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/order/special-order-item-modal',
        controller: 'SpecialOrderItemCtrl',
        controllerAs: 'ctrl'
    }
}])
    .controller('SpecialOrderItemCtrl', ['$scope', 'modals', 'SupplyCartService', function ($scope, modals, supplyCart) {

        var allowance = modals.params().allowance;

        $scope.cancel = function () {
            modals.resolve();
        };

        $scope.addToCart = function () {
            supplyCart.addToCart(allowance.item, allowance.selectedQuantity);
            modals.resolve();
        }
    }]);