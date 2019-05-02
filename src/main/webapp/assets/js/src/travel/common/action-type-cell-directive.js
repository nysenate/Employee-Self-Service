var essTravel = angular.module('essTravel');

/**
 * This directive styles a table cell according to a application review action type.
 * Call by assigning to the action type: <td ess-action-type-cell="action.type" ...>
 */
essTravel.directive('essActionTypeCell', ['appProps', function (appProps) {
    return {
        restrict: 'A',
        scope: {
            essActionTypeCell: '='
        },
        link: function ($scope, $elem, $attrs) {

            var actionType = $scope.essActionTypeCell;

            $scope.actionClass = function () {
                return actionType === 'APPROVE' ? 'approved-text'
                                                : actionType === 'DISAPPROVE' ? 'disapproved-text'
                                                                              : '';
            };

            $scope.actionDescription = function () {
                return actionType === 'APPROVE' ? 'Approve'
                                                : actionType === 'DISAPPROVE' ? 'Disapprove'
                                                                              : '';
            };

            $elem.addClass($scope.actionClass());
            $elem.text($scope.actionDescription());
        }
    }
}]);