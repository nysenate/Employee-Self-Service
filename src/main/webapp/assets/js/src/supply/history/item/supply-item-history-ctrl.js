essSupply = angular.module('essSupply')
    .controller('SupplyItemHistoryCtrl', ['$scope', '$q', 'SupplyUtils', 'LocationApi', 'SupplyItemHistoryFactory',
                                          'SupplyItemApi', 'modals', supplyItemHistoryCtrl]);

function supplyItemHistoryCtrl($scope, $q, supplyUtils, locationApi, itemHistoryFactory, itemApi, modals) {

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

        itemHistoryFactory.updateItemHistories(
            $scope.filters.item.codeToId[$scope.filters.item.selected] || 'All',
            $scope.filters.location.selected,
            moment($scope.filters.date.from, DATE_FORMAT).startOf('day').format(),
            moment($scope.filters.date.to, DATE_FORMAT).endOf('day').format()
        ).then(function () {
            $scope.loading = false;
        }).catch($scope.handleErrorResponse)
        .finally(function () {
            $scope.result.array = itemHistoryFactory.getItemHistories();
        })
    };

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
