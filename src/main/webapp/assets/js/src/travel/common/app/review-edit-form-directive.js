var essTravel = angular.module('essTravel');

essTravel.directive('essReviewEditForm', ['$compile', 'appProps', 'modals', reviewEditForm]);

function reviewEditForm($compile, appProps, modals) {
    return {
        restrict: 'E',
        scope: {
            app: '<',               // The application being edited.
            title: '@',             // The title
            positiveCallback: '&',  // Callback function called when continuing. Takes a travel app param named 'app'.
            positiveBtnLabel: '@',   // The label to use for the positive button.
            neutralCallback: '&',   // Callback function called when moving back. Takes a travel app param named 'app'.
            negativeCallback: '&?', // Callback function called when canceling. Takes a travel app param named 'app'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/review-edit-form-directive',
        link: function (scope, elem, attrs) {

            // Hides the negative button if no callback was provided.
            scope.showNegative = attrs.hasOwnProperty('negativeCallback');

            scope.reviewApp = angular.copy(scope.app);

            displayMap();

            scope.next = function () {
                scope.positiveCallback({app: scope.reviewApp});
            };

            scope.back = function () {
                scope.neutralCallback({app: scope.dirtyApp});
            };

            scope.cancel = function () {
                scope.negativeCallback({app: scope.reviewApp});
            };

            scope.displayLodgingDetails = function () {
                modals.open('ess-lodging-details-modal', {app: scope.reviewApp}, true);
            };

            scope.displayMealDetails = function () {
                modals.open('ess-meal-details-modal', {app: scope.reviewApp}, true);
            };

            scope.displayMileageDetails = function () {
                modals.open('ess-mileage-details-modal', {app: scope.reviewApp}, true);
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
                // All intermediate destinations should be waypoints, final destination should an address string.
                var origin = scope.reviewApp.route.origin.formattedAddressWithCounty;

                var waypoints = [];
                scope.reviewApp.route.outboundLegs.forEach(function (leg) {
                    waypoints.push({location: leg.to.address.formattedAddressWithCounty});
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
                    } else {
                        console.log("Unsuccessful map query, status = " + status);
                    }
                });
            }
        }
    }
}