angular.module('essSupply').directive('specialOrderItemModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/shopping/special-order-item-modal',
        controller: 'SpecialOrderItemCtrl',
        controllerAs: 'ctrl'
    }
}])
    .controller('SpecialOrderItemCtrl', ['$scope', 'modals', function ($scope, modals) {

        $scope.cancel = function () {
            modals.reject();
        };

        $scope.addToCart = function () {
            modals.resolve(modals.params().lineItem);
        }
    }]);