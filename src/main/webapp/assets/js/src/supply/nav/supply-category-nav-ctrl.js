var essSupply = angular.module('essSupply').controller('SupplyNavigationController',
    ['$scope', '$location', 'appProps', 'LocationService', 'SupplyCategoryService', 'SupplyOrderDestinationService', supplyNavigationController]);

function supplyNavigationController($scope, $location, appProps, locationService, categoryService, destinationService) {

    $scope.getCategories = function () {
        return categoryService.getCategories();
    };

    $scope.shouldDisplayCategoryFilter = function () {
        return onRequisitionOrderPage($location.path()) && destinationIsSelected();
    };

    function onRequisitionOrderPage(path) {
        return path === appProps.ctxPath + "/supply/order"
    }

    function destinationIsSelected() {
        return destinationService.isDestinationConfirmed();
    }

    /** Update selected categories when back/forward navigation is used. */
    $scope.$on('$locationChangeStart', function (event, newUrl) {
        if ($scope.shouldDisplayCategoryFilter) {
            updateSelectedCategoriesFromUrl();
        }
    });

    /**
     * This updates the category sidebar checkboxes to match the categories
     * specified in the url params when back/forward navigation is used in the browser.
     */
    function updateSelectedCategoriesFromUrl() {
        var categoryNamesInUrl = locationService.getSearchParam("category") || [];
        angular.forEach(categoryService.getCategories(), function (category) {
            category.selected = categoryNamesInUrl.indexOf(category.name) !== -1;
        });
    }

    /**
     * When a category is selected, add it to the url params.
     * The order page controller depends on the url params to know which categories are selected.
     */
    $scope.onCategorySelected = function () {
        var selectedCategoryNames = [];
        angular.forEach(categoryService.getCategories(), function (category) {
            if (category.selected) selectedCategoryNames.push(category.name);
        });
        locationService.setSearchParam("category", selectedCategoryNames, true, false);
    };

    $scope.clearSelections = function () {
        angular.forEach(categoryService.getCategories(), function (cat) {
            cat.selected = false;
        });
        $scope.onCategorySelected();
    };
}