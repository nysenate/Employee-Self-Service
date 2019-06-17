var essTravel = angular.module('essTravel');

/**
 * This directive styles a element according to the travel application status.
 * Example: <td ess-app-status-cell="applicationObject" ...>
 */
essTravel.directive('essAppStatusCell', ['appProps', function (appProps) {
    return {
        restrict: 'A',
        scope: {
            essAppStatusCell: '='
        },
        link: function ($scope, $elem, $attrs) {

            var app = $scope.essAppStatusCell;

            $scope.statusClass = function () {
                return app.status.isPending ? 'travel-highlight-text'
                                            : app.status.isApproved ? 'approved-text'
                                                                    : app.status.isDisapproved ? 'disapproved-text'
                                                                                               : '';
            };

            $scope.statusDescription = function () {
                return app.status.isPending ? 'Pending'
                                            : app.status.isApproved ? 'Approved'
                                                                    : app.status.isDisapproved ? 'Disapproved'
                                                                                               : '';
            };

            $elem.addClass($scope.statusClass());
            $elem.text($scope.statusDescription());
        }
    }
}]);