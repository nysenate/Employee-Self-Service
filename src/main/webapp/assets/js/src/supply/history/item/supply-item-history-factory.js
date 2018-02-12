var essSupply = angular.module('essSupply');

essSupply.factory('SupplyItemHistoryFactory',
                  ['SupplyRequisitionApi', supplyItemHistoryFactory]);

function supplyItemHistoryFactory(requisitionApi) {

    var params = {
        itemId: 'All',
        locId: 'All',
        from: {},
        to: {}
    };

    var itemHistories = {
        // An array of ItemHistories;
        value: [],
        // A map of ItemHistory.key to ItemHistory objects. Used for quick lookup when adding ItemHistories.
        map: new Map()
    };

    /**
     * Parses the requisition API response, populating the itemHistories object.
     * Returns the populated array of ItemHistories in itemHistory.value
     * @param requisitionResponse
     */
    function parseApiResponse(requisitionResponse) {
        var requisitions = requisitionResponse.result;
        requisitions.forEach(function(requisition) {
            requisition.lineItems.forEach(function(lineItem) {
                if (isItemSelectedInFilter(lineItem) && lineItem.quantity > 0) {
                    var itemHistory = new ItemHistory(lineItem.item.commodityCode,
                                                      requisition.destination.locId,
                                                      lineItem.quantity,
                                                      requisition);
                    addToResults(itemHistory);
                }
            })
        });
        itemHistories.map.forEach(function(value, key) {
            itemHistories.value.push(value);
        });

        function isItemSelectedInFilter(lineItem) {
            return params.itemId === 'All' || params.itemId === lineItem.item.id;
        }

        function addToResults(itemHistory) {
            if (itemHistories.map.has(itemHistory.key())) {
                // If this location has multiple orders for the same item, add to the current ItemHistory.
                itemHistories.map.get(itemHistory.key()).add(itemHistory.requisitions[0], itemHistory.quantity);
            }
            else {
                itemHistories.map.set(itemHistory.key(), itemHistory);
            }
        }

        return itemHistories.value;
    }

    /**
     * Sort item histories by commodity code first, then location code.
     */
    function sortItemHistories(itemHistories) {
        itemHistories.sort(function (a, b) {
            var aCommodityCode = a.commodityCode;
            var bCommodityCode = b.commodityCode;
            var aLocCode = a.locationCode;
            var bLocCode = b.locationCode;
            if (aCommodityCode === bCommodityCode) {
                return (aLocCode < bLocCode) ? -1 : (aLocCode > bLocCode) ? 1 : 0;
            }
            else {
                return (aCommodityCode < bCommodityCode) ? -1 : 1;
            }
        })
    }

    function apiParams() {
        return {
            status: ["APPROVED"],
            itemId: params.itemId,
            location: params.locId,
            from: params.from,
            to: params.to,
            limit: 'ALL',
            offset: 0
        }
    }

    function reset() {
        itemHistories.value = [];
        itemHistories.map = new Map();
        params = {
            itemId: 'All',
            locId: 'All',
            from: {},
            to: {}
        };
    }

    return {
        /**
         * Returns a promise containing an array of ItemHistory objects on success.
         * Updates any previous item history results stored in this factory.
         *
         * @param itemId The itemId to filter by, or 'ALL' if not filtering by item.
         * @param locId The LocationId to filter by, or 'ALL' if not filtering by location.
         * @param from Moment object representing the from date.
         * @param to Moment object representing the to date.
         */
        updateItemHistories: function(itemId, locId, from, to) {
            reset();
            params.itemId = itemId;
            params.locId = locId;
            params.from = from;
            params.to = to;
            return requisitionApi.get(apiParams()).$promise
                .then(parseApiResponse)
                .then(sortItemHistories)
        },

        /**
         * Returns the array of item histories that was previously created by
         * a call to updateItemHistories.
         * @return {Array}
         */
        getItemHistories: function () {
            return itemHistories.value;
        }
    };

    /**
     * The ItemHistory object contains the total quantity of an item
     * and an array of all requisitions containing that item
     * for a specific location and commodity.
     */
    function ItemHistory(commodityCode, locationCode, quantity, requisition) {
        this.commodityCode = commodityCode;
        this.locationCode = locationCode;
        this.quantity = quantity;
        this.requisitions = [requisition];

        /**
         * Return a unique key representing this object. To be used as a map key.
         * This is needed so the map keys compare by value instead of reference.
         */
        this.key = function() {
            return this.commodityCode + ':' + this.locationCode;
        };

        /**
         * Add the quantities from a requisition to this ItemHistory.
         */
        this.add = function(req, qty) {
            this.requisitions.push(req);
            this.quantity += qty;
        };
    }
}
