var essTravel = angular.module('essTravel');

essTravel.directive('essAllowancesEditForm', ['appProps', 'TravelDraftsApi', 'TravelLodgingPerDiemsApi', allowancesEditForm]);

function allowancesEditForm(appProps, draftsApi, lodgingPerDiemsApi) {
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
                    && scope.dirtyDraft.amendment.mealPerDiems.isAllowedMeals
                    && scope.dirtyDraft.amendment.mealPerDiems.totalPerDiem !== "0.00";
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

            scope.setLodgingPerDiemAddress = function (lpd, address) {
                scope.openLoadingModal();
                lodgingPerDiemsApi.save(
                    {
                        "date": lpd.date,
                        "address": address
                    })
                    .$promise
                    .then(function (res) {
                        // Update one field at a time.
                        lpd.address = res.result.address;
                        lpd.date = res.result.date;
                        lpd.rate = res.result.rate;
                        lpd.reimbursementRequested = res.result.reimbursementRequested;
                        lpd.isReimbursementRequested = res.result.isReimbursementRequested;
                        lpd.maximumPerDiem = res.result.maximumPerDiem;
                        lpd.requestedPerDiem = res.result.requestedPerDiem;
                    })
                    .finally(scope.closeLoadingModal)
            }
        }
    }
}