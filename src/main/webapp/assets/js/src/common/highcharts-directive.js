var essApp = angular.module('ess');

essApp.directive('essChart', [function() {
    return {
        restrict: 'AE',
        link: function(scope, element, attrs) {
            element.highcharts({});
        }
    }
}]);