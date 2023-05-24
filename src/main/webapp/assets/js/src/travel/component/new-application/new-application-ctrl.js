var essTravel = angular.module('essTravel');

/**
 * The Main/Parent controller for the travel application form.
 * This controller controls data updates and the page of the application.
 *
 * Each page of the application is implemented as its own directive (defined below).
 * These page directives don't modify the application directly, but call callback functions
 * that are defined in this Parent controller.
 */
essTravel.controller('NewApplicationCtrl',
                     ['$scope', '$window', 'appProps', 'modals', 'LocationService', 'AppEditStateService',
                      'UnsubmittedAppApi', 'TravelApplicationByIdApi', 'TravelDraftsApi',
                      travelAppController]);

function travelAppController($scope, $window, appProps, modals, locationService, stateService,
                             unsubmittedAppApi, appIdApi, draftsApi) {

    $scope.stateService = stateService;
    // Common data shared between all child controllers.
    $scope.data = {
        draft: {}
    };
    $scope.isLoading = true;

    this.$onInit = function () {
        $scope.stateService = stateService;
        $scope.stateService.setPurposeState();
        initApplication();

        function initApplication() {
            draftsApi.create().$promise.then(function (res) {
                $scope.data.draft = res.result;
                $scope.isLoading = false;
            });
        }
    };

    $scope.savePurpose = function (draft) {
        $scope.data.draft = draft;
        stateService.setOutboundState();
    };

    $scope.saveOutbound = function (draft) {
        $scope.data.draft = draft;
        stateService.setReturnState();
    };

    $scope.saveRoute = function (draft) {
        if (!_.isEqual(draft.amendment.route, $scope.data.draft.amendment.route)) {
            $scope.openLoadingModal();
            draftsApi.update(
                {
                    options: ['ROUTE'],
                    draft: draft
                })
                .$promise
                .then(function (res) {
                    $scope.data.draft = res.result;
                    stateService.setAllowancesState();
                })
                .catch(function (error) {
                    if (error.status === 502) {
                        $scope.handleDataProviderError();
                    } else if (error.status === 400) {
                        $scope.handleTravelDateError();
                    } else {
                        $scope.handleErrorResponse(error);
                    }
                })
                .finally($scope.closeLoadingModal)
        } else {
            stateService.setAllowancesState();
        }
    };

    $scope.saveAllowances = function (draft) {
        console.log(draft);
        $scope.openLoadingModal();
        draftsApi.update(
            {
                options: ['ALLOWANCES', 'MEAL_PER_DIEMS', 'LODGING_PER_DIEMS', 'MILEAGE_PER_DIEMS'],
                draft: draft
            })
            .$promise
            .then(function (res) {
                $scope.data.draft = res.result;
                stateService.setReviewState();
            })
            .catch(function (error) {
                $scope.handleErrorResponse(error);
            })
            .finally($scope.closeLoadingModal)
    };

    $scope.submitApplication = function () {
        modals.open('submit-confirm')
            .then(function () {
                modals.open("submit-progress");
                draftsApi.save($scope.data.draft).$promise
                    .then(function (response) {
                        $scope.data.traveler = response.result.traveler;
                        $scope.data.amendment = response.result.amendment;
                        modals.resolve({});
                    })
                    .then(function () {
                        modals.open("submit-results").then(function () {
                            locationService.go("/travel", true);
                        }, function () {
                            locationService.logout();
                        });
                    })
                    .catch($scope.handleErrorResponse);
            })
    };

    $scope.cancel = function (draft) {
        // modals.open("cancel-application").then(function () {
        //     modals.resolve({});
        //     $scope.openLoadingModal();
        //     cancelApplication(app);
        // });
    };

    $scope.toPurposeState = function () {
        $scope.stateService.setPurposeState();
    };

    $scope.toOutboundState = function () {
        $scope.stateService.setOutboundState();
    };

    $scope.toReturnState = function () {
        $scope.stateService.setReturnState();
    };

    $scope.toAllowancesState = function () {
        $scope.stateService.setAllowancesState();
    };

    function cancelApplication() {
        unsubmittedAppApi.remove()
            .$promise
            .then(reload)
            .catch($scope.handleErrorResponse)
    }

    function reload() {
        locationService.go("/travel/application/new", true);
    }

    $scope.openLoadingModal = function () {
        modals.open('loading');
    };

    $scope.closeLoadingModal = function () {
        if (modals.isTop('loading')) {
            modals.resolve();
        }
    };

    $scope.handleDataProviderError = function () {
        modals.open("external-api-error")
            .then(function () {
                reload();
            })
            .catch(function () {
                locationService.go("/logout", true);
            });
    };

    $scope.handleTravelDateError = function () {
        modals.open("travel-date-error-modal")
            .then(function () {
            })
    };

    /**
     * Error handler for google maps api.
     * Docs: https://developers.google.com/maps/documentation/javascript/events#auth-errors
     * and https://developers.google.com/maps/documentation/javascript/error-messages
     */
    $window.gm_authFailure = function () {
        $scope.$apply(function () {
            $scope.handleErrorResponse("Google maps api authentication error.");
        });
    }
}
