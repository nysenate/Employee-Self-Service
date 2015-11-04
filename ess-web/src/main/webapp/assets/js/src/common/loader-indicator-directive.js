var essApp = angular.module('ess');

essApp.directive('loaderIndicator', [function() {
    return {
        restrict: 'AE',
        template:
            '<div class="loader">' +
            '  <div class="dot dot1"></div>' +
            '  <div class="dot dot2"></div>' +
            '  <div class="dot dot3"></div>' +
            '  <div class="dot dot4"></div>' +
            '</div>',
        link: function(scope, element, attrs) {}
    };
}]);