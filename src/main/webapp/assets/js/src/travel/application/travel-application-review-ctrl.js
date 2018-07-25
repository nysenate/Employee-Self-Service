var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationReviewCtrl', ['$scope', '$q', 'modals', 'LocationService',
                                                     'TravelApplicationSubmitApi', reviewCtrl]);

function reviewCtrl($scope, $q, modals, locationService, submitApi) {

    $scope.init = function () {
        $scope.reviewApp = angular.copy($scope.data.app);
        displayMap();
    };

    $scope.displayLodgingDetails = function () {
        modals.open('travel-lodging-details-modal', {app: $scope.reviewApp}, true);
    };

    $scope.displayMealDetails = function () {
        modals.open('travel-meal-details-modal', {app: $scope.reviewApp}, true);
    };

    $scope.displayMileageDetails = function () {
        modals.open('travel-mileage-details-modal', {app: $scope.reviewApp}, true);
    };

    $scope.next = function () {
        modals.open('submit-confirm')
            .then(function () {
                modals.open("submit-progress");
                submitApi.update({id: $scope.data.app.id}).$promise
                    .then(function (response) {
                        modals.resolve({});
                    })
                    .then(function () {
                        modals.open("submit-results").then(function () {
                            locationService.go("/travel/application/travel-application", true);
                        });
                    })
                    .catch($scope.handleErrorResponse);
            })
    };

    function displayMap() {
        var map;
        var directionsDisplay = new google.maps.DirectionsRenderer();
        var directionsService = new google.maps.DirectionsService();

        // Create map centered on Albany.
        var albany = new google.maps.LatLng(42.6680631, -73.8807209);
        var mapOptions = {
            zoom: 9,
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
        directionsService.route(request, function (result, status) {
            if (status == 'OK') {
                directionsDisplay.setDirections(result);
            }
            else {
                console.log("Unsuccessful map query, status = " + status);
            }
        });
    }
}
