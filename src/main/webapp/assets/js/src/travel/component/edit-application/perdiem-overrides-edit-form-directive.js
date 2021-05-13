var essTravel = angular.module('essTravel');

essTravel.directive('essPerdiemOverridesEditForm', ['appProps', perDiemOverrideEditForm]);

function perDiemOverrideEditForm(appProps) {
    return {
        restrict: 'E',
        scope: {
            amendment: '<',         // The application being edited.
            positiveCallback: '&',  // Callback function called when continuing. Takes a travel app param named 'app'.
            neutralCallback: '&',   // Callback function called when moving back. Takes a travel app param named 'app'.
            negativeCallback: '&',  // Callback function called when canceling. Takes a travel app param named 'app'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/component/edit-application/perdiem-overrides-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.dirtyAmendment = angular.copy(scope.amendment);

            scope.perdiems = [
                Object.assign(scope.dirtyAmendment.mealPerDiems, {name: 'Meals'}),
                Object.assign(scope.dirtyAmendment.lodgingPerDiems, {name: 'Lodging'})
            ]

            console.log(scope.dirtyAmendment);

            // Reset the override rate when unchecked since the backend does not actually check the
            // isOverridden boolean, its derived from the overrideRate.
            scope.onCheckboxChange = function(perdiem) {
                if (!perdiem.isOverridden) {
                    perdiem.overrideRate = 0;
                }
            }

            scope.next = function () {
                scope.positiveCallback({amendment: scope.dirtyAmendment});
            };

            scope.back = function () {
                scope.neutralCallback({amendment: scope.dirtyAmendment});
            };

            scope.cancel = function () {
                scope.negativeCallback({amendment: scope.dirtyAmendment});
            };

            // Use undefined instead of 0 in the override input boxes so they are empty instead of 0 when not overridden.
            function zeroToUndefined(value) {
                return value === 0 ? undefined : value;
            }
        }
    }
}