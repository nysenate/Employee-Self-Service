var essApp = angular.module('ess');

essApp.directive('timeoutModal', ['modals', 'httpTimeoutChecker', '$interval', 'LocationService', 'TimeoutApi', 'appProps',
    function (modals, httpTimeoutChecker, $interval, locationService, timeoutApi, appProps) {
        return {
            template: '<section id="timeout-modal" title="Inactivity Warning">' +
            '<h1>Inactive Session Timeout</h1>' +
            '<p class="timeout-text">' +
            'Due to inactivity, you will be logged out in {{timeRemaining}} seconds.<br>' +
            'Do you want to continue your work?' +
            '</p>' +
            '<div class="button-container">' +
            '<input type="button" class="reject-button" ng-click="logout()" value="Log out of ESS"/>' +
            '<input type="button" class="submit-button" ng-click="close()" value="Continue"/>' +
            '</div>' +
            '</section>',

            link: function ($scope, $element, $attrs) {
                $scope.timeRemaining = 60;

                var countdown = $interval(function () {
                    if ($scope.timeRemaining > 0) {
                        $scope.timeRemaining--;
                    }
                    else {
                        // log out the user
                        var params = {
                            idleTime: -1
                        };
                        timeoutApi.get(params, function () {
                            window.location.replace(appProps.loginUrl);
                            window.location.reload(true);
                        });
                    }
                }, 1000);

                $scope.$on('$destroy', function () {
                    $interval.cancel(countdown);
                });

                $scope.close = function () {
                    modals.reject();
                    httpTimeoutChecker.modalClosed();
                };

                $scope.logout = function () {
                    locationService.go('/logout', true);
                };
            }
        };
    }]);
