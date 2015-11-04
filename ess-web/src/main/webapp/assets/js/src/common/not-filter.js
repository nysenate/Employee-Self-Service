var essApp = angular.module('ess');

essApp.filter('not', function () {
    return function(input) {
        return !input;
    };
});