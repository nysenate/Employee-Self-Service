var essTravel = angular.module('essTravel');

essTravel.controller('NewTravelApplicationCtrl',
                     ['$scope', '$q', 'appProps', 'modals', 'LocationService', 'TravelGsaAllowanceApi',
                      'TravelMileageAllowanceApi', 'TravelApplicationApi', travelAppController]);

function travelAppController($scope, $q, appProps, modals, locationService, gsaApi,
                             mileageAllowanceApi, travelApplicationApi) {

    /* --- Container Page --- */

    // Actions that can be performed by each page of the application.
    $scope.ACTIONS = {
        CANCEL: 5,
        BACK: 10,
        NEXT: 15
    };

    $scope.STATES = {
        PURPOSE: 5,
        ORIGIN: 10,
        DESTINATION: 15,
        LOCATION_ALLOWANCES: 20,
        OTHER_ALLOWANCES: 25,
        REVIEW: 30,
        EDIT: 35
    };

    $scope.pageState = undefined; // Controls which page is displayed to the user, must be one of $scope.STATES, but should not be EDIT.
    $scope.app = {
        appState: undefined, // The state of the application, i.e. what page we are on.
        traveler: undefined,
        allowances: {
            gsa: {
                meals: 0,
                lodging: 0,
                incidental: 0
            },
            mileage: 0,
            tolls: 0,
            parking: 0,
            alternate: 0,
            registrationFee: 0
        },
        itinerary: {
            origin: undefined,
            destinations: {
                items: []
            }
        },
        purposeOfTravel: ''
    };

    function init() {
        $scope.pageState = $scope.STATES.PURPOSE;
        $scope.app.appState = $scope.STATES.PURPOSE;
        $scope.app.traveler = appProps.user;
    }

    init();

    /**
     * Updates the app.pageState and $scope.pageState after an action has occurred.
     * @param action
     */
    function updateStates(action) {
        if (action === $scope.ACTIONS.CANCEL) {
            handleCancelAction();
        }
        else if(action === $scope.ACTIONS.BACK) {
            handleBackAction();
        }
        else if(action === $scope.ACTIONS.NEXT) {
            handleNextAction();
        }
    }

    function handleCancelAction() {
        // TODO: Implement
    }

    function handleBackAction() {
        switch ($scope.app.appState) {
            case $scope.STATES.PURPOSE:
                // TODO: Cancel order? Should Back be valid on first page?
                break;
            case $scope.STATES.ORIGIN:
                $scope.app.appState = $scope.STATES.PURPOSE;
                $scope.pageState = $scope.STATES.PURPOSE;
                break;
            case $scope.STATES.DESTINATION:
                $scope.app.appState = $scope.STATES.ORIGIN;
                $scope.pageState = $scope.STATES.ORIGIN;
                break;
            case $scope.STATES.LOCATION_ALLOWANCES:
                $scope.app.appState = $scope.STATES.DESTINATION;
                $scope.pageState = $scope.STATES.DESTINATION;
                break;
            case $scope.STATES.OTHER_ALLOWANCES:
                $scope.app.appState = $scope.STATES.LOCATION_ALLOWANCES;
                $scope.pageState = $scope.STATES.LOCATION_ALLOWANCES;
                break;
            case $scope.STATES.REVIEW:
                $scope.app.appState = $scope.STATES.OTHER_ALLOWANCES;
                $scope.pageState = $scope.STATES.OTHER_ALLOWANCES;
                break;
            case $scope.STATES.EDIT:
                $scope.pageState = $scope.STATES.REVIEW;
                break;
        }
    }

    function handleNextAction() {
        switch ($scope.app.appState) {
            case $scope.STATES.PURPOSE:
                $scope.app.appState = $scope.STATES.ORIGIN;
                $scope.pageState = $scope.STATES.ORIGIN;
                break;
            case $scope.STATES.ORIGIN:
                $scope.app.appState = $scope.STATES.DESTINATION;
                $scope.pageState = $scope.STATES.DESTINATION;
                break;
            case $scope.STATES.DESTINATION:
                $scope.app.appState = $scope.STATES.LOCATION_ALLOWANCES;
                $scope.pageState = $scope.STATES.LOCATION_ALLOWANCES;
                break;
            case $scope.STATES.LOCATION_ALLOWANCES:
                $scope.app.appState = $scope.STATES.OTHER_ALLOWANCES;
                $scope.pageState = $scope.STATES.OTHER_ALLOWANCES;
                break;
            case $scope.STATES.OTHER_ALLOWANCES:
                $scope.app.appState = $scope.STATES.REVIEW;
                $scope.pageState = $scope.STATES.REVIEW;
                break;
            case $scope.STATES.REVIEW:
                // TODO: Submit application
                break;
            case $scope.STATES.EDIT:
                $scope.pageState = $scope.STATES.REVIEW;
                break;
        }
    }

    $scope.purposeCallBack = function (purpose, action) {
        if (action === $scope.ACTIONS.NEXT) {
            $scope.app.purposeOfTravel = purpose;
        }
        updateStates(action);
    };

    $scope.originCallBack = function (origin, action) {
         if (action === $scope.ACTIONS.NEXT) {
            $scope.app.origin = origin;
        }
        updateStates(action);
    };




    /* --- Location Selection --- */

    $scope.setOrigin = function (address) {
        $scope.app.itinerary.origin = address;
    };

    $scope.addDestinationOnClick = function() {
        var params = {
            defaultModeOfTransportation: undefined
        };
        if ($scope.app.itinerary.destinations.items.length > 0) {
            params.defaultModeOfTransportation = $scope.app.itinerary.destinations.items[$scope.app.itinerary.destinations.items.length - 1].modeOfTransportation
        }
        modals.open('destination-selection-modal', params)
            .then(function (destination) {
                $scope.app.itinerary.destinations.items.push(destination);
            });
    };

    $scope.editDestination = function (dest) {
        var params = {
            destination: dest,
            defaultModeOfTransportation: undefined
        };
        modals.open('destination-selection-modal', params)
            .then(function (destination) {
                dest = destination;
            });
    };

    $scope.removeDestination = function(dest) {
        var index = $scope.app.itinerary.destinations.items.indexOf(dest);
        $scope.app.itinerary.destinations.items.splice(index, 1);
    };

    $scope.locationsCompleted = function() {
        return $scope.app.itinerary.origin && $scope.app.itinerary.destinations.items.length > 0;
    };

    $scope.toMethodAndPurpose = function() {
        $scope.state = STATES[1];
    };

    /* --- Method and Purpose --- */

    $scope.methodAndPurposeCompleted = function () {
        return $scope.app.allowances.tolls != null
            && $scope.app.allowances.parking != null
            && $scope.app.allowances.alternate != null
            && $scope.app.allowances.registrationFee != null
            && $scope.app.purposeOfTravel != null;
    };

    $scope.toReviewAndSubmit = function () {
        $scope.state = STATES[2];
        $scope.initReviewAndSubmit();
    };

    $scope.backToLocationSelection = function () {
        $scope.state = STATES[0];
    };

    /* --- Review and Submit --- */

    $scope.initReviewAndSubmit = function () {
        var promises = [];
        modals.open('calculating-allowances');

        promises.push(gsaApi.save($scope.app.itinerary, function (response) {
            $scope.app.allowances.gsa = response.result;
        }).$promise);

        promises.push(mileageAllowanceApi.save($scope.app.itinerary, function (response) {
            $scope.app.allowances.mileage = response.result.mileage;
        }).$promise);

        $q.all(promises)
            .then(function () {
                sumAllowances();
                modals.resolve({});
            })
    };

    function sumAllowances() {
        $scope.app.allowances.total = parseFloat($scope.app.allowances.gsa.meals)
            + parseFloat($scope.app.allowances.gsa.lodging)
            + parseFloat($scope.app.allowances.mileage)
            + $scope.app.allowances.tolls
            + $scope.app.allowances.parking
            + $scope.app.allowances.alternate
            + $scope.app.allowances.registrationFee;
    }

    $scope.submitApplication = function () {
        modals.open('submit-progress');
        travelApplicationApi.save($scope.app, function () {
            modals.resolve();
            locationService.go("/travel/upcoming-travel");
        });
    };

    $scope.backToMethodAndPurpose = function () {
        $scope.state = STATES[1];
    }
}

essTravel.directive('travelApplicationPurpose', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-purpose',
        scope: true,
        link: function ($scope, $elem, $attrs) {
            // Copy current purpose of travel for use in this directive.
            $scope.purposeOfTravel = angular.copy($scope.app.purposeOfTravel);
        }
    }
}]);

essTravel.directive('travelApplicationOrigin', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-origin',
        scope: true,
        link: function ($scope, $elem, $attrs) {
            // Copy current origin for use in this directive.
            $scope.origin = angular.copy($scope.app.origin);
        }
    }
}]);
