var essSupply = angular.module('essSupply').controller('SupplyOrderController',
    ['$scope', 'SupplyItemsApi', 'SupplyCategoryService',
        'SupplyCart', 'PaginationModel', supplyOrderController]);

function supplyOrderController($scope, itemsApi, supplyCategoryService, supplyCart, paginationModel) {

    $scope.itemSearch = {
        matches: [],
        paginate: angular.extend({}, paginationModel),
        response: {},
        error: false
    };
    $scope.items = null;
    $scope.quantity = 1;

    $scope.init = function() {
        itemsApi.get(function(response) {
            $scope.items = response.result;
        });
    };
    
    $scope.getItems = function(resetPagination) {
        if(resetPagination) {
            $scope.itemSearch.paginate.reset();
        }
        var params = {
            limit: $scope.itemSearch.paginate.getLimit(),
            offset: $scope.itemSearch.paginate.getOffset()
        };
        $scope.itemSearch.response = itemsApi.get(params, function(response) {
            // $scope.itemSearch.
        }, function(errorResponse) {
            
        })
    };

    // Called by ng-hide in the view. Returns true if a item does not belong to the selected categories.
    $scope.hideItem = function(item) {
        var names = supplyCategoryService.getSelectedCategoryNames();
        // If no filters selected, show all items.
        if (names.length === 0) {
            return false;
        }
        return names.indexOf(item.category) === -1;
    };

    $scope.addToCart = function(item, qty) {
        supplyCart.addToCart(item, qty);
    };

    $scope.isInCart = function(item) {
        return supplyCart.itemInCart(item.id)
    };

    $scope.orderQuantityRange = function(item) {
        var range = [];
        for (var i = 1; i <= item.suggestedMaxQty * 2; i++) {
            range.push(i);
        }
        return range;
    };

    $scope.init();
}
