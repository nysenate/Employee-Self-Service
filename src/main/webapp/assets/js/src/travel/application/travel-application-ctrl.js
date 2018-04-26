var essTravel = angular.module('essTravel');

essTravel.controller('NewTravelApplicationCtrl',
                     ['$scope', '$q', 'appProps', 'modals', 'LocationService', 'TravelApplicationInitApi',
                      'TravelApplicationApi', 'TravelApplicationAttachmentApi', 'TravelModeOfTransportationApi', travelAppController]);

function travelAppController($scope, $q, appProps, modals, locationService, appInitApi,
                             travelApplicationApi, attachmentApi, motApi) {

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
        OUTBOUND: 10,
        RETURN: 20,
        ALLOWANCES: 25,
        REVIEW: 30,
        EDIT: 35
    };

    $scope.pageState = undefined; // Controls which page is displayed to the user, must be one of $scope.STATES, but should not be EDIT.

    // The state of the application, usually the same as $scope.pageState, unless returning to a page for editing
    // in that case appState == EDIT and pageState == the page who's data is being edited.
    $scope.appState = undefined;

    $scope.app = {};

    function init() {
        $scope.pageState = $scope.STATES.PURPOSE;
        $scope.appState = $scope.STATES.PURPOSE;
        initApplication(appProps.user.employeeId) ;
        initModesOfTransportation();
    }

    init();

    function initApplication(travelerId) {
        appInitApi.save({id: travelerId}, {}, function (response) {
            $scope.app = response.result;
        }, $scope.handleErrorResponse)
    }

    function initModesOfTransportation() {
        motApi.get({}, function (response) {
            $scope.modesOfTransportation = response.result;
        }, $scope.handleErrorResponse);
    }

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
        switch ($scope.appState) {
            case $scope.STATES.PURPOSE:
                locationService.go("/travel/application/travel-application", true);
                break;
            case $scope.STATES.OUTBOUND:
                $scope.appState = $scope.STATES.PURPOSE;
                $scope.pageState = $scope.STATES.PURPOSE;
                break;
            case $scope.STATES.RETURN:
                $scope.appState = $scope.STATES.OUTBOUND;
                $scope.pageState = $scope.STATES.OUTBOUND;
                break;
            case $scope.STATES.ALLOWANCES:
                $scope.appState = $scope.STATES.RETURN;
                $scope.pageState = $scope.STATES.RETURN;
                break;
            case $scope.STATES.REVIEW:
                $scope.appState = $scope.STATES.ALLOWANCES;
                $scope.pageState = $scope.STATES.ALLOWANCES;
                break;
            case $scope.STATES.EDIT:
                $scope.pageState = $scope.STATES.REVIEW;
                break;
        }
    }

    function handleNextAction() {
        switch ($scope.appState) {
            case $scope.STATES.PURPOSE:
                $scope.appState = $scope.STATES.OUTBOUND;
                $scope.pageState = $scope.STATES.OUTBOUND;
                break;
            case $scope.STATES.OUTBOUND:
                $scope.appState = $scope.STATES.RETURN;
                $scope.pageState = $scope.STATES.RETURN;
                break;
            case $scope.STATES.RETURN:
                $scope.appState = $scope.STATES.ALLOWANCES;
                $scope.pageState = $scope.STATES.ALLOWANCES;
                break;
            case $scope.STATES.ALLOWANCES:
                $scope.appState = $scope.STATES.REVIEW;
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
        $scope.appState = $scope.STATES.EDIT;
        $scope.pageState = editField
    }

    $scope.purposeCallback = function (purpose, action) {
        if (action === $scope.ACTIONS.NEXT) {
            $scope.app.purposeOfTravel = purpose;
        }
        updateStates(action);
    };

    $scope.outboundCallback = function (outboundSegments, action) {
        if (action === $scope.ACTIONS.NEXT) {
            $scope.app.outboundSegments = outboundSegments;
        }
        updateStates(action);
    };

    $scope.returnCallback = function (returnSegments, action) {
        if (action === $scope.ACTIONS.NEXT) {
            $scope.app.returnSegments = returnSegments;
        }
        updateStates(action);
    };

    $scope.destinationCallback = function (destinations, action) {
        if (action === $scope.ACTIONS.NEXT) {
            $scope.setDestinations(destinations);
        }
        updateStates(action);
    };

    $scope.setDestinations = function(destinations) {
        $scope.app.destinations = destinations;
    };

    $scope.allowancesCallback = function (destinations, allowances, action) {
        if (action === $scope.ACTIONS.NEXT) {
            $scope.app.tollsAllowance = allowances.tollsAllowance.toString();
            $scope.app.parkingAllowance = allowances.parkingAllowance.toString();
            $scope.app.alternateAllowance = allowances.alternateAllowance.toString();
            $scope.app.registrationAllowance = allowances.registrationAllowance.toString();
            $scope.app.destinations= destinations;
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
                })
                .catch($scope.handleErrorResponse);
        }
        updateStates(action, editField);
    };

    $scope.highlightOutboundStep = function() {
        return $scope.appState !== $scope.STATES.PURPOSE
    };

    $scope.highlightReturnStep = function() {
        return $scope.appState === $scope.STATES.RETURN
            || $scope.appState === $scope.STATES.ALLOWANCES
            || $scope.appState === $scope.STATES.REVIEW
            || $scope.appState === $scope.STATES.EDIT
    };

    $scope.highlightExpensesStep = function() {
        return $scope.appState === $scope.STATES.ALLOWANCES
            || $scope.appState === $scope.STATES.REVIEW
            || $scope.appState === $scope.STATES.EDIT
    };

    $scope.highlightReviewStep = function() {
        return $scope.appState === $scope.STATES.REVIEW
            || $scope.appState === $scope.STATES.EDIT
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

essTravel.directive('travelApplicationPurpose', ['appProps', 'TravelApplicationAttachmentApi', '$http', function (appProps, attachmentApi, $http) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-purpose',
        scope: true,
        link: function ($scope, $elem, $attrs) {
            // Copy current purpose of travel for use in this directive.
            $scope.purposeOfTravel = angular.copy($scope.app.purposeOfTravel);

            $scope.save = function(files) {
                var files = angular.element("#file")[0].files;

                var formData = new FormData();
                for(var i = 0; i < files.length; i++) {
                    formData.append("file", files[i]);
                }

                $http.post(appProps.apiPath + '/travel/application/upload', formData, {
                    // Allow $http to choose the right 'content-type'.
                    headers: {'Content-Type': undefined},
                    transformRequest: angular.identity
                }).then(function (response) {
                    console.log(response);
                });
            }
        }
    }
}]);

