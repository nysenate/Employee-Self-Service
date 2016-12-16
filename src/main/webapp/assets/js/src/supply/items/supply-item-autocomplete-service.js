angular.module('essSupply').service('SupplyItemAutocompleteService',
                                    ['SupplyItemApi', itemAutocompleteService]);

/**
 * This service handles the data for item autocomplete elements used in supply.
 */
function itemAutocompleteService(itemsApi) {

    var items = [];
    var commodityCodes = [];
    var commodityCodesToItems = new Map();

    var setItems = function (response) {
        items = response;
    };

    var setCommodityCodes = function () {
        angular.forEach(items, function (item) {
            commodityCodes.push(item.commodityCode);
        })
    };

    var setCommodityCodesToItems = function () {
        angular.forEach(items, function (item) {
            commodityCodesToItems.set(item.commodityCode, item);
        })
    };

    var reset = function () {
        items = [];
        commodityCodes = [];
        commodityCodesToItems = new Map();
    };

    return {
        initWithAllItems: function () {
            reset();
            return itemsApi.items()
                .then(setItems)
                .then(setCommodityCodes)
                .then(setCommodityCodesToItems)
        },

        getItemFromCommodityCode: function (commodityCode) {
            return commodityCodesToItems.get(commodityCode);
        },

        getItemAutocompleteOptions: function () {
            var autocompleteOptions = {
                options: {
                    html: true,
                    focusOpen: false,
                    onlySelectValid: true,
                    outHeight: 50,
                    minLength: 0,
                    source: function (request, response) {
                        var data = commodityCodes;
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
                methods: {}
            };
            return autocompleteOptions;
        }
    }
}