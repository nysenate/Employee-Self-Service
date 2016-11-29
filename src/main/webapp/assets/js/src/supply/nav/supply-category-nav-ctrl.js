var essSupply = angular.module('essSupply').controller('SupplyNavigationController',
    ['$scope', '$location', 'appProps', 'LocationService', 'SupplyCategoryService', 'SupplyOrderDestinationService', supplyNavigationController]);

/**
 * This controls the category filter on the Supply app's navigation.
 * Categories use the same references that are in the SupplyCategoryService,
 * so anytime a category is selected or deselected, the SupplyCategoryService
 * is automatically updated with that change. Other controllers use the
 * SupplyCategoryService to determine which categories are selected.
 *
 * This controller also controls the 'category' url search param. This parameter
 * contains an array of category names which should always match the currently
 * selected categories on the navigation.
 * Whenever a change to the url occurs, the selected categories are updated
 * to match those in the 'category' url param. This allows forward and back browser navigation.
 * Whenever a category is selected or deselected, the 'category' url param
 * is updated to reflect those changes.
 *
 * Note: The category filter and most of this controllers functionality
 * is only active when on the order page.
 */
function supplyNavigationController($scope, $location, appProps, locationService, categoryService, destinationService) {

    var categoryNamesInUrl = locationService.getSearchParam("category") || [];

    $scope.updateWithURL = function (e) {
        if (categoryNamesInUrl.length == 0)
            return;
        if (categoryNamesInUrl.indexOf(e.name) != -1)
            e.selected = true;
    };

    $scope.getCategories = function () {
        return categoryService.getCategories();
    };

    $scope.shouldDisplayCategoryFilter = function () {
        return onRequisitionOrderPage($location.path()) && destinationIsSelected();

        function onRequisitionOrderPage(path) {
            return path === appProps.ctxPath + "/supply/order"
        }

        function destinationIsSelected() {
            return destinationService.isDestinationConfirmed();
        }
    };

    /**
     * When url is changed, if on the order page, update the selected
     * categories to match those in the url search parameters.
     * This keeps the categories selected in the navigation bar
     * and those in url search params synchronized.
     * Also allows forward/back button navigation.
     */
    $scope.$on('$locationChangeStart', function (event, newUrl) {
        if ($scope.shouldDisplayCategoryFilter) {
            categoryService.setSelectedCategories(locationService.getSearchParam("category") || []);
        }
    });

    /**
     * Updates the 'category' url search parameter whenever a user
     * selects or deselects a category filter.
     */
    $scope.onCategoryUpdated = function () {
        locationService.setSearchParam("category", categoryService.getSelectedCategoryNames(), true, false);
    };

    $scope.clearSelections = function () {
        categoryService.clearSelections();
        $scope.onCategoryUpdated();
    };
}