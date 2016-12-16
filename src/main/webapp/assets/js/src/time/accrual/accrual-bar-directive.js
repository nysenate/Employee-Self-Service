angular.module('essTime')
    .directive('accrualBar', ['appProps', accrualBarDirective]);

function accrualBarDirective(appProps) {
    return {
        scope: {
            accruals: "=",
            loading: "="
        },
        templateUrl: appProps.ctxPath + '/template/time/accrual/accrual-bar'
    };
}