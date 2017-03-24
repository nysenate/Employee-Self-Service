var essSupply = angular.module('essSupply')
    .controller('SupplyOrderController',
                ['$scope', 'LocationService', 'SupplyCartService', 'PaginationModel',
                 'SupplyLocationAutocompleteService', 'SupplyItemApi',
                 'SupplyOrderDestinationService', 'modals', 'SupplyLineItemService',
                 'SupplyItemFilterService', 'SupplyCategoryService', 'SupplyOrderPageStateService',
                 supplyOrderController]);

function supplyOrderController($scope, locationService, supplyCart, paginationModel,
                               locationAutocompleteService, itemApi, destinationService,
                               modals, lineItemService, itemFilterService,
                               categoryService, stateService) {

    // A reference to the stateService on the scope for checking the state in jsp.
    $scope.state = stateService;
    $scope.sorting = {
        Name: 0,
        Category: 10
    };
    $scope.sortBy = $scope.sorting.Alphabet;
    $scope.displaySorting = Object.getOwnPropertyNames($scope.sorting);
    $scope.paginate = angular.extend({}, paginationModel);
    $scope.filter = {
        searchTerm: ""
    };

    /**
     * The line items currently visible to the user.
     * Contains all the items allowed to be ordered at the selected destination
     * minus any items not matching the user defined filters.
     */
    $scope.displayedLineItems = [];

    // The user specified destination code. Defaults to the code of the employees work location.
    $scope.destinationCode = "";
    $scope.destinationDescription = "";

    /** --- Initialization --- */

    $scope.init = function () {
        $scope.state.toLoading();
        $scope.paginate.itemsPerPage = 16;
        updateFiltersFromUrlParams();
        if (!destinationService.isDestinationConfirmed()) {
            loadSelectDestinationState();
        }
        else {
            loadShoppingState();
        }
    };

    $scope.init();

    // Set state to invalid when leaving the order page.
    $scope.$on('$destroy', function () {
        $scope.state.toInvalid();
    });

    /** --- State --- */

    function loadSelectDestinationState() {
        locationAutocompleteService.initWithResponsibilityHeadLocations()
            .then(destinationService.queryDefaultDestination)
            .then(setDestinationCode)
            .then(setToSelectingDestinationState)
            .catch(loadDestinationsError);
    }

    function setDestinationCode() {
        $scope.destinationCode = destinationService.getDefaultCode();
    }

    function setToSelectingDestinationState() {
        $scope.state.toSelectingDestination();
    }

    function loadDestinationsError(response) {
        modals.open('500', {action: 'get valid order destinations', details: response});
    }

    function loadShoppingState() {
        $scope.state.toLoading();
        $scope.destinationCode = destinationService.getDestination().code; // Too much coupling with validator. If this is put in promise, errors occur.
        itemApi.itemsForLoc(destinationService.getDestination().locId)
            .then(initializeCart)
            .then(sortAndFilterLineItems)
            .then(setDestinationDescription)
            .then(setToShoppingState);
    }

    function initializeCart(items) {
        supplyCart.initializeCart(lineItemService.generateLineItems(items));
    }

    function sortAndFilterLineItems() {
        $scope.displayedLineItems = itemFilterService.filterLineItems(supplyCart.getLineItems(),
                                                                      categoryService.getSelectedCategoryNames(),
                                                                      $scope.filter.searchTerm);
        $scope.displayedLineItems = updateSort($scope.displayedLineItems);
    }

    function setToShoppingState() {
        $scope.state.toShopping();
    }

    function setDestinationDescription() {
        $scope.destinationDescription = destinationService.getDestination().locationDescription || "";
    }

    /** --- Search --- */

    $scope.search = function () {
        sortAndFilterLineItems();
    };

    $scope.reset = function () {
        $scope.filter.searchTerm = "";
        sortAndFilterLineItems();
    };

    /** --- Filters --- */

    /** Reset the category and search filters. */
    $scope.resetAllFilters = function () {
        categoryService.clearSelections();
        $scope.reset();
    };

    /** --- Navigation --- */

    /**
     * Synchronizes the categories and currPage objects with the values in the url.
     */
    function updateFiltersFromUrlParams() {
        $scope.paginate.currPage = locationService.getSearchParam("page") || 1;
        // Set page param. This ensures it gets set to 1 if it was never previously set.
        locationService.setSearchParam("page", $scope.paginate.currPage, true, true);
    }

    /**
     * Set the page url parameter when the user changes the page.
     * Triggers the $on('$locationChangeStart') event which will update url params and filter items.
     */
    $scope.onPageChange = function () {
        locationService.setSearchParam("page", $scope.paginate.currPage, true, false);
    };

    /**
     * Detect url param changes due to category side bar selections, page change, or back/forward browser navigation.
     * Update local $scope params to match the new url params and filter items for any categories specified.
     *
     * Note: Depends on '$locationChangeStart' in supply-category-nav-ctrl to
     * initialize the categories before this runs.
     */
    $scope.$on('$locationChangeSuccess', function (event, newUrl) {
        if ($scope.state.isShopping()) {
            updateFiltersFromUrlParams();
            sortAndFilterLineItems();
        }
    });

    /** --- Location selection --- */

    $scope.confirmDestination = function () {
        var success = destinationService.setDestination($scope.destinationCode);
        if (success) {
            loadShoppingState();
        }
    };

    $scope.getLocationAutocompleteOptions = function () {
        return locationAutocompleteService.getLocationAutocompleteOptions();
    };

    $scope.resetDestination = function () {
        if (supplyCart.isEmpty())
            reset();
        else {
            modals.open('order-canceling-modal')
                .then(reset);
        }

        function reset() {
            $scope.state.toLoading();
            supplyCart.reset();
            destinationService.reset();
            locationService.go("/supply/order", true);
        }
    };

    /** --- Sorting  --- */

    /**
     * Sort the given line items by the selected value.
     */
    function updateSort(lineItems) {
        var cur = locationService.getSearchParam("sortBy") || [];
        if (cur.length == 0 || cur[0] != $scope.sortBy) {
            locationService.setSearchParam("sortBy", $scope.sortBy, true, false);
        }
        if ($scope.sorting[$scope.sortBy] == $scope.sorting.Name) {
            lineItems.sort(function (a, b) {
                if (a.item.description < b.item.description) return -1;
                if (a.item.description > b.item.description) return 1;
                return 0;
            });
        }
        else if ($scope.sorting[$scope.sortBy] == $scope.sorting.Category) {
            lineItems.sort(function (a, b) {
                if (a.item.category < b.item.category) return -1;
                if (a.item.category> b.item.category) return 1;
                return 0;
            });
        }
        return lineItems;
    }

    /** --- Modals --- */

    $scope.displayLargeImage = function (commodityCode) {
        modals.open('large-item-image-modal', {commodityCode: commodityCode}, true)
    }
}

/**
 * Directive for validating destination selection.
 */
essSupply.directive('destinationValidator', ['SupplyLocationAutocompleteService', function (locationAutocompleteService) {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.destination = function (modelValue, viewValue) {
                return locationAutocompleteService.isValidCode(modelValue) || modelValue.length === 0;
            };

            /**
             * THIS IS A HACKY WAY TO SOLVE   #10625
             */
            elm.on('autocompleteselect', function (a, object, e, c) {
                angular.element("form[name=selectDestinationForm]").scope().selectDestinationForm.destination.$error.destination = false;
                if (!angular.element("form[name=selectDestinationForm]").scope().destinationCode)
                    angular.element("form[name=selectDestinationForm]").scope().destinationCode = object.item.label.split("(")[0].trim()
            });
        }
    }
}]);
