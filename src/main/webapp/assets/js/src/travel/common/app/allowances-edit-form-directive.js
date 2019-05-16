var essTravel = angular.module('essTravel');

essTravel.directive('essAllowancesEditForm', ['appProps', 'AppEditStateService', 'TravelApplicationByIdApi', allowancesEditForm]);

function allowancesEditForm(appProps, stateService, appIdApi) {
    return {
        restrict: 'E',
        scope: {
            appContainer: '='
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/allowances-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.stateService = stateService;
            scope.dirtyApp = angular.copy(scope.appContainer.app);

            scope.next = function () {
                var patches = {
                    allowances: JSON.stringify(scope.dirtyApp.allowances),
                    mealPerDiems: JSON.stringify(scope.dirtyApp.mealPerDiems),
                    lodgingPerDiems: JSON.stringify(scope.dirtyApp.lodgingPerDiems),
                    mileagePerDiems: JSON.stringify(scope.dirtyApp.mileagePerDiems)
                };
                appIdApi.update({id: scope.appContainer.app.id}, patches, function (response) {
                    scope.appContainer.app = response.result;
                    stateService.nextState();
                }, scope.handleErrorResponse)
            };

            scope.previousDay = function (date) {
                return moment(date).subtract(1, 'days').toDate();
            };

            scope.tripHasLodging = function () {
                return scope.dirtyApp.lodgingPerDiems.allLodgingPerDiems.length > 0;
            };

            scope.tripHasMileage = function () {
                return scope.dirtyApp.mileagePerDiems.qualifyingLegs.length > 0;
            }
        }
    }
}