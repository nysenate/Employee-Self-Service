/**
 * This modal is displayed when the user tries to order over the per order maximum by selecting "more" from the drop down.
 * Resolve the modal promise if the user confirms they want to order more, reject otherwise.
 */
angular.module('essSupply').directive('orderCancelingModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/shopping/order-canceling-modal',
        controller: 'OrderCancelingCtrl',
        controllerAs: 'ctrl'
    }
}])
    .controller('OrderCancelingCtrl', ['$scope', 'modals', 'SupplyOrderDestinationService', 'SupplyCartService', 'LocationService', function ($scope, modals, destinationService, supplyCart, locationService) {

        $scope.confirm = function () {
            modals.resolve();
        };

        $scope.nevermind = function () {
            modals.reject();
        }
    }]);