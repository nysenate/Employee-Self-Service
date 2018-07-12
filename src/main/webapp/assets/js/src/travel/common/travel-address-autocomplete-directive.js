var essTravel = angular.module('essTravel');

/**
 * Adds google maps autocomplete functionality to an input element.
 *
 * Example:
 * <input travel-address-autocomplete leg="leg" callback="setAddress(address)" placeholder="Enter Origin Address" type="text" size="30">
 *
 * Notes:
 *     - The callback function is required to have the 'leg' and 'address' param.
 *          - Callback function should set the address to the leg.
 *     - Only works on text input elements.
 */
essTravel.directive('travelAddressAutocomplete', ['appProps', '$q', 'GooglePlaceService', function (appProps, $q, placeService) {
    return {
        require: 'ngModel',
        restrict: 'A',
        scope: {
            callback: '&', // callback function
            leg: '='
        },
        link: function ($scope, $elem, $attrs, $ctrl) {

            var element = $elem[0];

            if ($attrs.address) {
                // If a default address is given, initialize with it.
                element.value = $attrs.address;
            }

            var autocomplete = new google.maps.places.Autocomplete(
                element, { types: ['address'] });

            autocomplete.addListener('place_changed', function() {
                var address = placeService.parseAddressFromPlace(autocomplete.getPlace());

                // Call $apply here because angular does not seem to realize when $scope vars are updated in the callback function.
                $scope.$apply(function () {
                    $scope.callback({leg: $scope.leg, address: address});
                });
            });
        }
    }
}]);
