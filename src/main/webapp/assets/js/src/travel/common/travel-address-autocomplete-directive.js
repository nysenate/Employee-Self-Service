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

            // Bias autocomplete results to NY.
            // This also fixes an issue where '250 Broadway, New York, New York 10007' would not show up.
            // I'd guess this is because without a 'bounds' param it biases by my current location?
            var nyBounds = [[40.488737, -74.264832],[40.955011, -71.762695],[41.294317, -71.932983],[40.955011, -73.641357],[41.100052, -73.721008],[41.215854, -73.487549],[41.298444, -73.550720],[42.085994, -73.504028],[42.747012, -73.267822],[43.612217, -73.289795],[45.003651, -73.300781],[45.011419, -74.959717], [43.612217, -77.189941],[43.269206, -79.112549],[42.843751, -78.936768],[42.536892, -79.782715],[42.000325, -79.749756],[41.983994, -75.366211],[41.327326, -74.783936],[40.996484, -73.907776],[40.653555, -74.058838],[40.640009, -74.200287]];
            var nyLatLngBounds = new google.maps.LatLngBounds();
            $.each(nyBounds, function(i,v){
                nyLatLngBounds.extend(new google.maps.LatLng(v[0], v[1]));
            });

            var element = $elem[0];

            if ($attrs.address) {
                // If a default address is given, initialize with it.
                element.value = $attrs.address;
            }

            var autocomplete = new google.maps.places.Autocomplete(
                element, {
                    types: ['address'],
                    bounds: nyLatLngBounds
                });

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
