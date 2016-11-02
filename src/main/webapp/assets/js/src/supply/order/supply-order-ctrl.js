var essSupply = angular.module('essSupply')
    .controller('SupplyOrderController', ['$scope', 'appProps', 'LocationService', 'SupplyCartService',
        'PaginationModel', 'SupplyLocationAutocompleteService', 'SupplyLocationAllowanceService',
        'SupplyOrderDestinationService', 'modals', 'SupplyUtils', 'SupplyLineItemService',
        'SupplyItemFilterService', supplyOrderController]);

function supplyOrderController($scope, appProps, locationService, supplyCart, paginationModel, locationAutocompleteService,
                               allowanceService, destinationService, modals, supplyUtils, lineItemService, itemFilterService) {
    $scope.state = {};
    $scope.states = {
        LOADING: 0,
        SELECTING_DESTINATION: 5,
        SHOPPING: 10
    };
    $scope.sorting = {
        Name: 0,
        Category: 10
    };
    $scope.sortBy = $scope.sorting.Alphabet;
    $scope.displaySorting = Object.getOwnPropertyNames($scope.sorting);
    $scope.paginate = angular.extend({}, paginationModel);
    $scope.filter = {
        searchTerm: "",
        categories: []
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
        $scope.state = $scope.states.LOADING;
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
        $scope.state = $scope.states.SELECTING_DESTINATION;
    }

    function loadDestinationsError(response) {
        modals.open('500', {action: 'get valid order destinations', details: response});
    }

    function loadShoppingState() {
        $scope.state = $scope.states.LOADING;
        $scope.destinationCode = destinationService.getDestination().code; // Too much coupling with validator. If this is put in promise, errors occur.
        allowanceService.queryLocationAllowance(destinationService.getDestination())
            .then(initializeCart)
            .then(filterLineItems)
            .then(setToShoppingState)
            .then(setDestinationDescription)
            .then($scope.updateSort)
            .catch(loadItemsError);
    }

    function initializeCart(allowanceResponse) {
        var items = [];
        angular.forEach(allowanceResponse.result.itemAllowances, function (allowance) {
            items.push(allowance.item);
        });
        supplyCart.initializeCart(lineItemService.generateLineItems(items));
    }

    function filterLineItems() {
        $scope.displayedLineItems = itemFilterService.filterLineItems(supplyCart.getLineItems(), $scope.filter.categories, $scope.filter.searchTerm);
        $scope.displayedLineItems = supplyUtils.alphabetizeLineItems($scope.displayedLineItems);
    }

    function setToShoppingState() {
        $scope.state = $scope.states.SHOPPING;
    }

    function setDestinationDescription() {
        $scope.destinationDescription = destinationService.getDestination().locationDescription || "";
    }

    function loadItemsError(response) {
        modals.open('500', {action: 'get supply items', details: response});
    }

    /** --- Search --- */

    $scope.search = function () {
        filterLineItems();
    };

    /** --- Reset --- */
    $scope.reset = function () {
        $scope.filter.searchTerm = "";
        filterLineItems();
    };

    /** --- Navigation --- */

    /**
     * Synchronizes the categories and currPage objects with the values in the url.
     */
    function updateFiltersFromUrlParams() {
        $scope.filter.categories = locationService.getSearchParam("category") || [];
        $scope.paginate.currPage = locationService.getSearchParam("page") || 1;
        // Set page param. This ensures it gets set to 1 if it was never previously set.
        locationService.setSearchParam("page", $scope.paginate.currPage, true, true);
    }

    /**
     * Set the page url parameter when the user changes the page.
     * Triggers the $on('$locationChangeStart') event which will update url params and filter allowances.
     */
    $scope.onPageChange = function () {
        locationService.setSearchParam("page", $scope.paginate.currPage, true, false);
    };

    /**
     * Detect url param changes due to category side bar selections, page change, or back/forward browser navigation.
     * Update local $scope params to match the new url params and filter allowances for any categories specified.
     */
    $scope.$on('$locationChangeStart', function (event, newUrl) {
        if (newUrl.indexOf(appProps.ctxPath + "/supply/order") > -1) { // If still on order page.
            updateFiltersFromUrlParams();
            filterLineItems();
        }
    });

    /** --- Shopping --- */

    $scope.addToCart = function (lineItem) {
        // first time adding special item, display modal.
        if (!supplyCart.isItemIdOrdered(lineItem.item.id) && lineItem.item.visibility === 'SPECIAL') {
            modals.open('special-order-item-modal', {lineItem: lineItem})
                .then(function() {
                    lineItem.increment();
                    updateAndSaveCart(lineItem);
                })
        }
        else {
            lineItem.increment();
            updateAndSaveCart(lineItem);
        }
    };

    $scope.decrementQuantity = function (lineItem) {
        lineItem.decrement();
        updateAndSaveCart(lineItem)
    };

    $scope.incrementQuantity = function (lineItem) {
        lineItem.increment();
        updateAndSaveCart(lineItem)
    };

    $scope.onCustomQtyEntered = function (lineItem) {
        if (lineItem.quantity > lineItem.item.maxQtyPerOrder) {
            modals.open('order-more-prompt-modal', {lineItem: lineItem})
                .then(updateAndSaveCart)
                .catch(function() {
                    // Reset displayedLineItem quantity to the old value.
                    lineItem.quantity = supplyCart.getCartLineItem(lineItem.item.id).quantity;
                })
        }
        else {
            updateAndSaveCart(lineItem);
        }
    };

    function updateAndSaveCart(lineItem) {
        supplyCart.updateCartLineItem(lineItem);
        supplyCart.save();
    }

    $scope.isInCart = function (item) {
        return supplyCart.isItemIdOrdered(item.id);
    };

    // /** This is called whenever an items quantity is changed.
    //  * Used to determine when "more" is selected. */
    // $scope.quantityChanged = function (allowance) {
    //     if (allowance.selectedQuantity === "more" || $scope.getItemRemainQuantities(allowance.item) == 0) {
    //         modals.open('order-more-prompt-modal', {allowance: allowance})
    //             .then(function (allowance) {
    //                 modals.open('order-custom-quantity-modal', {item: allowance.item});
    //             });
    //     }
    // };

    /** --- Location selection --- */

    $scope.confirmDestination = function () {
        var success = destinationService.setDestination($scope.destinationCode);
        if (success) {
            loadShoppingState();
        }
    };
    $scope.$on('$locationChangeStart', function (event, newUrl) {
        $scope.updateSort();
    });

    $scope.getLocationAutocompleteOptions = function () {
        return locationAutocompleteService.getLocationAutocompleteOptions();
    };

    $scope.resetDestination = function () {
        if (supplyCart.getCart().length > 0)
            modals.open('order-canceling-modal');
        else {
            destinationService.reset();
            locationService.go("/supply/order", true);
        }
    };

    /** --- Sorting  --- */
    $scope.updateSort = function () {
        var cur = locationService.getSearchParam("sortBy") || [];
        if (cur.length == 0 || cur[0] != $scope.sortBy) {
            locationService.setSearchParam("sortBy", $scope.sortBy, true, false);
        }
        var lineItemsCopy = angular.copy($scope.displayedLineItems);
        if ($scope.sorting[$scope.sortBy] == $scope.sorting.Name) {
            lineItemsCopy.sort(function (a, b) {
                if (a.item.description < b.item.description) return -1;
                if (a.item.description > b.item.description) return 1;
                return 0;
            });
        }
        else if ($scope.sorting[$scope.sortBy] == $scope.sorting.Category) {
            lineItemsCopy.sort(function (a, b) {
                if (a.item.category.name < b.item.category.name) return -1;
                if (a.item.category.name > b.item.category.name) return 1;
                return 0;
            });
        }
        $scope.displayAllowances = lineItemsCopy;
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
            }
        }
    }
}]);

/**
 * Validator for entering custom order quantities.
 * Limits key input to number keys and navigation keys.
 */
essSupply.directive('orderQuantityValidator', [function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ngModel) {
            // Only allow numbers, backspace, tab, and F5 keys to be pressed.
            elm.bind("keydown", function (event) {
                if (event.keyCode === 8 || event.keyCode === 9 || event.keyCode === 116) {
                    return;
                }
                if (event.keyCode < 48 || event.keyCode > 57) {
                    event.preventDefault();
                }
            });
        }
    }
}]);
