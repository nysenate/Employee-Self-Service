var essSupply = angular.module('essSupply').controller('SupplyOrderController',
    ['$scope', 'appProps', 'SupplyItemsApi', 'SupplyCategoryService', 'LocationService',
        'SupplyCart', 'PaginationModel', supplyOrderController]);

function supplyOrderController($scope, appProps, itemsApi, supplyCategoryService, locationService, supplyCart, paginationModel) {

    $scope.itemSearch = {
        matches: [],
        paginate: angular.extend({}, paginationModel),
        categories: [],
        response: {},
        error: false
    };
    
    $scope.isLocationSelected = true;
    
    $scope.quantity = 1;

    $scope.init = function() {
        $scope.itemSearch.categories = locationService.getSearchParam("category") || [];
        $scope.itemSearch.paginate.currPage = locationService.getSearchParam("page") || 1;
        locationService.setSearchParam("page", $scope.itemSearch.paginate.currPage, true, true);
        $scope.itemSearch.paginate.itemsPerPage = 16;
        $scope.getItems();
    };
    
    $scope.getItems = function(resetPagination) {
        if(resetPagination) {
            $scope.itemSearch.paginate.reset();
            setPageUrlParams();
        }
        var params = {
            category: $scope.itemSearch.categories,
            limit: $scope.itemSearch.paginate.getLimit(),
            offset: $scope.itemSearch.paginate.getOffset()
        };
        $scope.itemSearch.response = itemsApi.get(params, function(response) {
            $scope.itemSearch.matches = response.result;
            $scope.itemSearch.paginate.setTotalItems(response.total);
            $scope.itemSearch.error = false;
        }, function(errorResponse) {
            $scope.itemSearch.matches = [];
            $scope.itemSearch.error = true;
        })
    };
    
    $scope.onPageChange = function() {
        setPageUrlParams();
        $scope.getItems(false);
    };
    
    function setPageUrlParams() {
        locationService.setSearchParam("page", $scope.itemSearch.paginate.currPage, true, false);
    }

    /**
     * Detect url category param changes due to category side bar selections or back/forward browser navigation.
     * Need to reset the item matches when category search criteria changes.
     */
    $scope.$on('$locationChangeStart', function(event, newUrl) {
        if (newUrl.indexOf(appProps.ctxPath + "/supply/order") > -1) { // If still on order page.
            var urlCategories = locationService.getSearchParam("category") || [];
            if (!_.isEqual(urlCategories, $scope.itemSearch.categories)) { // If the category param changed.
                $scope.itemSearch.categories = urlCategories;
                $scope.getItems(true);
            }
        }
    });

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
