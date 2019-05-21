var essTravel = angular.module('essTravel');

essTravel.directive('essNewAppBreadcrumbs', ['appProps', 'NewAppStateService', function (appProps, stateService) {
    return {
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/travel/component/new-application/new-app-breadcrumbs-directive',
        scope: {},
        link: function (scope, elem, attrs) {

            scope.stateService = stateService;
        }
    }
}]);