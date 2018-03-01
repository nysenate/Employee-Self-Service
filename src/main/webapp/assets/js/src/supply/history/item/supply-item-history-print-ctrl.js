essSupply = angular.module('essSupply')
    .controller('SupplyItemHistoryPrintCtrl', ['$scope', 'SupplyItemHistoryFactory', 'SupplyItemApi',
                                               '$window', '$timeout', supplyItemHistoryPrintCtrl]);

function supplyItemHistoryPrintCtrl($scope, itemHistoryFactory, itemApi, $window, $timeout) {

    $scope.itemHistories = [];
    $scope.params = {};
    $scope.commodityCode = '';

    function init() {
        $scope.itemHistories = itemHistoryFactory.getItemHistories();
        $scope.params = itemHistoryFactory.getParams();
        if ($scope.params.itemId === 'All') {
           $scope.commodityCode = 'All';
        }
        else {
            itemApi.item($scope.params.itemId)
                .then(function (item) {
                    $scope.commodityCode = item.commodityCode;
                });
        }
        print();
    }

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

    $scope.getTotalQuantity = function() {
        var total = 0;
        $scope.itemHistories.forEach(function(i) {
            total += i.quantity;
        });
        return total;
    };

    function print() {
        $timeout(function () {
            $window.print();
        });
    }

    init();
}