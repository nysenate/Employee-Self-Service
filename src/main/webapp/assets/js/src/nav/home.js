var essApp = angular.module('ess');

/**
 * The wrapping controller that is the parent of the nav menu and views.
 */
essApp.controller('MainCtrl', ['$scope', '$http', '$route', '$routeParams', '$location',
    function($scope, $http, $route, $routeParams, $location) {
        $scope.$route = $route;
        $scope.$location = $location;
        $scope.$routeParams = $routeParams;
    }
]);