essTravel.directive('travelApplicationOutbound', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-outbound',
        scope: true,
        link: function ($scope, $elem, $attrs, ctrl) {

            $scope.outboundSegments = angular.copy($scope.app.outboundSegments) || [];
            if ($scope.outboundSegments.length === 0) {
                $scope.outboundSegments.push(new Segment());
            }

            $scope.addSegment = function() {
                // Initialize new leg
                var segment = new Segment();
                var prevSeg = $scope.outboundSegments[$scope.outboundSegments.length - 1];
                segment.from = prevSeg.to;
                segment.modeOfTransportation = prevSeg.modeOfTransportation;
                segment.isMileageRequested = prevSeg.isMileageRequested;
                segment.isMealsRequested = prevSeg.isMealsRequested;
                segment.isLodgingRequested = prevSeg.isLodgingRequested;
                $scope.outboundSegments.push(segment);
            };

            $scope.isLastSegment = function(index) {
                return $scope.outboundSegments.length - 1 === index;
            };

            $scope.deleteSegment = function() {
                $scope.outboundSegments.pop();
            }
        }
    }
}]);

essTravel.directive('travelApplicationReturn', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-return',
        scope: true,
        link: function ($scope, $elem, $attrs, ctrl) {

            $scope.returnSegments = angular.copy($scope.app.returnSegments) || [];
            if ($scope.returnSegments.length === 0) {
                // Init return leg
                var segment = new Segment();
                segment.from = $scope.app.outboundSegments[$scope.app.outboundSegments.length - 1].to;
                segment.to = $scope.app.outboundSegments[0].from;
                $scope.returnSegments.push(segment);
            }

            $scope.addSegment = function() {
                // Initialize new leg
                var segment = new Segment();
                var prevSeg = $scope.returnSegments[$scope.returnSegments.length - 1];
                segment.from = prevSeg.to;
                segment.modeOfTransportation = prevSeg.modeOfTransportation;
                segment.isMileageRequested = prevSeg.isMileageRequested;
                segment.isMealsRequested = prevSeg.isMealsRequested;
                segment.isLodgingRequested = prevSeg.isLodgingRequested;
                $scope.returnSegments.push(segment);
            };

            $scope.isLastSegment = function(index) {
                return $scope.returnSegments.length - 1 === index;
            };

            $scope.deleteSegment = function() {
                $scope.returnSegments.pop();
            }
        }
    }
}]);

essTravel.directive('travelApplicationAllowances', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-allowances',
        scope: true,
        link: function ($scope, $elem, $attrs) {
            $scope.allowances = {
                tollsAllowance: Number(angular.copy($scope.app.tollsAllowance)),
                parkingAllowance: Number(angular.copy($scope.app.parkingAllowance)),
                alternateAllowance: Number(angular.copy($scope.app.alternateAllowance)),
                registrationAllowance: Number(angular.copy($scope.app.registrationAllowance))
            };
        }
    }
}]);

essTravel.directive('travelApplicationReview', ['appProps', '$q', 'modals', 'TravelApplicationApi',
                                                function (appProps, $q, modals, appApi) {
        return {
            templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-review',
            scope: true,
            link: function ($scope, $elem, $attrs) {

                modals.open('review-progress');

                $scope.reviewApp = angular.copy($scope.app);

                appApi.save({}, $scope.reviewApp, function (response) {
                    modals.resolve({});
                    $scope.reviewApp = response.result;
                    console.log("Review App:");
                    console.log($scope.reviewApp);
                    displayMap();
                }, $scope.handleErrorResponse);


                $scope.displayLodgingDetails = function () {
                    modals.open('travel-lodging-details-modal', {app: $scope.reviewApp}, true);
                };

                $scope.displayMealDetails = function () {
                    modals.open('travel-meal-details-modal', {app: $scope.reviewApp}, true);
                };

                $scope.displayMileageDetails = function () {
                    modals.open('travel-mileage-details-modal', {app: $scope.reviewApp}, true);
                };

                $scope.submitConfirmModal = function () {
                    modals.open('submit-confirm')
                        .then(function () {
                            $scope.reviewCallback($scope.ACTIONS.NEXT);
                        })
                };

                function displayMap() {
                    console.log("Starting map");
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
                    var destinations = $scope.reviewApp.accommodations;
                    var origin = $scope.reviewApp.origin.formattedAddress;
                    var waypoints = [];
                    angular.forEach(destinations, function (dest, index) {
                        waypoints.push({location: dest.address.formattedAddress});
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


function Segment() {
    this.from = {};
    this.to = {};
    this.departureDate = ''; // Use setter to ensure formatted as ISO date.
    this.arrivalDate = ''; // Use setter to ensure formatted as ISO date.
    this.modeOfTransportation = {};
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

