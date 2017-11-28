var essTravel = angular.module('essTravel');

/**
 * Adds google maps autocomplete functionality to an input element.
 *
 * Provide a callback function to have the user selected address
 * (serializable to an AddressView.java) returned.
 *
 * Example:
 * <input travel-autocomplete callback="setAddress(address)" placeholder="Enter Origin Address" type="text" size="30">
 *
 * Notes:
 *     - The callback function is required to have the 'address' param.
 *     - Only works on input elements.
 */
essTravel.directive('travelAutocomplete', ['appProps', function (appProps) {
    return {
        restrict: 'A',
        scope: {
            callback: '&',
            address: '@'
        },
        link: function ($scope, $elem, $attrs) {
            var element = $elem[0];
            var autocomplete = new google.maps.places.Autocomplete(
                element, { types: ['address'] });

            autocomplete.addListener('place_changed', function() {
                var address = {};
                var place = autocomplete.getPlace();

                address.formatted_address = place.formatted_address; // TODO cant use this, wont have it once saved to back end.
                address.addr1 = parseAddress1(place);
                address.city = parseCity(place);
                address.state = parseState(place);
                address.zip5 = parseZip5(place);

                $scope.callback({address: address});
            });

            // TODO init autocomplete with address attr. Below code does not work
            if ($attrs.address) {
                autocomplete.notify("place_changed", $attrs.address);
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
