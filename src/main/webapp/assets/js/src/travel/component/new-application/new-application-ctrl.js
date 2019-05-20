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
                     ['$scope', '$q', '$window', 'appProps', 'modals', 'LocationService', 'AppEditStateService', 'TravelApplicationApi', 'TravelApplicationByIdApi', travelAppController]);

function travelAppController($scope, $q, $window, appProps, modals, locationService, stateService, appApi, appIdApi) {

    $scope.stateService = stateService;
    // Common data shared between all child controllers.
    $scope.data = {
        app: undefined
    };

    this.$onInit = function () {
        $scope.stateService = stateService;
        $scope.stateService.setPurposeState();
        initApplication(appProps.user.employeeId);

        /**
         * Checks to see if the logged in employee has an uncompleted travel application in progress.
         * If so, return that application so they can continue to work on it.
         * If not, initialize a new application.
         *
         * If continuing a previous application, display modal asking user if they
         * would like to continue working on it or start over.
         * @param travelerId
         */
        function initApplication(travelerId) {
            appApi.get({travelerId: travelerId}, {}, function (response) {
                $scope.data.app = response.result;
                if (hasUncompleteApplication()) {
                    modals.open('ess-continue-saved-app-modal')
                        .catch(function () { // Restart application on modal rejection.
                            cancelApplication() // TODO replace this functionality
                        });
                }
                console.log($scope.data.app.id);
            }, $scope.handleErrorResponse);

            function hasUncompleteApplication() {
                return $scope.data.app.purposeOfTravel !== "";
            }
        }

    };

    function cancelApplication() {
        appIdApi.remove({id: $scope.data.app.id})
            .$promise
            .then(reload)
            .catch($scope.handleErrorResponse)
    }

    function reload() {
        locationService.go("/travel/application/new", true);
    }

    /**
     * ---- Functions shared by child scopes ---
     */



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
    $window.gm_authFailure = function() {
        $scope.$apply(function () {
            $scope.handleErrorResponse("Google maps api authentication error.");
        });
    }
}
