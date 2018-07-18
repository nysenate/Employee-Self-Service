var essTravel = angular.module('essTravel');

essTravel.service('AddressCountyService', ['$q', '$timeout', 'modals', 'AddressGeocoder', addressCountyService]);

/**
 * Provides common functionality related to getting, setting, and querying address county info.
 * @param $q
 * @param modals
 * @param geocoder
 */
function addressCountyService($q, $timeout, modals, geocoder) {

    /**
     * Returns an array of addresses without a county or an empty array if all
     * addresses have a county.
     * @param addresses an array of addresses.
     * @returns A new array containing all addresses without a county set.
     */
    this.addressesMissingCounty = function (addresses) {
        return addresses.filter(function (addr) {
            return !addr.county;
        })
    };

    /**
     * Updates the county for an array of addresses from google geocoder data.
     * County will be set to an empty string if it was missing or unavailable in google.
     * @param addresses An array of addresses who's counties will be updated to the value
     *  in google's geocode service.
     * @returns {Promise} A single promise that will be resolved with an array of addresses
     *  with county info updated.
     */
    this.updateWithGeocodeCounty = function (addresses) {
        var promises = [];
        addresses.forEach(function (el, index, array) {
            var promise = geocoder.countyForAddress(el.formattedAddress)
                .then(function (county) {
                    el.county = county;
                });

            promises.push(promise);
        });

        return $q.all(promises).then(function () {
            return addresses;
        });
    };

    /**
     * Loops through addresses, displaying a modal prompting the user to provide a county
     * for the address. This is done one address at a time, a modal getting resolved
     * will trigger the next modal to display for as long as there are addresses.
     * @param addresses
     * @return {Promise} A single promise that is resolved when the user has entered
     *  a county for all addresses.
     */
    this.promptUserForCounty = function (addresses) {
        var deferred = $q.defer();

        /**
         * Recursively call this function so we can iterate through addresses one
         * at a time, displaying a modal for each, and only moving to the next iteration
         * once the user has resolved the modal.
         */
        (function displayCountyModal() {
            var addr = addresses.shift();
            if (modals.isTop('address-county-modal')) {
                modals.resolve();
            }
            if (addr == undefined) {
                deferred.resolve();
            }
            else {
                 // Close loading modal if present so address-county-modal is not blurry.
                if (modals.isTop('loading')) {
                    modals.resolve();
                }
                modals.open('address-county-modal', {address: addr})
                    .then(function () {
                        modals.open('loading');
                        // This timeout serves 2 purposes. One, It makes it more noticeable to the user
                        // that the modal has changed. Two, The modal does not seem to close correctly without it,
                        // the next modal will have the same params as the first.
                        $timeout(function () {
                            displayCountyModal();
                        }, 200);
                    });
            }
        })();
        return deferred.promise;
    }

}