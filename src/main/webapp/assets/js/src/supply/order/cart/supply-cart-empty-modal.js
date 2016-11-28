/**
 * This modal is displayed when the user tries to empty cart
 */
angular.module('essSupply').directive('supplyCartEmptyModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/order/cart/cart-empty-modal',
        controller: 'SupplyCartEmptyModal',
        controllerAs: 'ctrl'
    }
}])
    .controller('SupplyCartEmptyModal', ['$scope', 'modals', 'SupplyOrderDestinationService', 'SupplyCartService', 'LocationService', function ($scope, modals, destinationService, supplyCart, locationService) {

        $scope.confirm = function () {
            modals.resolve();
        };

        $scope.nevermind = function () {
            modals.reject();
        }
    }]);