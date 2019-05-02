var essTravel = angular.module('essTravel');

/**
 * This directive styles a table cell according to the travel application status.
 * Call by <td ess-app-status-cell="applicationObject" ...>
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
                return app.isPending ? 'pending-cell'
                                     : app.isApproved ? 'approved-cell'
                                                      : app.isDisapproved ? 'disapproved-cell'
                                                                          : '';
            };

            $scope.statusDescription = function () {
                return app.isPending ? 'Pending'
                                     : app.isApproved ? 'Approved'
                                                      : app.isDisapproved ? 'Disapproved'
                                                                          : '';
            };

            $elem.addClass($scope.statusClass());
            $elem.text($scope.statusDescription());
        }
    }
}]);