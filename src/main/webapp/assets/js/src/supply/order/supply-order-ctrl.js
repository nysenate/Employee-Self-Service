var essSupply = angular.module('essSupply').controller('SupplyOrderController',
    ['$scope', 'appProps', 'LocationService', 'SupplyCartService', 'PaginationModel', 'SupplyLocationAutocompleteService',
        'SupplyLocationAllowanceService', 'SupplyOrderDestinationService', 'modals', 'SupplyUtils', 'LocationApi', supplyOrderController]);

function supplyOrderController($scope, appProps, locationService, supplyCart, paginationModel, locationAutocompleteService,
                               allowanceService, destinationService, modals, supplyUtils, locationApi) {
    $scope.state = {};
    $scope.states = {
        LOADING: 0,
        SELECTING_DESTINATION: 5,
        SHOPPING: 10
    };
    var locations = [];
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

    $scope.description = "";

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
        locationApi.get().$promise
            .then(setDescription)
    };

    var setDescription = function (response) {
        response.result.forEach(function (location) {
            if (location.code === $scope.destinationCode) {
                $scope.description = location.locationDescription;

            }
        })
    };

    $scope.init();

    /** --- State --- */

    function loadSelectDestinationState() {
        locationAutocompleteService.initWithResponsibilityHeadLocations()
            .then(destinationService.queryDefaultDestination)
            .then(setDestinationCode)
            .then(setToSelectingDestinationState);
    }

    function setDestinationCode() {
        $scope.destinationCode = destinationService.getDefaultCode();
    }

    function setToSelectingDestinationState() {
        $scope.state = $scope.states.SELECTING_DESTINATION;
    }

    function loadShoppingState() {
        $scope.state = $scope.states.LOADING;
        $scope.destinationCode = destinationService.getDestination().code;
        allowanceService.queryLocationAllowance(destinationService.getDestination())
            .then(saveAllowances)
            .then(filterAllowances)
            .then(setToShoppingState);
    }

    function saveAllowances(allowanceResponse) {
        allowances = allowanceResponse.result.itemAllowances;
    }

    function filterAllowances() {
        $scope.displayAllowances = allowanceService.filterAllowances(allowances, $scope.filter.categories, $scope.filter.searchTerm);
        $scope.displayAllowances = supplyUtils.alphabetizeAllowances($scope.displayAllowances);
    }

    function Reset() {
        $scope.filter.searchTerm = "";
        filterAllowances();
    }

    function setToShoppingState() {
        $scope.state = $scope.states.SHOPPING;
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
        if (allowance.selectedQuantity === "more") {
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
        return supplyCart.isItemInCart(item.id)
    };

    $scope.getAllowedQuantities = function (item) {
        var allowedQuantities = allowanceService.getAllowedQuantities(item);
        allowedQuantities.push("more");
        return allowedQuantities;
    };

    /** This is called whenever an items quantity is changed.
     * Used to determine when "more" is selected. */
    $scope.quantityChanged = function (allowance) {
        if (allowance.selectedQuantity === "more") {
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

    $scope.getLocationAutocompleteOptions = function () {
        return locationAutocompleteService.getLocationAutocompleteOptions();
    };

    $scope.resetDestination = function () {
        supplyCart.reset();
        destinationService.reset();
        locationService.go("/supply/order", true);
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
 * Validator for the special order quantity form.
 * Note: The form considers 'e' valid input. I would like to use this validator to mark 'e' as invalid, however
 * this validator is not being called when 'e' characters are entered...
 */
essSupply.directive('wholeNumberValidator', [function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.wholeNumber = function (modelValue, viewValue) {
                return modelValue % 1 === 0 && modelValue !== null;
            };
        }
    }
}]);
