var essApp = angular.module('ess');

essApp.directive('timeoutModal', ['modals', '$interval', 'LocationService', 'TimeoutApi',
    function (modals, $interval, locationService, TimeoutApi) {
        return {
            template: '<section id="timeout-modal" title="Inactivity Warning">' +
            '<h1>Inactive Session Timeout</h1>' +
            '<p class="timeout-text">' +
            'Due to inactivity, you will be logged out in {{timeRemaining}} seconds.<br>' +
            'Do you want to continue your work?' +
            '</p>' +
            '<div class="button-container">' +
            '<input type="button" ng-click="logout()" ng-disabled="timeRemaining<=0"\n' +
                   'class="reject-button" value="Log out of ESS"/>' +
            '<input type="button" ng-click="close()" ng-disabled="timeRemaining<=0"\n' +
                   'class="submit-button" value="Continue"/>' +
            '</div>' +
            '</section>',

            link: function ($scope, $element, $attrs) {
                $scope.timeRemaining = modals.params().remainingInactivity;

                var countdown = $interval(function () {
                    if ($scope.timeRemaining > 0) {
                        $scope.timeRemaining--;
                    }
                    else {
                        $scope.logout();
                    }
                }, 1000);

                $scope.$on('$destroy', function () {
                    $interval.cancel(countdown);
                });

                $scope.close = function () {
                    // send an active ping to reset the timeout
                    TimeoutApi.save({active: 'true'}, {});
                    modals.reject();
                };

                $scope.logout = function () {
                    locationService.go('/logout', true);
                };
            }
        };
    }]);
