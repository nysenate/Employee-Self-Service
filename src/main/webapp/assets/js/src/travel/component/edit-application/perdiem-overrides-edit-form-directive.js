var essTravel = angular.module('essTravel');

essTravel.directive('essPerdiemOverridesEditForm', ['appProps', perDiemOverrideEditForm]);

function perDiemOverrideEditForm(appProps) {
    return {
        restrict: 'E',
        scope: {
            app: '<',               // The application being edited.
            title: '@',             // The title
            positiveCallback: '&',  // Callback function called when continuing. Takes a travel app param named 'app'.
            neutralCallback: '&',   // Callback function called when moving back. Takes a travel app param named 'app'.
            negativeCallback: '&',  // Callback function called when canceling. Takes a travel app param named 'app'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/component/edit-application/perdiem-overrides-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.dirtyApp = angular.copy(scope.app);

            // Convert overrides of 0 to undefined.
            scope.dirtyApp.perDiemOverrides.mileageOverride = zeroToUndefined(scope.dirtyApp.perDiemOverrides.mileageOverride);
            scope.dirtyApp.perDiemOverrides.mealsOverride = zeroToUndefined(scope.dirtyApp.perDiemOverrides.mealsOverride);
            scope.dirtyApp.perDiemOverrides.lodgingOverride = zeroToUndefined(scope.dirtyApp.perDiemOverrides.lodgingOverride);

            scope.next = function () {
                scope.positiveCallback({app: scope.dirtyApp});
            };

            scope.back = function () {
                scope.neutralCallback({app: scope.dirtyApp});
            };

            scope.cancel = function () {
                scope.negativeCallback({app: scope.dirtyApp});
            };

            // Use undefined instead of 0 in the override input boxes so they are empty instead of 0 when not overridden.
            function zeroToUndefined(value) {
                return value === 0 ? undefined : value;
            }
        }
    }
}