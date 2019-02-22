var essSupply = angular.module('essSupply')
    .controller('SupplyOrderController',
                ['$scope', 'appProps', 'LocationService', 'SupplyCartService', 'PaginationModel',
                 'SupplyLocationAutocompleteService', 'SupplyItemApi',
                 'SupplyOrderDestinationService', 'modals', 'SupplyLineItemService',
                 'SupplyItemFilterService', 'SupplyCategoryService', 'SupplyOrderPageStateService', 'EmpInfoApi',
                 supplyOrderController]);

function supplyOrderController($scope, appProps, locationService, supplyCart, paginationModel,
                               locationAutocompleteService, itemApi, destinationService,
                               modals, lineItemService, itemFilterService,
                               categoryService, stateService, empInfoApi) {

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

    $scope.employee = {}; // The logged in employee

    // The selected destination for this order.
    $scope.destination = {};
    $scope.isErrorWithWorkLocation = false;

    /** --- Initialization --- */

    $scope.init = function () {
        $scope.state.toLoading();
        $scope.paginate.itemsPerPage = 16;
        updateFiltersFromUrlParams();
        queryEmployee();

        if (!destinationService.isDestinationConfirmed()) {
            loadSelectDestinationState();
        } else {
            loadShoppingState();
        }
    };

    $scope.init();

    // Set state to invalid when leaving the order page.
    $scope.$on('$destroy', function () {
        $scope.state.toInvalid();
    });

    function queryEmployee() {
        empInfoApi.get({empId: appProps.user.employeeId, detail: true}).$promise
            .then(function (response) {
                $scope.employee = response.employee;
            });
    }

    /** --- State --- */

    function loadSelectDestinationState() {
        locationAutocompleteService.initWithUsersAllowedDestinations()
            .then(destinationService.queryDefaultDestination)
            .then(setDestination)
            .then(setToSelectingDestinationState)
            .catch($scope.handleErrorResponse);
    }

    // Defaults the destination selection to the employees work location if possible.
    // If the work location is not in the allowed destination list, set a flag so a warning message can be shown.
    function setDestination() {
        var defaultLocation = locationAutocompleteService.getLocationFromCode(destinationService.getDefaultCode());
        if (defaultLocation) {
            $scope.isErrorWithWorkLocation = false;
            $scope.destination = defaultLocation;
        } else {
            $scope.isErrorWithWorkLocation = true;
            // If the employee work location was not in allowed destination list, default to the first destination.
            $scope.destination = locationAutocompleteService.getLocations()[0];
        }
    }

    function setToSelectingDestinationState() {
        $scope.state.toSelectingDestination();
    }

    function loadShoppingState() {
        $scope.state.toLoading();
        $scope.destination = destinationService.getDestination();
        itemApi.itemsForLoc($scope.destination.locId)
            .then(initializeCart)
            .then(sortAndFilterLineItems)
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
        if ($scope.state.isShopping() && newUrl.indexOf("supply/shopping/order") != -1) {
            updateFiltersFromUrlParams();
            sortAndFilterLineItems();
        }
    });

    /** --- Location selection --- */

    $scope.confirmDestination = function () {
        var success = destinationService.setDestination($scope.destination);
        if (success) {
            loadShoppingState();
        }
    };

    $scope.allowedDestinations = function () {
        locs = locationAutocompleteService.getLocations();
        locs.forEach(function (loc) {
            loc['selectDescription'] = loc.code + ' (' + loc.locationDescription + ')'
        });
        return locs;
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
            locationService.go("/supply/shopping/order", true);
        }
    };

    $scope.hasPotentialRchErrors = function () {
        var missingRchLocation = true;
        angular.forEach(locationAutocompleteService.getLocations(), function (loc) {
            if (loc.respCenterHead.code === $scope.employee.respCtr.respCenterHead.code) {
                missingRchLocation = false;
            }
        });
        return missingRchLocation;
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
        } else if ($scope.sorting[$scope.sortBy] == $scope.sorting.Category) {
            lineItems.sort(function (a, b) {
                if (a.item.category < b.item.category) return -1;
                if (a.item.category > b.item.category) return 1;
                return 0;
            });
        }
        return lineItems;
    }

    /** --- Modals --- */

    $scope.displayLargeImage = function (item) {
        modals.open('large-item-image-modal', {item: item}, true)
    }
}
