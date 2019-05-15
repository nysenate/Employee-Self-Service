var essTravel = angular.module('essTravel');

essTravel.directive('essAppEditBreadcrumbs', ['appProps', 'AppEditStateService', function (appProps, stateService) {
    return {
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/app-edit-breadcrumbs-directive',
        scope: {},
        link: function (scope, elem, attrs) {

            scope.stateService = stateService;
        }
    }
}]);