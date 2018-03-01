var essTravel = angular.module('essTravel');

essTravel.controller('NewTravelApplicationCtrl',
                     ['$scope', '$q', 'appProps', 'modals', 'LocationService',
                      'TravelMileageAllowanceApi', 'TravelApplicationApi', travelAppController]);

function travelAppController($scope, $q, appProps, modals, locationService,
                             mileageAllowanceApi, travelApplicationApi) {

    /* --- Container Page --- */

    // Actions that can be performed by each page of the application.
    $scope.ACTIONS = {
        CANCEL: 5, // Cancel the entire application.
        BACK: 10, // Go back to previous step of application.
        NEXT: 15, // Go to next step of application
        EDIT: 20 // Edit the field selected. Used in review page, activates the page corresponding to the field to be edited.
    };

    // Each state represents a different step of the application.
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
        // The state of the application, usually the same as $scope.pageState, unless returning to a page for editing
        // in that case appState == EDIT and pageState == the page who's data is being edited.
        appState: undefined,
        traveler: {},
        travelerEmpId: undefined,
        allowances: {
            meals: {},
            lodging: {},
            mileage: {},
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
        $scope.app.travelerEmpId = appProps.user.employeeId;
    }

    init();

    /**
     * Updates the app.pageState and $scope.pageState after an action has occurred.
     * @param action
     * @param editField (optional) A $scope.STATE value representing the field/page
     *                  the user wants to edit.
     */
    function updateStates(action, editField) {
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
        else if(action === $scope.ACTIONS.EDIT) {
            handleEditAction(editField);
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

    function handleEditAction(editField) {
        $scope.app.appState = $scope.STATES.EDIT;
        $scope.pageState = editField
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

    /**
     * @param action
     * @param editField (optional) The field/page the user has requested to edit. Must be one of $scope.STATES.
     */
    $scope.reviewCallback = function (action, editField) {
        if (action === $scope.ACTIONS.NEXT) {
            modals.open("submit-progress");
            travelApplicationApi.save({}, $scope.app).$promise
                .then(function (response) {
                    modals.resolve({});
                })
                .then(function () {
                    modals.open("submit-results").then(function () {
                        locationService.go("/travel/application/travel-application", true);
                    });
                });
        }
        updateStates(action, editField);
    };
}

/**
 * --- Page Directives ---
 *
 * Page Directives should not directly modify model data in the parent controller.
 * They make copies of any data that the page will support editing of,
 * and modifications can be saved later through the directives callback method in the parent controller.
 *
 * Copying model data ensures that modifications are not made until the user clicks the 'Next' or 'Save' buttons.
 */

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
            // Copy current destinations for use in this directive.
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
                    // Use ModeOfTransportation from previous destination if exists.
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
                if (dest.requestMileage === undefined) {
                    // Init Mileage
                    if (dest.modeOfTransportation === 'Personal Auto') {
                        dest.requestMileage = true;
                    }
                }

                if (dest.requestMeals === undefined) {
                    // Init Meals
                    dest.requestMeals = true;
                }

                if (dest.requestLodging === undefined) {
                    // Init Lodging
                    var arrival = moment(dest.arrivalDate);
                    var departure = moment(dest.departureDate);
                    if (Math.abs(arrival.diff(departure, 'days')) > 0) {
                        dest.requestLodging = true;
                    }
                }
            });
        }
    }
}]);

essTravel.directive('travelApplicationReview', ['appProps', '$q', 'modals', 'TravelMealsAllowanceApi', 'TravelLodgingAllowanceApi',
                                                'TravelMileageAllowanceApi', function (appProps, $q, modals, mealsApi, lodgingApi, mileageAllowanceApi) {
        return {
            templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-review',
            scope: true,
            link: function ($scope, $elem, $attrs) {

                // Display calculating allowances modal until all api calls are completed.
                var promises = [];
                modals.open('calculating-allowances-progress');

                promises.push(mealsApi.save($scope.app.itinerary, function (response) {
                    $scope.app.allowances.meals = response.result;
                }).$promise);

                promises.push(lodgingApi.save($scope.app.itinerary, function (response) {
                    $scope.app.allowances.lodging = response.result;
                }).$promise);

                promises.push(mileageAllowanceApi.save($scope.app.itinerary, function (response) {
                    $scope.app.allowances.mileage = response.result;
                }).$promise);

                $q.all(promises)
                    .then(function () {
                        sumAllowances();
                        displayMap();
                        modals.resolve({});
                    });

                function sumAllowances() {
                    $scope.app.allowances.total = parseFloat($scope.app.allowances.meals.total)
                        + parseFloat($scope.app.allowances.lodging.total)
                        + parseFloat($scope.app.allowances.mileage)
                        + $scope.app.allowances.tolls
                        + $scope.app.allowances.parking
                        + $scope.app.allowances.alternate
                        + $scope.app.allowances.registrationFee;
                }

                $scope.displayLodgingDetails = function () {
                    modals.open('travel-lodging-details-modal', {}, true);
                };

                $scope.displayMealDetails = function () {
                    modals.open('travel-meal-details-modal', {}, true);
                };

                $scope.displayMileageDetails = function () {
                    modals.open('travel-mileage-details-modal', {}, true);
                };

                function displayMap() {
                    var map;
                    var directionsDisplay = new google.maps.DirectionsRenderer();
                    var directionsService = new google.maps.DirectionsService();

                    // Create map centered on Albany.
                    var albany = new google.maps.LatLng(42.6680631, -73.8807209);
                    var mapOptions = {
                        zoom:9,
                        center: albany
                    };
                    map = new google.maps.Map(document.getElementById('map'), mapOptions);
                    directionsDisplay.setMap(map);

                    // Create map api parameters.
                    // All intermediate destinations should be waypoints, final destination should be destination.
                    var destinations = $scope.app.itinerary.destinations.items;
                    var origin = $scope.app.itinerary.origin.formatted_address;
                    var waypoints = [];
                    angular.forEach(destinations, function (dest, index) {
                        waypoints.push({location: dest.address.formatted_address});
                    });
                    // Last destination should be destination param, not waypoint.
                    var destination = waypoints.pop().location;

                    // Set params
                    var request = {
                        origin: origin,
                        destination: destination,
                        waypoints: waypoints,
                        travelMode: 'DRIVING'
                    };

                    console.log(request);

                    // Get directions and display on map.
                    directionsService.route(request, function(result, status) {
                        if (status == 'OK') {
                            directionsDisplay.setDirections(result);
                        }
                        else {
                            console.log("Unsuccessful map query, status = " + status);
                        }
                    });
                }

            }
        }
    }]);
