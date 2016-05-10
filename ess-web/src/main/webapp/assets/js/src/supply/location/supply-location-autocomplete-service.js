angular.module('essSupply').service('SupplyLocationAutocompleteService',
    ['LocationApi', locationAutocompleteService]);

function locationAutocompleteService(locationApi) {

    var locations = [];
    var locationCodes = [];
    var codeToLocMap = new Map();

    var setLocations = function (response) {
        locations = response.result;
    };

    var setCodes = function () {
        angular.forEach(locations, function (loc) {
            locationCodes.push(loc.code);
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

    return {
        queryLocations: function () {
            return locationApi.get().$promise
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

        getLocationAutocompleteOptions: function (height) {
            var autocompleteOptions = {
                options: {
                    html: true,
                    focusOpen: false,
                    onlySelectValid: true,
                    outHeight: height || 300,
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
                    }
                },
                methods: {}
            };
            return autocompleteOptions;
        }
    }
}
