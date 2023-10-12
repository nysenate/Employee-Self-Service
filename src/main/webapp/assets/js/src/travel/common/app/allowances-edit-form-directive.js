var essTravel = angular.module('essTravel');

essTravel.directive('essAllowancesEditForm', ['appProps', 'TravelDraftsApi', allowancesEditForm]);

function allowancesEditForm(appProps, draftsApi) {
    return {
        restrict: 'E',
        scope: {
            data: '<',               // The application being edited.
            positiveCallback: '&',   // Callback function called when continuing. Takes a draft param named 'draft'.
            neutralCallback: '&',   // Callback function called when moving back. Takes a draft param named 'draft'.
            negativeCallback: '&',  // Callback function called when canceling. Takes a draft param named 'draft'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/allowances-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.mode = scope.data.mode;
            scope.dirtyDraft = angular.copy(scope.data.draft);

            scope.next = function () {
                scope.openLoadingModal();
                draftsApi.update(
                    {
                        options: ['ALLOWANCES', 'MEAL_PER_DIEMS', 'LODGING_PER_DIEMS', 'MILEAGE_PER_DIEMS'],
                        draft: scope.dirtyDraft
                    })
                    .$promise
                    .then(function (res) {
                        scope.dirtyDraft = res.result;
                        scope.positiveCallback({draft: scope.dirtyDraft});
                    })
                    .catch(function (error) {
                        scope.$parent.handleErrorResponse(error);
                    })
                    .finally(scope.closeLoadingModal)
            };

            scope.save = function () {
                scope.saveDraft(scope.dirtyDraft)
                    .then(function (draft) {
                        scope.dirtyDraft = draft;
                    });
            }

            scope.nextDay = function (date) {
                return moment(date).add(1, 'days').toDate();
            };

            scope.tripHasMeals = function () {
                return scope.dirtyDraft.amendment.mealPerDiems.allMealPerDiems.length > 0
                    && scope.dirtyDraft.amendment.mealPerDiems.isAllowedMeals;
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

            scope.setLodgingPerDiemAddress = function(lpd, address) {
                lpd.address = address;
                lpd.rate = '0'; // Since the address changed, rate is no longer valid. The backend will have to update it.
            }
        }
    }
}