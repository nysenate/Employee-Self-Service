angular.module('essSupply').directive('specialOrderItemModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/order/special-order-item-modal',
        controller: 'SpecialOrderItemCtrl',
        controllerAs: 'ctrl'
    }
}])
    .controller('SpecialOrderItemCtrl', ['$scope', 'modals', 'SupplyCartService', function ($scope, modals, supplyCart) {

        var lineItem = modals.params().lineItem;

        $scope.cancel = function () {
            modals.resolve();
        };

        $scope.addToCart = function () {
            supplyCart.addToCart(lineItem);
            modals.resolve();
        }
    }]);