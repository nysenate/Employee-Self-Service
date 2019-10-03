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
                     ['$scope', '$window', 'appProps', 'modals', 'LocationService', 'AppEditStateService', 'UnsubmittedAppApi', 'TravelApplicationByIdApi', travelAppController]);

function travelAppController($scope, $window, appProps, modals, locationService, stateService, unsubmittedAppApi, appIdApi) {

    $scope.stateService = stateService;
    // Common data shared between all child controllers.
    $scope.data = {
        app: undefined,
        allowedTravelers: []
    };

    this.$onInit = function () {
        $scope.stateService = stateService;
        $scope.stateService.setPurposeState();
        initApplication();

        /**
         * Checks to see if the logged in employee has an uncompleted travel application in progress.
         * If so, return that application so they can continue to work on it.
         * If not, initialize a new application.
         *
         * If continuing a previous application, display modal asking user if they
         * would like to continue working on it or start over.
         */
        function initApplication() {
            unsubmittedAppApi.get({userId: appProps.user.employeeId}, {}, function (response) {
                $scope.data.app = response.result.app;
                $scope.data.allowedTravelers = response.result.allowedTravelers;
                if (hasUncompleteApplication()) {
                    modals.open('ess-continue-saved-app-modal')
                        .catch(function () { // Restart application on modal rejection.
                            cancelApplication($scope.data.app) // TODO replace this functionality
                        });
                }
            }, $scope.handleErrorResponse);

            function hasUncompleteApplication() {
                return $scope.data.app.purposeOfTravel.eventType !== null;
            }
        }
    };

    $scope.savePurpose = function (app) {
        console.log(app);
        unsubmittedAppApi.update({userId: appProps.user.employeeId},
                                 {purposeOfTravel: JSON.stringify(app.purposeOfTravel), traveler: $scope.data.app.traveler.employeeId},
                                 function (response) {
            $scope.data.app = response.result;
            stateService.setOutboundState();
        }, $scope.handleErrorResponse)
    };

    $scope.saveOutbound = function (app) {
        unsubmittedAppApi.update({userId: appProps.user.employeeId},
                                 {outbound: JSON.stringify(app.route), traveler: $scope.data.app.traveler.employeeId},
                                 function (response) {
            $scope.data.app = response.result;
            stateService.setReturnState();
        }, $scope.handleErrorResponse);
    };

    $scope.saveRoute = function (app) {
        $scope.openLoadingModal();
        unsubmittedAppApi.update({userId: appProps.user.employeeId},
                                 {route: JSON.stringify(app.route), traveler: $scope.data.app.traveler.employeeId},
                                 function (response) {
            $scope.data.app = response.result;
            stateService.setAllowancesState();
            $scope.closeLoadingModal();
        }, function (error) {
            $scope.closeLoadingModal();
            if (error.status === 502) {
                $scope.handleDataProviderError();
            } else {
                $scope.handleErrorResponse(error);
            }
        });
    };

    $scope.saveAllowances = function (app) {
        var patches = {
            allowances: JSON.stringify(app.allowances),
            mealPerDiems: JSON.stringify(app.mealPerDiems),
            lodgingPerDiems: JSON.stringify(app.lodgingPerDiems),
            mileagePerDiems: JSON.stringify(app.route.mileagePerDiems),
            traveler: $scope.data.app.traveler.employeeId
        };
        unsubmittedAppApi.update({userId: appProps.user.employeeId}, patches, function (response) {
            $scope.data.app = response.result;
            stateService.setReviewState();
        }, $scope.handleErrorResponse)
    };

    $scope.submitApplication = function (app) {
        // No need to confirm submit for initial launch as apps are not being sent to anyone.
        // modals.open('submit-confirm')
        //     .then(function () {
                modals.open("submit-progress");
                unsubmittedAppApi.save({userId: appProps.user.employeeId}, {}).$promise
                    .then(function (response) {
                        $scope.data.app = response.result;
                        modals.resolve({});
                    })
                    .then(function () {
                        modals.open("submit-results").then(function () {
                            locationService.go("/travel", true);
                        });
                    })
                    .catch($scope.handleErrorResponse);
            // })
    };

    $scope.cancel = function (app) {
        modals.open("cancel-application").then(function () {
            modals.resolve({});
            $scope.openLoadingModal();
            cancelApplication(app);
        });
    };

    $scope.toPurposeState = function (app) {
        $scope.stateService.setPurposeState();
    };

    $scope.toOutboundState = function (app) {
        $scope.stateService.setOutboundState();
    };

    $scope.toReturnState = function (app) {
        $scope.stateService.setReturnState();
    };

    $scope.toAllowancesState = function (app) {
        $scope.stateService.setAllowancesState();
    };

    function cancelApplication(app) {
        unsubmittedAppApi.remove({userId: appProps.user.employeeId})
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
