var essTravel = angular.module('essTravel');

essTravel.directive('essPerdiemOverridesEditForm', ['appProps', perdiemOverrideEditForm]);

function perdiemOverrideEditForm(appProps) {
    return {
        restrict: 'E',
        scope: {
            app: '<',               // The application being edited.
            title: '@',             // The title
            positiveCallback: '&',  // Callback function called when continuing. Takes a travel app param named 'app'.
            neutralCallback: '&',   // Callback function called when moving back. Takes a travel app param named 'app'.
            negativeCallback: '&'   // Callback function called when canceling. Takes a travel app param named 'app'.
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/component/edit-application/perdiem-overrides-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.dirtyApp = angular.copy(scope.app);

            scope.next = function () {
                scope.positiveCallback({app: scope.dirtyApp});
            };

            scope.back = function () {
                scope.neutralCallback({app: scope.dirtyApp});
            };

            scope.cancel = function () {
                scope.negativeCallback({app: scope.dirtyApp});
            }
        }
    }
}