var essSupply = angular.module('essSupply')
    .controller('SupplyNavigationController',
                ['$scope', 'LocationService', 'SupplyCategoryService', 'SupplyOrderPageStateService',
                 supplyNavigationController]);

function supplyNavigationController($scope, locationService, categoryService, stateService) {

    // Attach the stateService to the scope object for access in jsp.
    $scope.state = stateService;
    $scope.categories = [];

    /**
     * On every state change, call the stateChangeCallback function.
     */
    function init() {
        $scope.state.subscribe($scope, stateChangeCallback);
    }

    init();

    /**
     * Callback function passed to the SupplyOrderPageStateService.
     * When the state transitions to shopping, initialize categories.
     * Otherwise reset categories.
     *
     * Categories are not initialized until just before the
     * transition to shopping state.
     */
    function stateChangeCallback() {
        if ($scope.state.isShopping()) {
            $scope.categories = categoryService.getCategories();
        }
        else {
            $scope.categories = [];
        }
    }

    /**
     * Handles forward/back browser navigation by updating the selected categories.
     *
     * Note: There is some coupling between this function and the '$locationChangeSuccess'
     * in supply-order-ctrl. That function initializes displayed items using the
     * categories set by this function.
     */
    $scope.$on('$locationChangeStart', function (event, newUrl, oldUrl) {
        if ($scope.state.isShopping()) {
            categoryService.setSelectedCategories(locationService.getSearchParam("category") || []);
        }
    });

    /**
     * Called when user selects or deselects a category.
     */
    $scope.onCategoryUpdated = function () {
        categoryService.updateUrlParam();
    };

    $scope.clearSelections = function () {
        categoryService.clearSelections();
    };
}