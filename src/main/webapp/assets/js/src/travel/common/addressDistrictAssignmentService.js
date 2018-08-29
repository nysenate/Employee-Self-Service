var essTravel = angular.module('essTravel');

essTravel.service('AddressDistrictAssigner', ['TravelDistrictAssignApi', addressGeocoderService]);

function addressDistrictAssignService(districtAssignApi) {

    /**
     * Returns a Sage verified USPS address with the county. This will only work well in the state of NY
     */
    //TODO this class has not been tested yet, and is currently not in use. It may become useful later in ess travel
    this.distAssignAddress = function(address) {
        return districtAssignApi.get({address: address}).$promise
            .then(function (response) {
                var distAddress = {};
                distAddress.addr1 = response.address.addr1;
                distAddress.city = response.address.city;
                distAddress.state = response.address.state;
                distAddress.zip5 = response.address.zip5;
                distAddress.county = response.districts.county.name;
                distAddress.formattedAddress = distAddress.addr1 + ", " + distAddress.city + ", " + distAddress.zip5
                    + ", " + distAddress.county + ", " + distAddress.state;

                return distAddress;
            });
    }
}