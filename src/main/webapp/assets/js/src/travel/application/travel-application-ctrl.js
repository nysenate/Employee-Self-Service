var essTravel = angular.module('essTravel');

/**
 * The Main/Parent controller for the travel application form.
 * This controller controls data updates and the page of the application.
 *
 * Each page of the application is implemented as its own directive (defined below).
 * These page directives don't modify the application directly, but call callback functions
 * that are defined in this Parent controller.
 */
essTravel.controller('TravelApplicationCtrl',
                     ['$scope', '$q', 'appProps', 'modals', 'LocationService', 'TravelApplicationInitApi', 'TravelApplicationPurposeApi',
                      'TravelApplicationOutboundApi', 'TravelApplicationReturnApi', 'TravelApplicationExpensesApi', 'TravelApplicationSubmitApi',
                      'TravelModeOfTransportationApi', 'TravelApplicationCancelApi', 'AddressGeocoder', travelAppController]);

function travelAppController($scope, $q, appProps, modals, locationService, appInitApi, purposeApi,
                             outboundApi, returnApi, expensesApi, submitApi, motApi, cancelApi, geocoder) {

    /**
     * States and Actions
     * State - The currently active page of the application process.
     * Action - The valid actions users can perform at any particular state.
     *      ACTIONS.CANCEL - will prompt the user to cancel/delete their current application.
     *      ACTIONS.BACK - will return to the previous state without saving any modifications.
     *      ACTIONS.NEXT - will go to the next state and save all data entered or modifications made.
     */
    $scope.ACTIONS = {
        CANCEL: 5,
        BACK: 10,
        NEXT: 15
    };

    $scope.STATES = {
        PURPOSE: 5,
        OUTBOUND: 10,
        RETURN: 20,
        ALLOWANCES: 25,
        REVIEW: 30
    };

    // The current state.
    $scope.pageState = undefined;

    // The users travel application.
    $scope.app = undefined;

    this.$onInit = function () {
        console.log("parent oninit");
    };

    console.log("parent");
    function init() {
        $scope.pageState = $scope.STATES.PURPOSE;
        initApplication(appProps.user.employeeId);
        initMethodsOfTravel();
    }

    init();

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
        appInitApi.save({empId: travelerId}, {}, function (response) {
            $scope.app = response.result;
            if (hasUncompleteApplication()) {
                modals.open('travel-continue-application-modal')
                    .catch(function () { // Restart application on modal rejection.
                        cancelApplication()
                    });
            }
        }, $scope.handleErrorResponse);

        function hasUncompleteApplication() {
            return $scope.app.purposeOfTravel.length > 0;
        }
    }

    function initMethodsOfTravel() {
        motApi.get({}, function (response) {
            $scope.modesOfTransportation = response.result;
        }, $scope.handleErrorResponse);
    }

    function cancelApplication() {
        cancelApi.remove({empId: $scope.app.traveler.employeeId})
            .$promise
            .then(reload)
            .catch($scope.handleErrorResponse)
    }

    /**
     * updateStates is called by every page's callback function to determine what page should be displayed next.
     * @param action the $scope.ACTION done by the user.
     */
    function updateStates(action) {
        console.log($scope.app);
        if (action === $scope.ACTIONS.CANCEL) {
            handleCancelAction();
        }
        else if (action === $scope.ACTIONS.BACK) {
            handleBackAction();
        }
        else if (action === $scope.ACTIONS.NEXT) {
            handleNextAction();
        }

        function handleCancelAction() {
            modals.open("cancel-application").then(function () {
                modals.resolve({});
                $scope.openLoadingModal();
                cancelApplication();
            })
        }

        function handleBackAction() {
            switch ($scope.pageState) {
                case $scope.STATES.PURPOSE:
                    // Cant go back from Purpose.
                    break;
                case $scope.STATES.OUTBOUND:
                    $scope.pageState = $scope.STATES.PURPOSE;
                    break;
                case $scope.STATES.RETURN:
                    $scope.pageState = $scope.STATES.OUTBOUND;
                    break;
                case $scope.STATES.ALLOWANCES:
                    $scope.pageState = $scope.STATES.RETURN;
                    break;
                case $scope.STATES.REVIEW:
                    $scope.pageState = $scope.STATES.ALLOWANCES;
                    break;
            }
        }

        function handleNextAction() {
            switch ($scope.pageState) {
                case $scope.STATES.PURPOSE:
                    $scope.pageState = $scope.STATES.OUTBOUND;
                    break;
                case $scope.STATES.OUTBOUND:
                    $scope.pageState = $scope.STATES.RETURN;
                    break;
                case $scope.STATES.RETURN:
                    $scope.pageState = $scope.STATES.ALLOWANCES;
                    break;
                case $scope.STATES.ALLOWANCES:
                    $scope.pageState = $scope.STATES.REVIEW;
                    break;
                case $scope.STATES.REVIEW:
                    // There is no next state. App should of been submitted.
                    break;
            }
        }
    }

    /** ----- Callback Functions -----
     *
     * These take the user action and user entered data specific to that page.
     * If the action = ACTIONS.NEXT, the application will be updated with the new data.
     */

    $scope.purposeCallback = function (action, purpose) {
        if (action === $scope.ACTIONS.NEXT) {
            purposeApi.update({id: $scope.app.id}, purpose, function(response) {
                updateAppFromResponse(response);
                updateStates(action);
            }).$promise
                .catch(catchErrorResponse)
        }
        else {
            updateStates(action);
        }
    };

    $scope.outboundCallback = function (action, route) {
        if (action === $scope.ACTIONS.NEXT) {
            outboundApi.update({id: $scope.app.id}, route, function(response) {
                updateAppFromResponse(response);
                updateStates(action);
            }).$promise
                .catch(catchErrorResponse)
        }
        else {
            updateStates(action);
        }
    };

    $scope.returnCallback = function (action, route) {
        if (action === $scope.ACTIONS.NEXT) {
            $scope.openLoadingModal();
            returnApi.update({id: $scope.app.id}, route, function(response) {
                updateAppFromResponse(response);
                updateStates(action);
                $scope.closeLoadingModal();
            }).$promise
                .catch(catchErrorResponse)
        }
        else {
            updateStates(action);
        }
    };

    $scope.allowancesCallback = function (action, destinations, allowances) {
        if (action === $scope.ACTIONS.NEXT) {
            // Default empty allowances to 0.
            for (var prop in allowances) {
                if (!allowances[prop]) {
                    allowances[prop] = 0;
                }
            }
            expensesApi.update({id: $scope.app.id}, {destinations: destinations,
                allowances: allowances}, function(response) {
                updateAppFromResponse(response);
                updateStates(action);
            }).$promise
                .catch(catchErrorResponse)
        }
        else {
            updateStates(action);
        }
    };

    $scope.reviewCallback = function (action) {
        if (action === $scope.ACTIONS.NEXT) {
            modals.open("submit-progress");
            submitApi.update({id: $scope.app.id}).$promise
                .then(function (response) {
                    modals.resolve({});
                })
                .then(function () {
                    modals.open("submit-results").then(function () {
                        locationService.go("/travel/application/travel-application", true);
                    });
                })
                .catch($scope.handleErrorResponse);
        }
        updateStates(action);
    };

    /** ----- Misc Functions ----- */

    function updateAppFromResponse(response) {
        $scope.app = response.result;
    }

    $scope.openLoadingModal = function() {
        modals.open('loading');
    };

    $scope.closeLoadingModal = function() {
        if (modals.isTop('loading')) {
            modals.resolve();
        }
    };

    function catchErrorResponse(response) {
        if (modals.isOpen('loading')) {
            $scope.closeLoadingModal();
        }
        $scope.handleErrorResponse(response);
    }

    function reload() {
        locationService.go("/travel/application/travel-application", true);
    }

    $scope.numDistinctModesOfTransportation = function (app) {
        var mots = (app.route.outboundLegs.concat(app.route.returnLegs)).map(function(leg) {
            return leg.modeOfTransportation.description;
        });

        console.log(mots);
        var distinct = _.uniq(mots);
        console.log(distinct);
        return distinct.length;
    };

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

}

