essSupply = angular.module('essSupply')
    .controller('SupplyItemHistoryCtrl', ['$scope', '$q', 'SupplyUtils', 'SupplyRequisitionApi',
                                          'LocationApi', 'SupplyItemApi', 'modals', supplyItemHistoryCtrl]);

function supplyItemHistoryCtrl($scope, $q, supplyUtils, requisitionApi, locationApi, itemApi, modals) {

    const DATE_FORMAT = "MM/DD/YYYY";

    $scope.filters = {
        location: {
            values: [],
            selected: ''
        },
        item: {
            values: [],
            selected: '',
            codeToId: {} // Maps item codes to item id's for use in requisition search Api.
        },
        date: {
            from: {},
            to: {}
        }
    };

    $scope.result = {
        // A map of item ItemHistory.key() values to ItemHistory objects.
        // Used for quick lookup when parsing requisitions.
        map: new Map(),
        // An array of ItemHistory objects. Used to display in the UI.
        array: []
    };

    // An item history key that identifies which details should be displayed.
    // This is updated by clicking on a row, and displays a sub table with all requisitions containing that item and location.
    $scope.details = null;

    $scope.loading = false;

    var init = function () {
        $scope.loading = true;
        initFilters()
            .then($scope.onFilterChange);
    };

    /**
     * Initialize all filters.
     * Returns a promise that is resolved when both item and location ajax requests have resolved.
     */
    var initFilters = function() {
        $scope.filters.date.from = moment().subtract(1, 'month').format(DATE_FORMAT);
        $scope.filters.date.to = moment().format(DATE_FORMAT);
        var locPromise = locationApi.get().$promise
            .then(initLocationFilter);
        var itemPromise = itemApi.items()
            .then(initItemFilter);

        var promises = [];
        promises.push(locPromise);
        promises.push(itemPromise);
        return $q.all(promises);
    };

    var initLocationFilter = function (response) {
        response.result.forEach(function(loc) {
            $scope.filters.location.values.push(loc.locId);
        });
        supplyUtils.alphabetizeByName($scope.filters.location.values);
        $scope.filters.location.values.unshift("All");
        $scope.filters.location.selected = $scope.filters.location.values[0];
    };

    var initItemFilter = function (items) {
        items.forEach(function(item) {
            $scope.filters.item.values.push(item.commodityCode);
            $scope.filters.item.codeToId[item.commodityCode] = item.id;
        });
        $scope.filters.item.values = supplyUtils.alphabetizeByName($scope.filters.item.values);
        $scope.filters.item.values.unshift("All");
        $scope.filters.item.selected = $scope.filters.item.values[0];
    };

    $scope.onFilterChange = function () {
        $scope.loading = true;
        $scope.result.map.clear();
        getRequisitions()
            .then(parseResults)
            .then(sortResults)
            .then(function () {
                $scope.loading = false;
            })
    };

    var getRequisitions = function () {
        var params = {
            status: ["APPROVED"],
            from: moment($scope.filters.date.from, DATE_FORMAT).startOf('day').format(),
            to: moment($scope.filters.date.to, DATE_FORMAT).endOf('day').format(),
            limit: 'ALL',
            location: $scope.filters.location.selected,
            itemId: $scope.filters.item.codeToId[$scope.filters.item.selected]
        };
        return requisitionApi.get(params).$promise
            .catch(function (error) {
               modals.open('500', {details: error}) ;
               console.error(error);
            })
    };

    /**
     * Groups quantities ordered by item and location
     */
    var parseResults = function(requisitionResponse) {
        var requisitions = requisitionResponse.result;
        requisitions.forEach(function(requisition) {
           requisition.lineItems.forEach(function(lineItem) {
               if (isItemSelectedInFilter(lineItem) && lineItem.quantity > 0) {
                   var itemHistory = new ItemHistory(lineItem.item.commodityCode,
                                                     requisition.destination.locId,
                                                     lineItem.quantity,
                                                     requisition);
                   updateResults(itemHistory);
               }
           })
        });
        $scope.result.array = Array.from($scope.result.map.values());
    };

    function isItemSelectedInFilter(lineItem) {
        return $scope.filters.item.selected === 'All' || $scope.filters.item.selected === lineItem.item.commodityCode;
    }

    function updateResults(itemHistory) {
        if ($scope.result.map.has(itemHistory.key())) {
            $scope.result.map.get(itemHistory.key()).add(itemHistory.requisitions[0], itemHistory.quantity);
        }
        else {
            $scope.result.map.set(itemHistory.key(), itemHistory);
        }
    }

    /**
     * Sort results by commodity code first, then location code.
     */
    var sortResults = function () {
        $scope.result.array.sort(function (a, b) {
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
    };

    /**
     * Represents a single row of results
     * Contains the quantity ordered for a single combination of commodity code and location code.
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
         * Add the quantities from another requisition to this ItemHistory.
         */
        this.add = function(req, qty) {
            this.requisitions.push(req);
            this.quantity += qty;
        };
    }

    $scope.displayDetails = function (key) {
      console.log(key);
      $scope.details = key;
    };

    /**
     * Get the quantity of an item ordered in a requisition.
     */
    $scope.getItemQuantity = function (requisition, commodityCode) {
        var qty = 0;
        requisition.lineItems.forEach(function(li) {
            if (li.item.commodityCode === commodityCode) {
                qty = li.quantity;
            }
        });
        return qty;
    };

    $scope.openReqModal = function (requisition) {
        modals.open('requisition-modal', requisition, true);
    };

    init();
}
