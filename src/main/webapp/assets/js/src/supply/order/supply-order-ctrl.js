var essSupply = angular.module('essSupply')
    .controller('SupplyOrderController', ['$scope', 'appProps', 'LocationService', 'SupplyCartService',
        'PaginationModel', 'SupplyLocationAutocompleteService', 'SupplyLocationAllowanceService',
        'SupplyOrderDestinationService', 'modals', 'SupplyUtils', supplyOrderController]);

function supplyOrderController($scope, appProps, locationService, supplyCart, paginationModel, locationAutocompleteService,
                               allowanceService, destinationService, modals, supplyUtils) {
    $scope.state = {};
    $scope.sorting = {
        Name: 0,
        Category: 10
    };
    $scope.sortBy = $scope.sorting.Alphabet;
    $scope.states = {
        LOADING: 0,
        SELECTING_DESTINATION: 5,
        SHOPPING: 10
    };
    $scope.displaySorting = Object.getOwnPropertyNames($scope.sorting);

    $scope.paginate = angular.extend({}, paginationModel);

    $scope.filter = {
        searchTerm: "",
        categories: []
    };

    // All allowances for the selected destination.
    var allowances = [];

    // An array of allowances which match the current filters.
    $scope.displayAllowances = [];

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

    function setDestinationDescription() {
        $scope.destinationDescription = destinationService.getDestination().locationDescription || "";
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
            .then(saveAllowances)
            .then(filterAllowances)
            .then(setAllowances)
            .then(setToShoppingState)
            .then(setDestinationDescription)
            .then(checkSortOrder)
            .catch(loadItemsError);
    }

    function saveAllowances(allowanceResponse) {
        allowances = allowanceResponse.result.itemAllowances;
    }

    function filterAllowances() {
        $scope.displayAllowances = allowanceService.filterAllowances(allowances, $scope.filter.categories, $scope.filter.searchTerm);
        $scope.displayAllowances = supplyUtils.alphabetizeAllowances($scope.displayAllowances);
    }

    function setAllowances() {
        if (supplyCart.getCart().length > 0) {
            supplyCart.getCart().forEach(function (item) {
                $scope.displayAllowances.forEach(function (allowance) {
                    if (item.item.id == allowance.item.id)
                        if (item.quantity > allowance.item.maxQtyPerOrder)
                            allowance.selectedQuantity = "more";
                })
            })
        }
    }

    function setToShoppingState() {
        $scope.state = $scope.states.SHOPPING;
    }

    function checkSortOrder(allowance) {
        $scope.updateSort();
    }

    function loadItemsError(response) {
        modals.open('500', {action: 'get supply items', details: response});
    }

    function Reset() {
        $scope.filter.searchTerm = "";
        filterAllowances();
    }

    /** --- Search --- */

    $scope.search = function () {
        filterAllowances();
    };

    /** --- Reset --- */
    $scope.reset = function () {
        Reset();
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
            filterAllowances();
        }
    });

    /** --- Shopping --- */

    $scope.addToCart = function (allowance) {
        // If more is selected, display
        if (allowance.selectedQuantity === "more" || $scope.getItemRemainQuantities(allowance.item) == 0) {
            $scope.quantityChanged(allowance);
            return;
        }
        if (isNaN(allowance.selectedQuantity)) {
            return;
        }
        // Cant add more than is allowed per order.
        if (supplyCart.isOverOrderAllowance(allowance.item, allowance.selectedQuantity)) {
            return;
        }
        // first time adding special item, display modal.
        if (!supplyCart.isItemInCart(allowance.item.id) && allowance.visibility === 'SPECIAL') {
            modals.open('special-order-item-modal', {allowance: allowance});
        }
        else {
            supplyCart.addToCart(allowance.item, allowance.selectedQuantity);
        }
    };
    $scope.isInCart = function (item) {
        return supplyCart.isItemInCart(item.id);
    };

    $scope.getItemQuantity = function (item) {
        if (supplyCart.isItemInCart(item.id))
            return supplyCart.getCartLineItem(item.id).quantity;
        else
            return 0;
    };
    $scope.getItemAllowedQuantities = function (item) {
        return allowanceService.getAllowedQuantities(item).slice(-1)[0];
    };
    $scope.getItemTestSpecialOrder = function (item) {
        if ($scope.getItemAllowedQuantities(item) < $scope.getItemQuantity(item))
            return "Yes";
        else
            return "No"
    };
    $scope.getItemRemainQuantities = function (item) {
        if ($scope.getItemAllowedQuantities(item) - $scope.getItemQuantity(item) >= 0 || item.visibility === 'SPECIAL')
            return $scope.getItemAllowedQuantities(item) - $scope.getItemQuantity(item);
        else
            return 0;
    };


    $scope.getAllowedQuantities = function (item) {
        var allowedQuantities = allowanceService.getAllowedQuantities(item);
        allowedQuantities.push("more");
        return allowedQuantities;
    };

    /** This is called whenever an items quantity is changed.
     * Used to determine when "more" is selected. */
    $scope.quantityChanged = function (allowance) {
        if (allowance.selectedQuantity === "more" || $scope.getItemRemainQuantities(allowance.item) == 0) {
            modals.open('order-more-prompt-modal', {allowance: allowance})
                .then(function (allowance) {
                    modals.open('order-custom-quantity-modal', {item: allowance.item});
                });
        }
    };

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

    $scope.resetDestination = function (body) {
        supplyCart.reset();
        destinationService.reset();
        locationService.go("/supply/order", true);
    };

    $scope.backHidden = function () {
        return $scope.state == $scope.states.SELECTING_DESTINATION;
    };

    /** --- Sorting  --- */
    $scope.updateSort = function () {
        var cur = locationService.getSearchParam("sortBy") || [];
        if (cur.length == 0 || cur[0] != $scope.sortBy) {
            locationService.setSearchParam("sortBy", $scope.sortBy, true, false);
        }
        var allowancesCopy = angular.copy($scope.displayAllowances);
        if ($scope.sorting[$scope.sortBy] == $scope.sorting.Name) {
            allowancesCopy.sort(function (a, b) {
                if (a.item.description < b.item.description) return -1;
                if (a.item.description > b.item.description) return 1;
                return 0;
            });
        }
        else if ($scope.sorting[$scope.sortBy] == $scope.sorting.Category) {
            allowancesCopy.sort(function (a, b) {
                if (a.item.category.name < b.item.category.name) return -1;
                if (a.item.category.name > b.item.category.name) return 1;
                return 0;
            });
        }
        $scope.displayAllowances = allowancesCopy;
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
 * Sets maximum input length to 4 digits.
 * See order-custom-quantity-modal.jsp for an example of usage.
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

            var maxLength = 4;
            scope.$watch(attrs.ngModel, function (newValue) {
                var value = ngModel.$viewValue;
                // Limit the max length of input.
                if (value.length > maxLength) {
                    ngModel.$setViewValue(value.substring(0, maxLength));
                    ngModel.$render();
                }
                // Trim the leading zeros out of numbers.
                if (value.indexOf(0) == 0 && value.length > 1) {
                    ngModel.$setViewValue(value.substring(1, value.length));
                    ngModel.$render();
                }
            });
        }
    }
}]);
