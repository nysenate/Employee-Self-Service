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
                     ['$scope', '$q', '$window', 'appProps', 'modals', 'LocationService','TravelApplicationApi', 'TravelApplicationByIdApi',
                      'TravelModeOfTransportationApi', 'AddressCountyService', travelAppController]);

function travelAppController($scope, $q, $window, appProps, modals, locationService, appApi, appIdApi, motApi, countyService) {

    $scope.STATES = {
        PURPOSE: 1,
        OUTBOUND: 2,
        RETURN: 3,
        ALLOWANCES: 4,
        REVIEW: 5
    };

    // The current state.
    $scope.pageState = undefined;

    // Common data shared between all child controllers.
    $scope.data = {
        app: undefined
    };

    this.$onInit = function () {
        $scope.pageState = $scope.STATES.PURPOSE;
        initApplication(appProps.user.employeeId);
        initMethodsOfTravel();

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
            }, $scope.handleErrorResponse);

            function hasUncompleteApplication() {
                return $scope.data.app.purposeOfTravel !== "";
            }
        }

        function initMethodsOfTravel() {
            motApi.get({}, function (response) {
                $scope.methodsOfTravel = [];
                    response.result.forEach(function (modeOfTransportation) {
                        $scope.methodsOfTravel.push(modeOfTransportation.displayName);
                    });
            }, $scope.handleErrorResponse);
        }
    };

    $scope.nextState = function () {
        if ($scope.pageState < $scope.STATES.REVIEW) {
            $scope.pageState++;
        }
    };

    $scope.previousState = function () {
        if ($scope.pageState > $scope.STATES.PURPOSE) {
            $scope.pageState--;
        }
    };

    $scope.cancel = function () {
        modals.open("cancel-application").then(function () {
            modals.resolve({});
            $scope.openLoadingModal();
            cancelApplication();
        });
    };

    function cancelApplication() {
        appIdApi.remove({id: $scope.data.app.id})
            .$promise
            .then(reload)
            .catch($scope.handleErrorResponse)
    }

    $scope.openLoadingModal = function () {
        modals.open('loading');
    };

    $scope.closeLoadingModal = function () {
        if (modals.isTop('loading')) {
            modals.resolve();
        }
    };

    function reload() {
        locationService.go("/travel/application/travel-application", true);
    }

    /**
     * Determines when a step indicator/breadcrumb should be highlighted.
     * @param state
     * @return {boolean}
     */
    $scope.highlightStep = function (state) {
        return state <= $scope.pageState;
    };

    /**
     * Determines if the user is allowed to click on this step indicator.
     * Users can only click on steps they have already completed.
     * @param state
     */
    $scope.stepNavigable = function (state) {
        return state < $scope.pageState;
    };

    /**
     * Attempts to go to state page.
     * Only allowed to go to a page if it goes backwards in the application.
     * @param state an instance of $scope.STATES.
     */
    $scope.gotoStep = function (state) {
        if (state < $scope.pageState) {
            $scope.pageState = state;
        }
    };

    /**
     * ---- Functions shared by child scopes ---
     */

    // Set all form elements as touched so they can be styled appropriately if they have errors.
    $scope.setFormElementsTouched = function (form) {
        for (var prop in form) {
            if (form[prop] && typeof(form[prop].$setTouched) === 'function') {
                form[prop].$setTouched();
            }
        }
    };

    $scope.checkCounties = function (legs) {
        var deferred = $q.defer();
        var addrsMissingCounty = findAddressesWithoutCounty(legs);

        if (addrsMissingCounty.isEmpty) {
            deferred.resolve();
        }
        else {
            $scope.openLoadingModal();
            countyService.updateWithGeocodeCounty(addrsMissingCounty)
                .then(countyService.addressesMissingCounty) // filter out addresses that were updated with a county.
                .then(countyService.promptUserForCounty)
                .then($scope.closeLoadingModal)
                .then(function () {
                    deferred.resolve();
                })
                .catch(function () {
                    deferred.reject();
                })
        }
        return deferred.promise;


        function findAddressesWithoutCounty(legs) {
            var addresses = legs.map(function (leg) {
                return leg.from;
            }).concat(legs.map(function (leg) {
                return leg.to;
            }));
            return countyService.addressesMissingCounty(addresses);
        }
    };

    /**
     * Modifies the given legs, normalizing each travel date to MM/DD/YYYY format.
     * @param legs
     */
    $scope.normalizeTravelDates = function (legs) {
        angular.forEach(legs, function (leg) {
            if (leg.travelDate) {
                leg.travelDate = $scope.normalizedDateFormat(leg.travelDate);
            }
        })
    };

    /**
     * Returns a date representing the given date in MM/DD/YYYY format.
     * Returns undefined if the given date format cannot be converted.
     * @param date String representation of a date.
     */
    $scope.normalizedDateFormat = function (date) {
        if (moment(date, 'M/D/YY', true).isValid()) {
            return moment(date, 'M/D/YY', true).format("MM/DD/YYYY");
        }
        else if (moment(date, 'M/D/YYYY', true).isValid()) {
            return moment(date, 'M/D/YYYY', true).format("MM/DD/YYYY");
        }
        return undefined;
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
    $window.gm_authFailure = function() {
        $scope.$apply(function () {
            $scope.handleErrorResponse("Google maps api authentication error.");
        });
    }
}

function Leg () {
    this.from = new Destination();
    this.to = new Destination();
    this.travelDate = "";
}

function Destination () {
    this.address = new Address();
}

function Address () {
    this.addr1 = "";
    this.addr2 = "";
    this.city = "";
    this.county = "";
    this.state = "";
    this.zip4 = "";
    this.zip5 = "";
    this.country = "";
}