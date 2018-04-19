var essTravel = angular.module('essTravel');

/**
 * Adds google maps autocomplete functionality to an input element.
 *
 * Provide a callback function to have the user selected address
 * (serializable to an AddressView.java) returned.
 *
 * Example:
 * <input travel-address-autocomplete callback="setAddress(address)" placeholder="Enter Origin Address" type="text" size="30">
 *
 * Notes:
 *     - The callback function is required to have the 'address' param.
 *     - Only works on text input elements.
 */
essTravel.directive('travelAddressAutocomplete', ['appProps', function (appProps) {
    return {
        restrict: 'A',
        scope: {
            callback: '&', // function which is given address object after fetched from google.
            address: '@' // Optional - a default address to initialize input with.
        },
        link: function ($scope, $elem, $attrs) {
            var element = $elem[0];
            var autocomplete = new google.maps.places.Autocomplete(
                element, { types: ['address'] });

            autocomplete.addListener('place_changed', function() {
                var address = {};
                var place = autocomplete.getPlace();

                // Convert place result into address object
                address.formattedAddress = place.formatted_address;
                address.addr1 = parseAddress1(place);
                address.city = parseCity(place);
                address.state = parseState(place);
                address.zip5 = parseZip5(place);

                // Call $apply here because angular does not seem to realize when $scope vars are updated in the callback function.
                $scope.$apply(function () {
                    $scope.callback({address: address});
                });
            });

            if ($attrs.address) {
                // If a default address is given, initialize with it.
                element.value = $attrs.address;
            }

            function parseAddress1(place) {
                return getTypeName(place, 'street_number') + ' ' + getTypeName(place, 'route');
            }

            function parseCity(place) {
                var city = getTypeName(place, 'locality');
                return city === null ? getTypeName(place, 'administrative_area_level_3'): city;
            }

            function parseState(place) {
                return getTypeName(place, 'administrative_area_level_1');
            }

            function parseZip5(place) {
                return getTypeName(place, 'postal_code');
            }

            /**
             * Returns the value associated with the given place and type.
             * @param place A place object from google autocomplete api.
             * @param type A address type. i.e. 'street_number', 'postal_code'
             *  - Types are documented here: https://developers.google.com/places/web-service/autocomplete#place_types
             * @return {*}
             */
            function getTypeName(place, type) {
                for (var i = 0; i < place.address_components.length; i++) {
                    var component = place.address_components[i];
                    if (component.types[0] ===  type) {
                        return component.long_name;
                    }
                }
                return '';
            }
        }
    }
}]);
