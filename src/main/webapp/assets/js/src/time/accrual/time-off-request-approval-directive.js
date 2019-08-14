(function () {

    var essTime = angular.module('essTime');

    essTime.directive('timeOffRequestApproval', ['appProps', requestApprovalDirective]);

    function requestApprovalDirective(appProps) {
        return {
            restrict: 'E',
            templateUrl: appProps.ctxPath + '/template/time/accrual/time-off-request-approval-directive',
            transclude: true,
            scope: {
                approve: '=',
                active: '='
            },
            link: function($scope) {
                console.log("hello");
            }
        }
    }

})();