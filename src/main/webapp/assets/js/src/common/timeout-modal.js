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
            '<input type="button" ng-click="logout()" ng-disabled="disableButtons()"\n' +
                   'class="reject-button" value="Log out of ESS"/>' +
            '<input type="button" ng-click="close()" ng-disabled="disableButtons()"\n' +
                   'class="submit-button" value="Continue"/>' +
            '</div>' +
            '</section>',

            link: function ($scope, $element, $attrs) {
                $scope.sendingPing = false;

                $scope.timeRemaining = modals.params().remainingInactivity;

                // Display less time than is actually available
                // Reduces risk of attempting to cancel timeout when its too late.
                $scope.timeRemaining -= 1;

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

                /**
                 * Send an active ping to reset the timeout.
                 *
                 * If successful, close the modal.
                 * If failure, log out.
                 */
                $scope.close = function () {
                    $interval.cancel(countdown);
                    $scope.sendingPing = true;
                    var params = {active: true};
                    var body = {};
                    TimeoutApi.save(params, body, modals.reject, $scope.logout)
                };

                $scope.logout = function () {
                    locationService.go('/logout', true);
                };

                $scope.disableButtons = function () {
                    return $scope.sendingPing || $scope.timeRemaining <= 0;
                }
            }
        };
    }]);
