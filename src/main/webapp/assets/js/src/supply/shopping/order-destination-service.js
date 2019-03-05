/**
 * OrderDestinationService is responsible for storing and changing the user selected destination.
 */
essSupply.service('SupplyOrderDestinationService', ['appProps', 'EssStorageService', orderDestinationService]);

function orderDestinationService(appProps, storageService) {

    /**
     * A unique key used for persisting and loading saved destination information.
     */
    var KEY = "supply-destination";

    return {

        /**
         * Returns a location object.
         */
        getDestination: function () {
            return storageService.load(KEY);
        },

        /**
         * Sets the destination corresponding to the given location code.
         * If code is valid sets the destination and returns true, otherwise returns false.
         */
        setDestination: function (destination) {
            if (destination) {
                storageService.save(KEY, destination);
                return true;
            }
            return false;
        },

        isDestinationConfirmed: function () {
            return storageService.load(KEY) != null;
        },

        reset: function () {
            defaultCode = undefined;
            storageService.remove(KEY);
        }
    }
}