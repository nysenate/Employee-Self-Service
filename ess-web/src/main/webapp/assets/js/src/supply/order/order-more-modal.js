/**
 * This modal is displayed when the user tries to order over the per order maximum by selecting "more" from the drop down.
 */
angular.module('essSupply').directive('orderMoreModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/order/order-more-modal',
        controller: 'OverAllowedQuantityCtrl',
        controllerAs: 'ctrl'
    }
}])
    .controller('OverAllowedQuantityCtrl', ['$scope', 'modals', function ($scope, modals) {

        $scope.submitSpecialRequest = function () {
            modals.resolve();
            modals.open('item-special-request-modal', params);
        };

        $scope.nevermind = function () {
            modals.resolve();
        }
    }]);