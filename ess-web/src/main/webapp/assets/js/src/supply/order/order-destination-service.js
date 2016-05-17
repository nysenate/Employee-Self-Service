/**
 * OrderDestinationService is responsible for storing and changing the user selected destination.
 */
essSupply.service('SupplyOrderDestinationService', ['appProps', 'EmpInfoApi', 'SupplyLocationAutocompleteService', orderDestinationService]);
function orderDestinationService(appProps, empInfoApi, locationAutocompleteService) {

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
                return true;
            }
            return false;
        },

        reset: function () {
            defaultCode = undefined;
            destination = undefined;
        },

        getDefaultCode: function () {
            return defaultCode;
        },

        getDestination: function () {
            return destination;
        }
    }
}