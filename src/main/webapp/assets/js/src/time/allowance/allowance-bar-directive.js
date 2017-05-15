angular.module('essTime')
    .directive('allowanceBar', ['appProps', 'AllowanceUtils', allowanceBarDirective]);

/**
 * A directive that displays current allowance information for a single employee
 * In a bar
 * @param appProps
 * @param allowanceUtils
 * @returns {{scope: {allowance: string, tempWorkHours: string, loading: string}, templateUrl: string, link: link}}
 */
function allowanceBarDirective(appProps, allowanceUtils) {
    return {
        scope: {
            allowance: "=",
            tempWorkHours: "=",
            loading: "=?"
        },
        templateUrl: appProps.ctxPath + '/template/time/allowance/allowance-bar',
        link: function ($scope, $elem, $attrs) {

            var submitted = $attrs['submitted'] === 'true';

            $scope.getAvailableHours = function() {
                var hours = submitted ? 0 : $scope.tempWorkHours;
                return allowanceUtils.getAvailableHours($scope.allowance, hours);
            };
        }
    };
}
