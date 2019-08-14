(function () {
    var essTime = angular.module('essTime');

    essTime.directive('timeOffRequestList', ['appProps', requestDirective]);

    function requestDirective(appProps) {
        return {
            restrict: 'E',
            templateUrl: appProps.ctxPath + '/template/time/accrual/time-off-request-list',
            transclude: true,
            scope: {
                data: '='
            }
        }
    }
})();