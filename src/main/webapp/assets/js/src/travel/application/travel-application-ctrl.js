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
        ALLOWANCES: 25,
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
        console.log($scope.app);
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
            case $scope.STATES.ALLOWANCES:
                $scope.app.appState = $scope.STATES.DESTINATION;
                $scope.pageState = $scope.STATES.DESTINATION;
                break;
            case $scope.STATES.REVIEW:
                $scope.app.appState = $scope.STATES.ALLOWANCES;
                $scope.pageState = $scope.STATES.ALLOWANCES;
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
                $scope.app.appState = $scope.STATES.ALLOWANCES;
                $scope.pageState = $scope.STATES.ALLOWANCES;
                break;
            case $scope.STATES.ALLOWANCES:
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

    $scope.purposeCallback = function (purpose, action) {
        if (action === $scope.ACTIONS.NEXT) {
            $scope.app.purposeOfTravel = purpose;
        }
        updateStates(action);
    };

    $scope.originCallback = function (origin, action) {
        if (action === $scope.ACTIONS.NEXT) {
            $scope.app.itinerary.origin = origin;
        }
        updateStates(action);
    };

    $scope.destinationCallback = function (destinations, action) {
        if (action === $scope.ACTIONS.NEXT) {
            $scope.app.itinerary.destinations.items = destinations;
        }
        updateStates(action);
    };

    $scope.allowancesCallback = function (destinations, allowances, action) {
        if (action === $scope.ACTIONS.NEXT) {
            $scope.app.allowances = allowances;
            $scope.app.itinerary.destinations.items = destinations;
        }
        updateStates(action);
    };

    $scope.reviewCallback = function (action) {
        if (action === $scope.ACTIONS.NEXT) {
            // Submit?
        }
        updateStates(action);
    };
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
            $scope.origin = angular.copy($scope.app.itinerary.origin);

            $scope.setOrigin = function (address) {
                $scope.origin = address;
            };
        }
    }
}]);


essTravel.directive('travelApplicationDestination', ['appProps', 'modals', function (appProps, modals) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-destination',
        scope: true,
        link: function ($scope, $elem, $attrs) {
            // Copy current destinations for use in this directive. So old values are not overwritten on cancel.
            $scope.destinations = angular.copy($scope.app.itinerary.destinations.items);

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
                var index = $scope.destinations.indexOf(dest);
                $scope.destinations.splice(index, 1);
            };

            $scope.addDestinationOnClick = function() {
                var params = {
                    defaultModeOfTransportation: undefined
                };
                if ($scope.destinations.length > 0) {
                    params.defaultModeOfTransportation = $scope.destinations[$scope.destinations.length - 1].modeOfTransportation
                }
                modals.open('destination-selection-modal', params)
                    .then(function (destination) {
                        $scope.destinations.push(destination);
                    });
            };
        }
    }
}]);

essTravel.directive('travelApplicationAllowances', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-allowances',
        scope: true,
        link: function ($scope, $elem, $attrs) {
            $scope.allowances = angular.copy($scope.app.allowances);

            $scope.destinations = angular.copy($scope.app.itinerary.destinations.items);
            // Initialize default reimbursement selections.
            $scope.destinations.forEach(function (dest) {
                // Mileage
                if (dest.modeOfTransportation === 'Personal Auto') {
                    dest.requestMileage = true;
                }

                // Meals
                dest.requestMeals = true;

                // Lodging
                var arrival = moment(dest.arrivalDate);
                var departure = moment(dest.departureDate);
                if (Math.abs(arrival.diff(departure, 'days')) > 0) {
                    dest.requestLodging = true;
                }
            });
        }
    }
}]);

essTravel.directive('travelApplicationReview', ['appProps', '$q', 'modals', 'TravelGsaAllowanceApi',
                                                'TravelMileageAllowanceApi', function (appProps, $q, modals, gsaApi, mileageAllowanceApi) {
        return {
            templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-review',
            scope: true,
            link: function ($scope, $elem, $attrs) {

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
                    });

                function sumAllowances() {
                    $scope.app.allowances.total = parseFloat($scope.app.allowances.gsa.meals)
                        + parseFloat($scope.app.allowances.gsa.lodging)
                        + parseFloat($scope.app.allowances.mileage)
                        + $scope.app.allowances.tolls
                        + $scope.app.allowances.parking
                        + $scope.app.allowances.alternate
                        + $scope.app.allowances.registrationFee;
                }
            }
        }
    }]);
