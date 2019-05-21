var essTravel = angular.module('essTravel');

essTravel.directive('essAllowancesEditForm', ['appProps', allowancesEditForm]);

function allowancesEditForm(appProps) {
    return {
        restrict: 'E',
        scope: {
            app: '<',               // The application being edited.
            title: '@',             // The title
            positiveCallback: '&',   // Callback function called when continuing. Takes a travel app param named 'app'.
            neutralCallback: '&',   // Callback function called when moving back. Takes a travel app param named 'app'.
            negativeCallback: '&'   // Callback function called when canceling. Takes a travel app param named 'app'.
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/allowances-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.dirtyApp = angular.copy(scope.app);

            scope.next = function () {
                scope.positiveCallback({app: scope.dirtyApp});
            };

            scope.previousDay = function (date) {
                return moment(date).subtract(1, 'days').toDate();
            };

            scope.tripHasLodging = function () {
                return scope.dirtyApp.lodgingPerDiems.allLodgingPerDiems.length > 0;
            };

            scope.tripHasMileage = function () {
                return scope.dirtyApp.mileagePerDiems.qualifyingLegs.length > 0;
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