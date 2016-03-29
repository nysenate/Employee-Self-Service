var essSupply = angular.module('essSupply').controller('SupplyNavigationController',
    ['$scope', 'appProps', 'LocationService', 'SupplyCategoryService', supplyNavigationController]);

function supplyNavigationController($scope, appProps, locationService, SupplyCategoryService) {

    $scope.categories = null;
    $scope.shouldDisplayCategoryFilter = null;

    $scope.init = function() {
        $scope.categories = SupplyCategoryService.getCategories();
    };

    /** Only display the categories sidebar if on requisition order page.
     * Update selected categories when back/forward navigation is used. */
    $scope.$on('$locationChangeStart', function(event, newUrl) {
        $scope.shouldDisplayCategoryFilter = isRequisitionOrderPage(newUrl);
        if($scope.shouldDisplayCategoryFilter) {
            updateSelectedCategoriesFromUrl();
        }
    });

    function isRequisitionOrderPage(url) {
        return url.indexOf(appProps.ctxPath + "/supply/order/order") > -1;
    }

    /**
     * This updates the category sidebar checkboxes to match the categories
     * specified in the url params when back/forward navigation is used in the browser.
     */
    function updateSelectedCategoriesFromUrl() {
        var categoryNamesInUrl = locationService.getSearchParam("category") || [];
        angular.forEach($scope.categories, function(category) {
            category.selected = categoryNamesInUrl.indexOf(category.name) !== -1;
        });
    }

    /**
     * When a category is selected, add it to the url params.
     * The order page controller depends on the url params to know which categories are selected.
     */
    $scope.onCategorySelected = function() {
        var selectedCategoryNames = [];
        angular.forEach($scope.categories, function(category) {
            if(category.selected) selectedCategoryNames.push(category.name);
        });
        locationService.setSearchParam("category", selectedCategoryNames, true, false);
    };
    
    $scope.init();
}