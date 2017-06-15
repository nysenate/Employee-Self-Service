essSupply = angular.module('essSupply')
    .controller('SupplyItemHistoryCtrl', ['$scope', 'SupplyUtils', 'SupplyRequisitionApi',
                                          'LocationApi', 'SupplyItemApi', supplyItemHistoryCtrl]);

function supplyItemHistoryCtrl($scope, supplyUtils, requisitionApi, locationApi, itemApi) {

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

    var initFilters = function() {
        locationApi.get().$promise
            .then(initLocationFilter);
        itemApi.items()
            .then(initItemFilter);
        $scope.filters.date.from = moment().subtract(1, 'month').format("MM/DD/YYYY");
        $scope.filters.date.to = moment().format("MM/DD/YYYY");
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
            console.log(item.commodityCode);
            $scope.filters.item.values.push(item.commodityCode);
            $scope.filters.item.codeToId[item.commodityCode] = item.id;
        });
        $scope.filters.item.values = supplyUtils.alphabetizeByName($scope.filters.item.values);
        $scope.filters.item.values.unshift("All");
        $scope.filters.item.selected = $scope.filters.item.values[0];
    };

    $scope.onFilterChange = function () {
        console.log("On filter change.")
    };

    initFilters();
}
