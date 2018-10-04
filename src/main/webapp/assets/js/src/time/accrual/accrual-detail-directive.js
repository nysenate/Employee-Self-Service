angular.module('essTime')
    .directive('accrualDetails', ['appProps', 'modals', 'AccrualUtils', accrualDetailDirective]);

function accrualDetailDirective(appProps, modals, accrualUtils) {
    return {
        templateUrl: appProps.ctxPath + '/template/time/accrual/accrual-details',
        link: function ($scope, $elem, $attrs) {
            $scope.accruals = modals.params().accruals;

            $scope.close = modals.resolve;
        }
    };
}
