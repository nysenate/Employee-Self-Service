angular.module('essSupply').service('SupplyLocationAutocompleteService',
    ['appProps', 'LocationApi', 'SupplyDestinationApi', locationAutocompleteService]);

/**
 * This service handles the data for location autocomplete elements used in supply.
 */
function locationAutocompleteService(appProps, locationApi, destinationApi) {

    var locations = [];
    var locationCodes = [];
    var codeToLocMap = new Map();

    var setLocations = function (response) {
        locations = response.result;
    };

    var setCodes = function () {
        angular.forEach(locations, function (loc) {
            locationCodes.push(loc.code + "(" + loc.locationDescription + ")");
        });
        sortCodes(locationCodes);
    };

    var sortCodes = function (codes) {
        codes.sort(function (a, b) {
            if (a < b) return -1;
            if (a > b) return 1;
            return 0;
        })
    };

    var setCodesToLocationMap = function () {
        angular.forEach(locations, function (loc) {
            codeToLocMap.set(loc.code, loc);
        });
    };

    var reset = function () {
        locations = [];
        locationCodes = [];
        codeToLocMap = new Map();
    };

    return {
        initWithAllLocations: function () {
            reset();
            return locationApi.get().$promise
                .then(setLocations)
                .then(setCodes)
                .then(setCodesToLocationMap);
        },

        /** Only get locations that fall under the logged in users responsibility Head. */
        initWithResponsibilityHeadLocations: function () {
            reset();
            return destinationApi.get({empId: appProps.user.employeeId}).$promise
                .then(setLocations)
                .then(setCodes)
                .then(setCodesToLocationMap);
        },

        getCodes: function () {
            return locationCodes;
        },

        getCodeToLocationMap: function () {
            return codeToLocMap;
        },

        isValidCode: function (code) {
            return codeToLocMap.has(code);
        },

        getLocationFromCode: function (code) {
            return codeToLocMap.get(code);
        },

        getLocationAutocompleteOptions: function (height) {
            var autocompleteOptions = {
                options: {
                    html: true,
                    focusOpen: false,
                    onlySelectValid: true,
                    outHeight: height || 300,
                    minLength: 0,
                    select: function (event, object) {
                        object.item.label = object.item.label.split("(")[0];
                        object.item.value = object.item.value.split("(")[0]
                    },
                    source: function (request, response) {
                        var data = locationCodes;
                        data = autocompleteOptions.methods.filter(data, request.term);
                        if (!data.length) {
                            data.push({
                                label: 'Not Found',
                                value: ''
                            })
                        }
                        response(data);
                    },
                    // Remove jquery help messages.
                    messages: {
                        noResults: '',
                        results: function () {
                        }
                    }
                },
                methods: {},
            };
            return autocompleteOptions;
        }
    }
}
