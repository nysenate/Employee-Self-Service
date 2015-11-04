var essApp = angular.module('ess');

essApp.filter('activeInactive', function () {
    return function(input) {
        return (input === 'A' || input === 'a') ? "Active" : "Inactive";
    };
});