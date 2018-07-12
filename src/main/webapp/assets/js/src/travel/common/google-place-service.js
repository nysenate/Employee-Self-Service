var essTravel = angular.module('essTravel');

essTravel.service('GooglePlaceService', [googlePlaceService]);

function googlePlaceService() {

    /**
     * Parses an address from a google place object.
     * @param place
     */
    this.parseAddressFromPlace = function (place) {
        var address = {};

        address.formattedAddress = place.formatted_address;
        address.addr1 = parseAddress1(place);
        address.city = parseCity(place);
        address.county = parseCounty(place);
        address.state = parseState(place);
        address.zip5 = parseZip5(place);

        function parseAddress1(place) {
            return getTypeName(place, 'street_number') + ' ' + getTypeName(place, 'route');
        }

        function parseCity(place) {
            var city = getTypeName(place, 'locality');
            return !city ? getTypeName(place, 'administrative_area_level_3') : city;
        }

        function parseCounty(place) {
            return getTypeName(place, 'administrative_area_level_2');
        }

        function parseState(place) {
            return getTypeName(place, 'administrative_area_level_1');
        }

        function parseZip5(place) {
            return getTypeName(place, 'postal_code');
        }

        /**
         * Returns the value associated with the given place and component type.
         * @param place A place object from google autocomplete api.
         * @param type A component type. i.e. 'street_number', 'postal_code'
         *  - Types are documented here: https://developers.google.com/places/web-service/autocomplete#place_types
         * @return {*}
         */
        function getTypeName(place, type) {
            for (var i = 0; i < place.address_components.length; i++) {
                var component = place.address_components[i];
                if (component.types[0] === type) {
                    return component.long_name;
                }
            }
            return '';
        }

        return address;
    }
}