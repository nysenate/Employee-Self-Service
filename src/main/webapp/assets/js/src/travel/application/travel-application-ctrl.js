var essTravel = angular.module('essTravel');

/**
 * The Main/Parent controller for the travel application form.
 * This controller controls data updates and the page of the application.
 *
 * Each page of the application is implemented as its own directive (defined below).
 * These page directives don't modify the application directly, but call callback functions
 * that are defined in this Parent controller.
 */
essTravel.controller('NewTravelApplicationCtrl',
                     ['$scope', '$q', 'appProps', 'modals', 'LocationService', 'TravelApplicationInitApi', 'TravelApplicationPurposeApi',
                      'TravelApplicationOutboundApi', 'TravelApplicationReturnApi', 'TravelApplicationExpensesApi', 'TravelApplicationSubmitApi',
                      'TravelModeOfTransportationApi', 'TravelApplicationCancelApi', travelAppController]);

function travelAppController($scope, $q, appProps, modals, locationService, appInitApi, purposeApi,
                             outboundApi, returnApi, expensesApi, submitApi, motApi, cancelApi) {


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
     * @param travelerId
     */
    function initApplication(travelerId) {
        appInitApi.save({empId: travelerId}, {}, function (response) {
            $scope.app = response.result;
        }, $scope.handleErrorResponse)
    }

    function initMethodsOfTravel() {
        motApi.get({}, function (response) {
            $scope.modesOfTransportation = response.result;
        }, $scope.handleErrorResponse);
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
                cancelApi.remove({empId: $scope.app.traveler.employeeId})
                    .$promise
                    .then(reload)
                    .catch($scope.handleErrorResponse)
                    .finally($scope.closeLoadingModal)
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
            $scope.openLoadingModal();
            purposeApi.update({id: $scope.app.id}, purpose, function(response) {
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

    $scope.outboundCallback = function (action, route) {
        if (action === $scope.ACTIONS.NEXT) {
            $scope.openLoadingModal();
            outboundApi.update({id: $scope.app.id}, route, function(response) {
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
             $scope.openLoadingModal();
            expensesApi.update({id: $scope.app.id}, {destinations: destinations,
                allowances: allowances}, function(response) {
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
 * --- Page Directives ---
 *
 * Page directives contain the html and logic needed for each step of the application.
 *
 * Generally these directives don't update $scope.app directly, but make copies needed data
 * and pass user actions along with data to their callback function.
 *
 * Copying model data ensures that modifications are not made until the user clicks the 'Next' button.
 */

essTravel.directive('travelApplicationPurpose', ['appProps', '$http', 'TravelAttachmentDelete', function (appProps, $http, deleteAttachmentApi) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-purpose',
        scope: true,
        link: function ($scope, $elem, $attrs) {
            // Copy current purpose of travel for use in this directive.
            $scope.data = {
                purposeOfTravel: angular.copy($scope.app.purposeOfTravel)
            };

           var attachmentInput = angular.element("#addAttachment");
            attachmentInput.on('change', uploadAttachment);

            /**
             * This is the one place were a page directive directly updates the application.
             * This is because we need to upload the attachments while staying on the purpose page.
             */
            function uploadAttachment(event) {
                $scope.openLoadingModal();

                var files = attachmentInput[0].files;
                var formData = new FormData();
                for(var i = 0; i < files.length; i++) {
                    formData.append("file", files[i]);
                }

                // Use $http instead of $resource because it can handle formData.
                $http.post(appProps.apiPath + '/travel/application/uncompleted/' + $scope.app.id + '/attachment', formData, {
                    // Allow $http to choose the correct 'content-type'.
                    headers: {'Content-Type': undefined},
                    transformRequest: angular.identity
                }).then(function (response) {
                    // Note, This creates a new local scope app, does not overwrite parent $scope.app.
                    $scope.app = response.data.result;
                }).finally($scope.closeLoadingModal)
            }

            $scope.deleteAttachment = function(attachment) {
                deleteAttachmentApi.delete({id: $scope.app.id, attachmentId: attachment.id}, function(response) {
                    console.log(response);
                    $scope.app = response.result;
                })
            };
        }
    }
}]);

essTravel.directive('travelApplicationOutbound', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-outbound',
        scope: true,
        link: function ($scope, $elem, $attrs, ctrl) {

            $scope.route = angular.copy($scope.app.route);

            if ($scope.route.outboundLegs.length === 0) {
                var segment = new Segment();
                // Init from address to employees work address.
                segment.from = $scope.app.traveler.empWorkLocation.address;
                $scope.route.outboundLegs.push(segment);
            }

            $scope.addSegment = function() {
                // Initialize new leg
                var segment = new Segment();
                var prevSeg = $scope.route.outboundLegs[$scope.route.outboundLegs.length - 1];
                segment.from = prevSeg.to;
                segment.modeOfTransportation = prevSeg.modeOfTransportation;
                segment.isMileageRequested = prevSeg.isMileageRequested;
                segment.isMealsRequested = prevSeg.isMealsRequested;
                segment.isLodgingRequested = prevSeg.isLodgingRequested;
                $scope.route.outboundLegs.push(segment);
            };

            $scope.setFromAddress = function(leg, address) {
                leg.from = address;
            };

            $scope.setToAddress = function(leg, address) {
                leg.to = address;
            };

            $scope.isLastSegment = function(index) {
                return $scope.route.outboundLegs.length - 1 === index;
            };

            $scope.deleteSegment = function() {
                $scope.route.outboundLegs.pop();
            };

            $scope.submit = function () {
                for (var prop in $scope.outboundForm) {
                    // Set all form elements as touched so they can be styled appropriately if they have errors.
                    if ($scope.outboundForm[prop] && typeof($scope.outboundForm[prop].$setTouched) === 'function') {
                        $scope.outboundForm[prop].$setTouched();
                    }
                }
                // If the entire form is valid, continue to next page.
                if ($scope.outboundForm.$valid) {
                    $scope.outboundCallback($scope.ACTIONS.NEXT, $scope.route);
                }
            };
        }
    }
}]);

essTravel.directive('travelApplicationReturn', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-return',
        scope: true,
        link: function ($scope, $elem, $attrs, ctrl) {

            $scope.route = angular.copy($scope.app.route);
            if ($scope.route.returnLegs.length === 0) {
                // Init return leg
                var segment = new Segment();
                segment.from = angular.copy($scope.route.outboundLegs[$scope.route.outboundLegs.length - 1].to);
                segment.to = angular.copy($scope.route.outboundLegs[0].from);
                // If only 1 outbound mode of transportation, initialize to that.
                if ($scope.numDistinctModesOfTransportation($scope.app) === 1) {
                    segment.modeOfTransportation = angular.copy($scope.route.outboundLegs[0].modeOfTransportation);
                }
                $scope.route.returnLegs.push(segment);
            }

            $scope.addSegment = function() {
                // Initialize new leg
                var segment = new Segment();
                var prevSeg = $scope.route.returnLegs[$scope.route.returnLegs.length - 1];
                segment.from = prevSeg.to;
                segment.to = angular.copy($scope.route.outboundLegs[0].from);
                segment.modeOfTransportation = prevSeg.modeOfTransportation;
                segment.isMileageRequested = prevSeg.isMileageRequested;
                segment.isMealsRequested = prevSeg.isMealsRequested;
                segment.isLodgingRequested = prevSeg.isLodgingRequested;
                $scope.route.returnLegs.push(segment);
                console.log($scope.app);
            };

            $scope.setFromAddress = function(leg, address) {
                leg.from = address;
            };

            $scope.setToAddress = function(leg, address) {
                leg.to = address;
            };

            $scope.isLastSegment = function(index) {
                return $scope.route.returnLegs.length - 1 === index;
            };

            $scope.deleteSegment = function() {
                $scope.route.returnLegs.pop();
            };

            $scope.submit = function () {
                for (var prop in $scope.returnForm) {
                    // Set all form elements as touched so they can be styled appropriately if they have errors.
                    if ($scope.returnForm[prop] && typeof($scope.returnForm[prop].$setTouched) === 'function') {
                        $scope.returnForm[prop].$setTouched();
                    }
                }
                // If the entire form is valid, continue to next page.
                if ($scope.returnForm.$valid) {
                    $scope.returnCallback($scope.ACTIONS.NEXT, $scope.route);
                }
            };
        }
    }
}]);

essTravel.directive('travelApplicationAllowances', ['appProps', 'modals', function (appProps, modals) {
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

            $scope.route = angular.copy($scope.app.route);

            $scope.destinations = [];

            function Destination () {
                this.accommodation;
                this.stays = [];
            }

            function Stay() {
                this.date = '';
                this.isMealsRequested = false;
                this.isLodgingRequested = false;
                this.isLodgingEligible = false;
            }

            // Init accommodations
            angular.forEach($scope.app.accommodations, function(a) {
                var destination = new Destination();
                destination.accommodation = a;
                angular.forEach(a.days, function(day) {
                    var stay = new Stay();
                    stay.date = day.date;
                    stay.isMealsRequested = day.isMealsRequested;

                    // Find out if lodging is possible and requested.
                    angular.forEach(a.nights, function(night) {
                        if (night.date === day.date) {
                            stay.isLodgingEligible = true;
                            stay.isLodgingRequested = night.isLodgingRequested;
                        }
                    });

                    destination.stays.push(stay);
                });
                $scope.destinations.push(destination);
            });

            $scope.anyReimbursableTravel = function() {
                for(var i = 0; i < $scope.route.outboundLegs.length; i++) {
                    if ($scope.route.outboundLegs[i].modeOfTransportation.methodOfTravel === 'PERSONAL_AUTO') {
                        return true;
                    }
                }
                for(var y = 0; y < $scope.route.returnLegs.length; y++) {
                    if ($scope.route.returnLegs[y].modeOfTransportation.methodOfTravel === 'PERSONAL_AUTO') {
                        return true;
                    }
                }
                return false;
            };

            $scope.isReimbursableLeg = function(leg) {
                return leg.modeOfTransportation.methodOfTravel === 'PERSONAL_AUTO';
            };

        }
    }
}]);

essTravel.directive('travelApplicationReview', ['appProps', '$q', 'modals',
                                                function (appProps, $q, modals) {
        return {
            templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-review',
            scope: true,
            link: function ($scope, $elem, $attrs) {

                $scope.reviewApp = angular.copy($scope.app);


                displayMap();

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
                    var origin = $scope.reviewApp.route.origin.formattedAddress;
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
                console.log(modelValue);
                if (modelValue && modelValue.methodOfTravel == null) {
                    return false;
                }
                return true;
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

