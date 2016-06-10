/**
 * OrderDestinationService is responsible for storing and changing the user selected destination.
 */
essSupply.service('SupplyOrderDestinationService', ['appProps', 'SupplyCookieService', 'EmpInfoApi', 'SupplyLocationAutocompleteService', orderDestinationService]);
function orderDestinationService(appProps,cookies, empInfoApi, locationAutocompleteService) {

    var defaultCode = undefined;
    var destination = undefined;

    return {
        queryDefaultDestination: function () {
            if (!defaultCode) {
                return empInfoApi.get({empId: appProps.user.employeeId, detail: true}, function (response) {
                    defaultCode = response.employee.empWorkLocation.code;
                }).$promise
            }
        },

        isDestinationConfirmed: function () {
            return destination !== undefined;
        },

        /**
         * Sets the destination corresponding to the given code.
         * If code is valid sets the destination, otherwise returns false.
         */
        setDestination: function (code) {
            if (locationAutocompleteService.isValidCode(code)) {
                destination = locationAutocompleteService.getLocationFromCode(code);
                cookies.addDestination(destination);
                return true;
            }
            return false;
        },

        reset: function () {
            defaultCode = undefined;
            destination = undefined;
            cookies.resetDestination();
        },

        getDefaultCode: function () {
            return defaultCode;
        },

        getDestination: function () {
            return destination;
        }
    }
}