/**
 * This modal is displayed when the user tries to order over the per order or per month allowed quantities.
 */
angular.module('essSupply').directive('overAllowedQuantityModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/order/over-allowed-quantity-modal',
        controller: 'OverAllowedQuantityCtrl',
        controllerAs: 'ctrl'
    }
}])
    .controller('OverAllowedQuantityCtrl', ['$scope', 'modals', function ($scope, modals) {
        $scope.type = undefined;
        $scope.types = {
            ORDER: 0,
            MONTH: 1
        };

        var params = modals.params();

        function init() {
            if (modals.params().type === 'order') {
                $scope.type = $scope.types.ORDER;
            }
            if (modals.params().type === 'month') {
                $scope.type = $scope.types.MONTH;
            }
        }

        init();

        $scope.submitSpecialRequest = function () {
            modals.resolve();
            modals.open('item-special-request-modal', params);
        };

        $scope.nevermind = function () {
            modals.resolve();
        }
    }]);