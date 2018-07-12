var essTravel = angular.module('essTravel');

essTravel.service('AddressGeocoder', ['TravelGeocodeApi', 'GooglePlaceService', addressGeocoderService]);

function addressGeocoderService(geocodeApi, placeService) {

    /**
     * Returns a promise containing the county of the given address or an empty string if
     * no county could be determined.
     * @param address
     * @return {*}
     */
    this.countyForAddress = function(address) {
        return geocodeApi.get({address: address}).$promise
            .then(function (response) {
                var address = placeService.parseAddressFromPlace(response.results[0]);
                return address.county;
            });
    }
}