/**
 * Validators
 */

/**
 * Validator for Method of Transportation select elements.
 * Requires that a method of transportation is selected.
 */
essTravel.directive('motValidator', function () {
    return {
        require: 'ngModel',
        link: function ($scope, elm, attrs, ctrl) {
            ctrl.$validators.motValidator = function (modelValue, viewValue) {
                if (modelValue && modelValue.methodOfTravel == null) {
                    return false;
                }
                return true;
            }
        }
    }
});

essTravel.directive('dateValidator', function() {
    return {
        require: 'ngModel',
        link: function($scope, elm, attrs, ctrl) {
            ctrl.$validators.dateValidator = function (modelValue, viewValue) {
                if (!modelValue) {
                    return false;
                }
                if (moment(modelValue, 'MM/DD/YYYY', true).isValid()) {
                    return true;
                }
            }
        }
    }
});

/**
 * Validates address are selected from the google autocomplete.
 * Elements using this must have an ng-model in the form of
 * 'leg.from.formattedAddress' or 'leg.to.formattedAddress'
 */
essTravel.directive('addressValidator', function() {
    return {
        require: 'ngModel',
        link: function($scope, elm, attrs, ctrl) {
            elm.on('keydown', function(e) {
                // Reset address when manually edited.
                var address = $scope.leg[attrs.ngModel.split('.')[1]];
                address.addr1 = '';
                address.addr2 = '';
                address.city = '';
                address.county = '';
                address.state = '';
                address.zip4 = '';
                address.zip5 = '';

            });
            ctrl.$validators.addressValidator = function (modelValue, viewValue) {
                if (!modelValue) {
                    return false;
                }
                // Parse the ng-model attribute to determine if we should validate the 'to' or 'from' address.
                var address = $scope.leg[attrs.ngModel.split('.')[1]];
                if (address.addr1) {
                    return true;
                }
            }
        }
    }
});

function Segment() {
    this.from = {};
    this.to = {};
    this.departureDate = ''; // Use setter to ensure formatted as ISO date.
    this.arrivalDate = ''; // Use setter to ensure formatted as ISO date.
    this.travelDate = '';
    this.modeOfTransportation = undefined;
    this.isMileageRequested = true;
    this.isMealsRequested = true;
    this.isLodgingRequested = true;

    // Used as callback method for travel-address-autocomplete
    this.setFrom = function(address) {
        this.from = address;
    };

    // Used as callback method for travel-address-autocomplete
    this.setTo = function(address) {
        this.to = address;
    };
}

