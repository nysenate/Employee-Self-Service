var essTravel = angular.module('essTravel');

essTravel.directive('essPerdiemOverridesEditForm', ['appProps', perDiemOverrideEditForm]);

function perDiemOverrideEditForm(appProps) {
    return {
        restrict: 'E',
        scope: {
            amendment: '<',         // The application being edited.
            title: '@',             // The title
            positiveCallback: '&',  // Callback function called when continuing. Takes a travel app param named 'app'.
            neutralCallback: '&',   // Callback function called when moving back. Takes a travel app param named 'app'.
            negativeCallback: '&',  // Callback function called when canceling. Takes a travel app param named 'app'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/component/edit-application/perdiem-overrides-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.dirtyAmendment = angular.copy(scope.amendment);

            console.log(scope.dirtyAmendment);

            // Convert overrides of 0 to undefined.
            scope.dirtyAmendment.route.mileagePerDiems.overrideRate = 0;
            // scope.dirtyAmendment.route.mileagePerDiems.overrideRate = zeroToUndefined(scope.dirtyAmendment.perDiemOverrides.mileageOverride);
            scope.dirtyAmendment.mealPerDiems.overrideRate = zeroToUndefined(scope.dirtyAmendment.mealPerDiems.overrideRate);
            scope.dirtyAmendment.lodgingPerDiems.overrideRate = zeroToUndefined(scope.dirtyAmendment.lodgingPerDiems.overrideRate);

            scope.mileageOverrideRate = 0; // TODO implement mileage overrides.
            scope.mealOverrideRate = parseFloat(scope.dirtyAmendment.mealPerDiems.overrideRate);
            scope.lodgingOverrideRate = parseFloat(scope.dirtyAmendment.lodgingPerDiems.overrideRate);

            scope.next = function () {
                scope.dirtyAmendment.route.mileagePerDiems.overrideRate = scope.mileageOverrideRate.toString();
                scope.dirtyAmendment.mealPerDiems.overrideRate = scope.mealOverrideRate.toString();
                scope.dirtyAmendment.lodgingPerDiems.overrideRate = scope.lodgingOverrideRate.toString();

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