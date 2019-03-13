var essSupply = angular.module('essSupply')
    .controller('SupplyOrderController',
                ['$scope', 'appProps', 'LocationService', 'SupplyCartService', 'PaginationModel',
                 'SupplyDestinationApi', 'SupplyItemApi',
                 'SupplyOrderDestinationService', 'modals', 'SupplyLineItemService',
                 'SupplyItemFilterService', 'SupplyCategoryService', 'SupplyOrderPageStateService', 'EmpInfoApi',
                 supplyOrderController]);

function supplyOrderController($scope, appProps, locationService, supplyCart, paginationModel,
                               destinationApi, itemApi, destinationService,
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

    $scope.destinations = {
        allowed: [],
        selected: undefined,
        isWorkLocationError: false,
        isRchLocationError: false
    };

    /** --- Initialization --- */

    $scope.init = function () {
        $scope.state.toLoading();
        $scope.paginate.itemsPerPage = 16;
        updateFiltersFromUrlParams();

        if (!destinationService.isDestinationConfirmed()) {
            queryEmployee()
                .then(loadSelectDestinationState)
        } else {
            queryEmployee()
                .then(loadShoppingState);
        }
    };

    $scope.init();

    // Set state to invalid when leaving the order page.
    $scope.$on('$destroy', function () {
        $scope.state.toInvalid();
    });

    function queryEmployee() {
        return empInfoApi.get({empId: appProps.user.employeeId, detail: true}).$promise
            .then(function (response) {
                $scope.employee = response.employee;
            });
    }

    /** --- State --- */

    function loadSelectDestinationState() {
        $scope.state.toLoading();
        initAllowedDestinations()
            .then(selectDefaultDestination)
            .then(checkForLocationErrors)
            .then(setToSelectingDestinationState)
            .catch($scope.handleErrorResponse);
    }

    function initAllowedDestinations() {
        return destinationApi.get({empId: appProps.user.employeeId}).$promise
            .then(saveDestinations);

        function saveDestinations(response) {
            $scope.destinations.allowed = response.result;
        }
    }

    function selectDefaultDestination() {
        $scope.destinations.allowed.forEach(function (dest) {
            if ($scope.employee.empWorkLocation.code === dest.code) {
                $scope.destinations.selected = dest;
            }
        });

        // If selected did not get set default it to the first.
        if ($scope.destinations.selected === undefined) {
            $scope.destinations.selected = $scope.destinations.allowed[0];
        }
    }

    function checkForLocationErrors() {
        $scope.destinations.isWorkLocationError = true;
        $scope.destinations.isRchLocationError = true;

        $scope.destinations.allowed.forEach(function (dest) {
            if ($scope.employee.empWorkLocation.code === dest.code) {
                $scope.destinations.isWorkLocationError = false;
            }

            if ($scope.employee.respCtr.respCenterHead.code === dest.respCenterHead.code) {
                $scope.destinations.isRchLocationError = false;
            }
        });
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
        var success = destinationService.setDestination($scope.destinations.selected);
        if (success) {
            loadShoppingState();
        }
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
