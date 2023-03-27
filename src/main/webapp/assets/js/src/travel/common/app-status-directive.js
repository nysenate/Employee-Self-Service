var essTravel = angular.module('essTravel');

/**
 * This directive styles a element according to the travel application status.
 * Example: <td ess-app-status="applicationObject" ...>
 */
essTravel.directive('essAppStatus', ['appProps', function (appProps) {
    return {
        restrict: 'A',
        scope: {
            essAppStatus: '='
        },
        link: function ($scope, $elem, $attrs) {

            var app = $scope.essAppStatus;

            $scope.statusClass = function () {
                if (app.status.isPending) {
                    return 'travel-highlight-text';
                }
                else if (app.status.isApproved) {
                    return 'approved-text';
                }
                else if (app.status.isDisapproved) {
                    return 'disapproved-text';
                }
                else if (app.status.isNotApplicable) {
                    return 'travel-text bold';
                }
                return '';
            };

            $scope.statusDescription = function () {
                if (app.status.isPending) {
                    return 'Pending';
                }
                else if (app.status.isApproved) {
                    return 'Approved';
                }
                else if (app.status.isDisapproved) {
                    return 'Disapproved';
                }
                else if (app.status.isNotApplicable) {
                    return 'Not Applicable';
                }
                return '';
            };

            $elem.addClass($scope.statusClass());
            $elem.text($scope.statusDescription());
        }
    }
}]);