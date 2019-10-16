var essTravel = angular.module('essTravel');

essTravel.directive('essAppSummaryTable', ['appProps', function (appProps) {
    return {
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/travel/common/app-summary-table-directive',
        scope: {
            apps: '=',          // An array of applications to display in the table.
            onRowClick: '&'     // Method to be called when a row is clicked, Must take 1 param named 'app'
        },
        link: function ($scope, $elem, $attrs) {

            $scope.options = {
                // Include a 'show-status' attribute on this directive to display the status column.
                showStatus: $attrs.hasOwnProperty('showStatus')
            };
        }
    }
}]);
