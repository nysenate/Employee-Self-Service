/**
 * This modal is displayed when the user tries to order over the per order maximum by selecting "more" from the drop down.
 * Resolve the modal promise if the user confirms they want to order more, reject otherwise.
 */
angular.module('essSupply').directive('orderMorePromptModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/order/order-more-prompt-modal',
        controller: 'OrderMorePromptModalCtrl',
        controllerAs: 'ctrl'
    }
}])
    .controller('OrderMorePromptModalCtrl', ['$scope', 'modals', function ($scope, modals) {

        $scope.confirm = function () {
            modals.resolve(modals.params().allowance);
        };

        $scope.nevermind = function () {
            modals.reject();
        }
    }]);