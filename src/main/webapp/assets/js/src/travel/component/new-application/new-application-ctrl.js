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
        traveler: undefined,
        amendment: undefined,
        allowedTravelers: [],
        eventTypes: []
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
            unsubmittedAppApi.get({}, {}, function (response) {
                $scope.data.allowedTravelers = response.result.allowedTravelers;
                $scope.data.eventTypes = response.result.validEventTypes;
                $scope.data.traveler = response.result.traveler;
                $scope.data.amendment = response.result.amendment;
                if (hasUncompleteApplication()) {
                    modals.open('ess-continue-saved-app-modal')
                        .catch(function () { // Restart application on modal rejection.
                            cancelApplication()
                        });
                }
            }, $scope.handleErrorResponse);

            function hasUncompleteApplication() {
                return $scope.data.amendment.purposeOfTravel && ($scope.data.amendment.purposeOfTravel.eventType !== null);
            }
        }
    };

    $scope.savePurpose = function (amendment) {
        unsubmittedAppApi.update({},
                                 {purposeOfTravel: JSON.stringify(amendment.purposeOfTravel), traveler: $scope.data.traveler.employeeId},
                                 function (response) {
            $scope.data.traveler = response.result.traveler;
            $scope.data.amendment = response.result.amendment;
            console.log(response);
            stateService.setOutboundState();
        }, $scope.handleErrorResponse)
    };

    $scope.saveOutbound = function (amendment) {
        unsubmittedAppApi.update({},
                                 {outbound: JSON.stringify(amendment.route), traveler: $scope.data.traveler.employeeId},
                                 function (response) {
            $scope.data.traveler = response.result.traveler;
            $scope.data.amendment = response.result.amendment;
            stateService.setReturnState();
        }, $scope.handleErrorResponse);
    };

    $scope.saveRoute = function (amendment) {
        $scope.openLoadingModal();
        unsubmittedAppApi.update({},
                                 {route: JSON.stringify(amendment.route), traveler: $scope.data.traveler.employeeId},
                                 function (response) {
            $scope.data.traveler = response.result.traveler;
            $scope.data.amendment = response.result.amendment;
            stateService.setAllowancesState();
            $scope.closeLoadingModal();
        }, function (error) {
            $scope.closeLoadingModal();
            if (error.status === 502) {
                $scope.handleDataProviderError();
            } else if (error.status === 400) {
                $scope.handleTravelDateError();
            } else {
                $scope.handleErrorResponse(error);
            }
        });
    };

    $scope.saveAllowances = function (amendment) {
        var patches = {
            allowances: JSON.stringify(amendment.allowances),
            mealPerDiems: JSON.stringify(amendment.mealPerDiems),
            lodgingPerDiems: JSON.stringify(amendment.lodgingPerDiems),
            mileagePerDiems: JSON.stringify(amendment.route.mileagePerDiems),
            traveler: $scope.data.traveler.employeeId
        };
        unsubmittedAppApi.update({}, patches, function (response) {
            $scope.data.traveler = response.result.traveler;
            $scope.data.amendment = response.result.amendment;
            stateService.setReviewState();
        }, $scope.handleErrorResponse)
    };

    $scope.submitApplication = function () {
        modals.open('submit-confirm')
            .then(function () {
                modals.open("submit-progress");
                unsubmittedAppApi.save().$promise
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

    $scope.cancel = function (app) {
        modals.open("cancel-application").then(function () {
            modals.resolve({});
            $scope.openLoadingModal();
            cancelApplication(app);
        });
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
