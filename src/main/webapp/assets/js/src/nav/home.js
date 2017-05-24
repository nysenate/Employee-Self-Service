var essApp = angular.module('ess');

/**
 * The wrapping controller that is the parent of the nav menu and views.
 */
essApp.controller('MainCtrl', ['$scope', '$http', '$route', '$routeParams', '$location', '$window', 'appProps',
    function($scope, $http, $route, $routeParams, $location, $window, appProps) {
        $scope.$route = $route;
        $scope.$location = $location;
        $scope.$routeParams = $routeParams;

        /** Url pointing to help page */
        $scope.helpPageUrl = appProps.ctxPath + '/assets/help/html/index.htm';

        // Options for opening the help document in a new window
        var helpWindowName = 'helpWindow';
        var helpWindowOptions =
            'width=1024,height=768,location=no,menubar=no,personalbar=no,status=no,titlebar=no,toolbar=no';

        /**
         * Open the help page in a new window
         * @param $event
         */
        $scope.openHelpWindow = function ($event) {
            $window.open($scope.helpPageUrl, helpWindowName, helpWindowOptions);
            $event.preventDefault();    // prevents following of displayed link
        }
    }
]);