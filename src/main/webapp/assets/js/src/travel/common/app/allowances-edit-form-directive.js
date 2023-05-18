var essTravel = angular.module('essTravel');

essTravel.directive('essAllowancesEditForm', ['appProps', allowancesEditForm]);

function allowancesEditForm(appProps) {
    return {
        restrict: 'E',
        scope: {
            data: '<',               // The application being edited.
            positiveCallback: '&',   // Callback function called when continuing. Takes a travel app param named 'app'.
            neutralCallback: '&',   // Callback function called when moving back. Takes a travel app param named 'app'.
            negativeCallback: '&',  // Callback function called when canceling. Takes a travel app param named 'app'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/allowances-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.dirtyDraft = angular.copy(scope.data.draft);
            console.log(scope.dirtyDraft);

            scope.next = function () {
                scope.positiveCallback({draft: scope.dirtyDraft});
            };

            scope.previousDay = function (date) {
                return moment(date).subtract(1, 'days').toDate();
            };

            scope.tripHasMeals = function () {
                return scope.dirtyDraft.amendment.mealPerDiems.allMealPerDiems.length > 0;
            };

            scope.tripHasLodging = function () {
                return scope.dirtyDraft.amendment.lodgingPerDiems.allLodgingPerDiems.length > 0;
            };

            scope.back = function () {
                scope.neutralCallback({draft: scope.dirtyDraft});
            };

            scope.cancel = function () {
                scope.negativeCallback({draft: scope.dirtyDraft});
            }
        }
    }
}