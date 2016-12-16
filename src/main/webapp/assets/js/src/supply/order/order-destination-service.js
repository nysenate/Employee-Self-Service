/**
 * OrderDestinationService is responsible for storing and changing the user selected destination.
 */
essSupply.service('SupplyOrderDestinationService', ['appProps', 'EssStorageService', 'EmpInfoApi', 'SupplyLocationAutocompleteService', orderDestinationService]);
function orderDestinationService(appProps, storageService, empInfoApi, locationAutocompleteService) {

    /**
     * A unique key used for persisting and loading saved destination information.
     */
    var KEY = "supply-destination";

    var defaultCode = undefined;

    return {
        queryDefaultDestination: function () {
            if (!defaultCode) {
                return empInfoApi.get({empId: appProps.user.employeeId, detail: true}, function (response) {
                    defaultCode = response.employee.empWorkLocation.code;
                }).$promise
            }
        },

        isDestinationConfirmed: function () {
            return storageService.load(KEY) != null;
        },

        /**
         * Sets the destination corresponding to the given location code.
         * If code is valid sets the destination, otherwise returns false.
         */
        setDestination: function (code) {
            if (locationAutocompleteService.isValidCode(code)) {
                storageService.save(KEY, locationAutocompleteService.getLocationFromCode(code));
                return true;
            }
            return false;
        },

        reset: function () {
            defaultCode = undefined;
            storageService.remove(KEY);
        },

        getDefaultCode: function () {
            return defaultCode;
        },

        /**
         * Returns a location object.
         */
        getDestination: function () {
            return storageService.load(KEY);
        }
    }
}