var essTravel = angular.module('essTravel');

essTravel.directive('essReviewEditForm', ['$compile', 'appProps', 'modals', reviewEditForm]);

function reviewEditForm($compile, appProps, modals) {
    return {
        restrict: 'E',
        scope: {
            data: '<',         // The application being edited.
            positiveCallback: '&',  // Callback function called when continuing. Takes a draft param named 'draft'.
            positiveBtnLabel: '@',  // The label to use for the positive button.
            neutralCallback: '&',   // Callback function called when moving back. Takes a draft param named 'draft'.
            negativeCallback: '&?', // Callback function called when canceling. Takes a draft param named 'draft'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/review-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.mode = scope.data.mode;
            scope.app = {
                activeAmendment: scope.data.draft.amendment,
                traveler: scope.data.draft.traveler,
                submittedDateTime: new Date(),
            };

            // Hides the negative button if no callback was provided.
            scope.showNegative = attrs.hasOwnProperty('negativeCallback');

            displayMap();

            scope.next = function () {
                scope.positiveCallback({draft: scope.data.draft});
            };

            scope.save = function () {
                scope.saveDraft(scope.data.draft)
                    .then(function (draft) {
                        scope.data.draft = draft;
                    });
            }

            scope.back = function () {
                scope.neutralCallback({draft: scope.data.draft});
            };

            scope.cancel = function () {
                scope.negativeCallback({draft: scope.data.draft});
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
                var origin = scope.data.draft.amendment.route.origin.formattedAddressWithCounty;

                var waypoints = [];
                scope.data.draft.amendment.route.outboundLegs.forEach(function (leg) {
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