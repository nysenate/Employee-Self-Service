var essTravel = angular.module('essTravel');

essTravel.directive('essPerdiemOverridesEditForm', ['appProps', 'TravelDraftsApi', perDiemOverrideEditForm]);

function perDiemOverrideEditForm(appProps, draftsApi) {
    return {
        restrict: 'E',
        scope: {
            data: '<',         // The application being edited.
            positiveCallback: '&',  // Callback function called when continuing. Takes a travel app param named 'app'.
            neutralCallback: '&',   // Callback function called when moving back. Takes a travel app param named 'app'.
            negativeCallback: '&',  // Callback function called when canceling. Takes a travel app param named 'app'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/component/edit-application/perdiem-overrides-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.dirtyDraft = angular.copy(scope.data.draft);
            // scope.dirtyAmendment = angular.copy(scope.amendment);

            scope.perdiems = [
                Object.assign(scope.dirtyDraft.amendment.mealPerDiems, {name: 'Meals'}),
                Object.assign(scope.dirtyDraft.amendment.lodgingPerDiems, {name: 'Lodging'})
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
                        scope.handleErrorResponse(error);
                    })
                    .finally(scope.closeLoadingModal)
            };

            scope.back = function () {
                scope.neutralCallback({draft: scope.dirtyDraft});
            };

            scope.cancel = function () {
                scope.negativeCallback({draft: scope.dirtyDraft});
            };

            // Use undefined instead of 0 in the override input boxes so they are empty instead of 0 when not overridden.
            function zeroToUndefined(value) {
                return value === 0 ? undefined : value;
            }
        }
    }
}