var essTravel = angular.module('essTravel');

essTravel.directive('essEditAppBreadcrumbs', ['appProps', 'AppEditStateService', function (appProps, stateService) {
    return {
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/travel/component/edit-application/edit-app-breadcrumbs-directive',
        scope: {},
        link: function (scope, elem, attrs) {

            scope.stateService = stateService;
        }
    }
}]);