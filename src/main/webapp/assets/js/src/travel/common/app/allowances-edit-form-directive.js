var essTravel = angular.module('essTravel');

essTravel.directive('essAllowancesEditForm', ['appProps', allowancesEditForm]);

function allowancesEditForm(appProps) {
    return {
        restrict: 'E',
        scope: {
            amendment: '<',               // The application being edited.
            title: '@',             // The title
            positiveCallback: '&',   // Callback function called when continuing. Takes a travel app param named 'app'.
            neutralCallback: '&',   // Callback function called when moving back. Takes a travel app param named 'app'.
            negativeCallback: '&',  // Callback function called when canceling. Takes a travel app param named 'app'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/allowances-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.dirtyAmendment = angular.copy(scope.amendment);
            console.log(scope.dirtyAmendment);

            scope.next = function () {
                scope.positiveCallback({amendment: scope.dirtyAmendment});
            };

            scope.previousDay = function (date) {
                return moment(date).subtract(1, 'days').toDate();
            };

            scope.tripHasMeals = function () {
                return scope.dirtyAmendment.mealPerDiems.allMealPerDiems.length > 0;
            };

            scope.tripHasLodging = function () {
                return scope.dirtyAmendment.lodgingPerDiems.allLodgingPerDiems.length > 0;
            };

            scope.back = function () {
                scope.neutralCallback({amendment: scope.dirtyAmendment});
            };

            scope.cancel = function () {
                scope.negativeCallback({amendment: scope.dirtyAmendment});
            }
        }
    }
}